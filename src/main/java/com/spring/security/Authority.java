package com.spring.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Arrays;
import java.util.Collection;

import static java.util.stream.Collectors.toList;

public enum Authority {

    ACTUATOR,
    FEATURE_ROLE,
    ID_BY_PHONE,
    PHONE_BY_ID,
    ID_BY_HASH,
    SEND_MESSAGE;

    private final SimpleGrantedAuthority grantedAuthority;

    Authority() {
        this.grantedAuthority = new SimpleGrantedAuthority(name());
    }

    public static Collection<GrantedAuthority> allAuthorities() {
        return Arrays.stream(values()).map(Authority::getAuthority).collect(toList());
    }

    public GrantedAuthority getAuthority() {
        return grantedAuthority;
    }

}
