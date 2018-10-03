package com.spring.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.utils.ResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
@Component
class CustomBasicAuthenticationEntryPoint extends BasicAuthenticationEntryPoint {

    private final String errorResponse;

    @Autowired
    public CustomBasicAuthenticationEntryPoint(ObjectMapper mapper) {
        String error = null;
        try {
            error = mapper.writeValueAsString(ResponseUtils.error("Unauthorized"));
        } catch (JsonProcessingException e) {
            error = "HTTP Status 401";
        }
        errorResponse = error;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authEx) throws IOException, ServletException {
        response.addHeader("WWW-Authenticate", "Basic realm=" +getRealmName());
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        PrintWriter writer = response.getWriter();
        log.warn("Authentication failed: {}", authEx.getMessage());
        writer.println(errorResponse);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        setRealmName("Linkage API");
        super.afterPropertiesSet();
    }

}
