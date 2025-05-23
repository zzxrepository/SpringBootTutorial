package com.zzx.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Date;

@Component
public class JwtUtil {
    @Value("${jwt.secret}") private String secret;
    @Value("${jwt.issuer}") private String issuer;
    @Value("${jwt.expire}") private long expireMs;
    private Algorithm algorithm;
    private JWTVerifier verifier;

    @PostConstruct
    public void init() {
        algorithm = Algorithm.HMAC256(secret);
        verifier = JWT.require(algorithm).withIssuer(issuer).build();
    }

    public String generateToken(Long userId) {
        return JWT.create()
            .withIssuer(issuer)
            .withSubject(userId.toString())
            .withExpiresAt(new Date(System.currentTimeMillis() + expireMs))
            .sign(algorithm);
    }

    public Long validateToken(String token) {
        DecodedJWT jwt = verifier.verify(token);
        return Long.valueOf(jwt.getSubject());
    }
}