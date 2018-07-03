package com.gbr.gateways.spring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(
            final HttpServletRequest req,
            final HttpServletResponse res,
            final AuthenticationException e) throws IOException {
        log.error(e.getMessage(), e);
        res.sendError(HttpServletResponse.SC_UNAUTHORIZED);
    }
}
