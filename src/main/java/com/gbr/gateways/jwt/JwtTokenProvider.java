package com.gbr.gateways.jwt;

import com.gbr.domains.User;
import com.gbr.gateways.exceptions.InvalidTokenException;
import com.gbr.gateways.spring.UserPrincipal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.TextCodec;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
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

    @Value("${users.auth.publicKey}")
    private String pubKey;

    public JwtTokenProvider(final String secret) {
        this.secret = secret;
    }

    public String generateToken(final UserPrincipal user) {
        try {
            Path path = Paths.get(getClass().getClassLoader()
                    .getResource("private_key.pem").toURI());
            byte[] fileBytes = Files.readAllBytes(path);
            String temp = new String(fileBytes);
            temp = temp.replace("-----BEGIN PRIVATE KEY-----\n", "");
            String privateKeyStr = temp.replace("-----END PRIVATE KEY-----", "");

            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(TextCodec.BASE64.decode(privateKeyStr));
            KeyFactory kf = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = kf.generatePrivate(spec);

            final Map<String, Object> claims = new HashMap<>();
            claims.put("username", user.getUsername());
            claims.put("authorities", user.getAuthorities().stream().map(auth -> auth.getAuthority()).collect(Collectors.toList()));
            claims.put("sub", user.getUid());
            return Jwts.builder()
                    .setClaims(claims)
                    .setExpiration(Date.from(LocalDateTime.now().plusYears(1).toInstant(ZoneOffset.UTC)))
                    .signWith(SignatureAlgorithm.RS256, privateKey)
                    .compact();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    public PublicKey getPublicKey() {
        try {
            X509EncodedKeySpec spec =
                    new X509EncodedKeySpec(TextCodec.BASE64.decode(pubKey));
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePublic(spec);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void validateToken(final String token) {
        try {
            Jwts.parser().setSigningKey(getPublicKey()).parseClaimsJws(token);
        } catch (Exception e) {
            throw new InvalidTokenException(e);
        }
    }

    public User getUserFromToken(final String token) {
        validateToken(token);

        final Claims claims = Jwts.parser()
                .setSigningKey(getPublicKey())
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
                .setSigningKey(getPublicKey())
                .parseClaimsJws(token)
                .getBody();
        return claims.getExpiration();
    }

}
