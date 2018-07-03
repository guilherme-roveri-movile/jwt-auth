package com.gbr.http;

import com.gbr.domains.User;
import com.gbr.gateways.jwt.JwtTokenProvider;
import com.gbr.gateways.mongo.UserRepository;
import com.gbr.gateways.spring.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class SignupController {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping
    public void createUser(@RequestBody User user){
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        repository.save(user);
    }

    @GetMapping(path = "/me")
    public UserPrincipal getUser(Authentication authentication){
        return (UserPrincipal) authentication.getPrincipal();
    }
}
