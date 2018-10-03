package com.spring.security.impl.propertybased;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.userdetails.UserDetailsService;

@Profile("!anyuser")
@Configuration
@EnableConfigurationProperties(PropertyFileUsersConfigurationProperties.class)
public class PropertyBasedSecurityConfiguration {

    @Autowired
    private PropertyFileUsersConfigurationProperties usersAuthorities;

    @Bean
    public UserDetailsService userDetailsService() {
        return new PropertyBasedUserDetailService(usersAuthorities.getUsers(), usersAuthorities.getRoles());
    }

}
