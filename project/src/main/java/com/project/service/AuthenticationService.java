package com.project.service;

import com.project.exceptions.UserNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class AuthenticationService {

    @Autowired
    JwtEncoder encoder;

    private final UserService userService;

    public record MyToken(String token){}

    public MyToken login(Authentication authentication) {

        if(!userService.hasUser(authentication.getName())) throw new UserNotFoundException("User not found.");

        Instant now = Instant.now();
        long expiry = 3600L;

        String scope = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiry))
                .subject(authentication.getName())
                .claim("scope", scope)
                .build();
        return new MyToken(this.encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue());
    }
}
