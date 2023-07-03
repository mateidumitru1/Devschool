package com.project.handlers;

import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.interfaces.RSAPublicKey;

@Service
public class JwtHandler {

    @Value("${jwt.public.key}")
    private RSAPublicKey key;

    public String getSubject(String token) {

        token = token.replace("Bearer ", "");
        return Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
