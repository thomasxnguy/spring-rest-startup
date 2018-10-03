package com.spring.filters;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.UUID.randomUUID;

@Slf4j
@Component
public class RequestIdFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            Optional<Principal> principal = Optional.ofNullable(request.getUserPrincipal());

            String mdcData = String.format("[requestId:%s, user: %s] ", randomUUID(), principal.map(Principal::getName).orElse("Unknown"));
            if (log.isTraceEnabled()) {
                Enumeration<String> headers = request.getHeaderNames();
                Map<String, List<String>> headersMap = new HashMap<>();
                while (headers.hasMoreElements()) {
                    List<String> headerValues = new ArrayList<>();
                    String header = headers.nextElement();
                    Enumeration<String> values = request.getHeaders(header);
                    while (values.hasMoreElements()) {
                        String value = values.nextElement();
                        headerValues.add(value);
                    }
                    headersMap.put(header, headerValues);
                }
                log.trace("Request with headers [{}]", headersMap);
            }
            MDC.put("mdcData", mdcData); //Variable 'mdcData' is referenced in Spring Boot's logging.pattern.level property
            filterChain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }
}
