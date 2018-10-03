package com.spring.security.impl.propertybased;

import com.spring.security.Authority;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;

public class PropertyBasedUserDetailService implements UserDetailsService {

    private Map<String, String> users = new HashMap<>();
    private Map<String, List<GrantedAuthority>> grantedAuthority = new HashMap<>();

    public PropertyBasedUserDetailService(Map<String, String> users, Map<String, List<Authority>> roles) {
        this.users = users;
        for (String username: roles.keySet()) {
            List<Authority> authorities = roles.get(username);
            authorities = isNull(authorities) ? Collections.emptyList() : authorities;
            grantedAuthority.put(username, authorities.stream().map(Authority::getAuthority).collect(toList()));
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String password = users.get(username);
        if (StringUtils.isEmpty(password)) {
            throw new UsernameNotFoundException("No user " + username);
        }
        return new User(username, password, grantedAuthority.get(username));
    }
}
