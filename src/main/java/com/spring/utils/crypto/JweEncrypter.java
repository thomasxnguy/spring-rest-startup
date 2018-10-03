package com.spring.utils.crypto;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jose.crypto.DirectDecrypter;
import com.nimbusds.jose.crypto.DirectEncrypter;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.text.ParseException;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;


/**
 * public static void main(String[] args) throws NoSuchAlgorithmException {
 * KeyGenerator keyGen = KeyGenerator.getInstance("AES");
 * keyGen.init(256);
 * SecretKey key = keyGen.generateKey();
 * String encodedKey = Base64.getEncoder().encodeToString(key.getEncoded());
 * System.out.println(encodedKey);
 * }
 */
@Slf4j
@Component
public class JweEncrypter {

    private final JWEHeader header = new JWEHeader(JWEAlgorithm.DIR, EncryptionMethod.A256GCM);

    private final DirectEncrypter encrypter;
    private final DirectDecrypter decrypter;

    private static final String CLAIM_PHONE = "phone";

    private final int expirationMins;

    public JweEncrypter(@Value("${encrypter.key}") String encodedKey, @Value("${encrypter.expire.mins:30}") int expirationMins) throws KeyLengthException {
        byte[] decodedKey = Base64.getDecoder().decode(encodedKey);
        SecretKey secretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
        encrypter = new DirectEncrypter(secretKey);
        decrypter = new DirectDecrypter(secretKey);
        this.expirationMins = expirationMins;
    }

    public Token encrypt(String body) {

        Calendar now = Calendar.getInstance();
        now.add(Calendar.MINUTE, expirationMins);
        Date expire = now.getTime();

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder().expirationTime(expire).claim(CLAIM_PHONE, body).build();
        EncryptedJWT jwt = new EncryptedJWT(header, claimsSet);

        try {
            jwt.encrypt(encrypter);
            return new Token(jwt.serialize(), expire);
        } catch (JOSEException e) {
            throw new RuntimeException("Unable to crypto payload", e);
        }

    }

    public String decrypt(String jweString) throws JweEncrypterException {
        try {
            EncryptedJWT jwt = EncryptedJWT.parse(jweString);
            jwt.decrypt(decrypter);
            if (DateUtils.isAfter(new Date(), jwt.getJWTClaimsSet().getExpirationTime(), 0)) {
                throw new JweEncrypterException("Expired JWT");
            }
            return jwt.getJWTClaimsSet().getClaim(CLAIM_PHONE).toString();
        } catch (JOSEException | ParseException e) {
            log.error("Failed to decrypt {}", e.getMessage());
            throw new JweEncrypterException(e.getMessage());
        }
    }

    public static class JweEncrypterException extends Exception {
        public JweEncrypterException(String message) {
            super(message);
        }
    }

    @lombok.Value
    public static class Token {
        private final String token;
        private final Date expire;
    }

}
