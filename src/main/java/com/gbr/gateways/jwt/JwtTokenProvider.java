package com.gbr.gateways.jwt;

import com.gbr.domains.User;
import com.gbr.gateways.exceptions.InvalidTokenException;
import com.gbr.gateways.spring.UserPrincipal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class JwtTokenProvider {

    private final String secret;

    public JwtTokenProvider(final String secret) {
        this.secret = secret;
    }

    public String generateToken(final UserPrincipal user) {
        final Map<String, Object> claims = new HashMap<>();
        claims.put("username", user.getUsername());
        claims.put("authorities", user.getAuthorities().stream().map(auth->auth.getAuthority()).collect(Collectors.toList()));
        claims.put("sub", user.getUid());
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(Date.from(LocalDateTime.now().plusYears(1).toInstant(ZoneOffset.UTC)))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public void validateToken(final String token) {
        try {
            Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
        } catch (RuntimeException e) {
            throw new InvalidTokenException(e);
        }
    }

    public User getUserFromToken(final String token) {
        validateToken(token);
        final Claims claims = Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
        final String username = claims.get("username").toString();
        final String uid = claims.getSubject();
        final ArrayList authorities = (ArrayList) claims.get("authorities");
        final User user = new User();
        user.setUid(uid);
        user.setUsername(username);
        user.setAuthorities(authorities);
        return user;
    }

    public java.util.Date getExp(final String token) {
        validateToken(token);
        final Claims claims = Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
        return claims.getExpiration();
    }

}
