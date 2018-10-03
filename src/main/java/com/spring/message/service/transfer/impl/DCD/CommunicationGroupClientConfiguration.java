package com.spring.message.service.transfer.impl.DCD;


import com.spring.message.service.transfer.MessageTransferService;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509ExtendedTrustManager;
import javax.validation.Valid;
import javax.validation.Validator;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Profile("dcd")
@Configuration
@EnableConfigurationProperties(CommunicationGroupClientConfiguration.CommunicationGroupClientConfigurationProperties.class)
public class CommunicationGroupClientConfiguration {

    @Autowired
    private Validator validator;

    @Autowired
    @Valid
    private CommunicationGroupClientConfigurationProperties props;

    @Bean
    public MessageTransferService messageTransferService(@Qualifier("dcdRestClient") RestTemplate restTemplate) throws URISyntaxException {
        return new CommunicationGroupClientMessageTransferService(validator, restTemplate, URI.create(props.getUrl()));
    }

    @PostConstruct
    public void postConstruct() throws KeyManagementException, NoSuchAlgorithmException {
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509ExtendedTrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] x509Certificates, String s) {
                    }
                    @Override
                    public void checkServerTrusted(X509Certificate[] x509Certificates, String s) {
                    }
                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                    @Override
                    public void checkClientTrusted(X509Certificate[] x509Certificates, String s, Socket socket) {
                    }
                    @Override
                    public void checkServerTrusted(X509Certificate[] x509Certificates, String s, Socket socket) {
                    }
                    @Override
                    public void checkClientTrusted(X509Certificate[] x509Certificates, String s, SSLEngine sslEngine) {
                    }
                    @Override
                    public void checkServerTrusted(X509Certificate[] x509Certificates, String s, SSLEngine sslEngine) {
                    }
                }
        };

        SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
    }

    @Bean
    @Qualifier("dcdRestClient")
    public RestTemplate dcdRestClient() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new DcdResponseErrorHandler());
        final String notEncoded = MessageFormat.format("{0}:{1}", props.getServiceId(), props.getPassword());
        final String encodedAuth = Base64.getEncoder().encodeToString(notEncoded.getBytes());
        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
        interceptors.add((request, body, execution) -> {
            request.getHeaders().set(AUTHORIZATION, "Basic " + encodedAuth);
            request.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            return execution.execute(request, body);
        });
        restTemplate.setInterceptors(interceptors);
        return restTemplate;

    }

    @Data
    @ConfigurationProperties(prefix = "dcd.reservation")
    @Validated
    public static class CommunicationGroupClientConfigurationProperties {

        @NotEmpty
        private String serviceId;
        @NotEmpty
        private String password;
        @NotEmpty
        private String url;

    }



}
