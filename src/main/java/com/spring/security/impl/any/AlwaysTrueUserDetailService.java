package com.spring.security.impl.any;

import com.spring.security.Authority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Provides valid user with password <i>password</i> and all credentials
 * This bean should be used ONLY in dev purposes
 */
@Profile("anyuser")
@Service
class AlwaysTrueUserDetailService implements UserDetailsService {

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AlwaysTrueUserDetailService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String s) {
        return new User(s, passwordEncoder.encode("password"), Authority.allAuthorities());
    }

}
