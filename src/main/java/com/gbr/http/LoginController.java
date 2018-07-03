package com.gbr.http;

import com.gbr.gateways.jwt.JwtTokenProvider;
import com.gbr.gateways.spring.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/auth", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class LoginController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider = new JwtTokenProvider("mySecret");

    @PostMapping(value = "/token")
    public LoginResponse generateToken(@RequestBody final LoginRequest loginRequest) {
        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        final UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        final String token = tokenProvider.generateToken(principal);
        LoginResponse response = new LoginResponse();
        response.setAccessToken(token);
        return response;
    }
}
