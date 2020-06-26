package com.my.poc.gateway.filters;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;
import java.util.LinkedHashMap;

public class JwtTokenGenerator {

    @Value("${jwt.signing.key}")
    private String jwtSigningKey;

    @Value("${jwt.id}")
    private String jwtId;

    @Value("${jwt.subject}")
    private String jwtSubject;

    @Value("${jwt.issuer}")
    private String jwtIssuer;

    @Value("${jwt.expiration.ttl.mins}")
    private Long jwtExpirationTtlInMinutes;

    @SneakyThrows
    public String generate(Object... args) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        byte[] apiKeySecretBytes = jwtSigningKey.getBytes();
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        if(args.length > 1) {
            linkedHashMap.put("permissions", args[1]);
        }

        JwtBuilder jwtBuilder = Jwts.builder()
                .setId(jwtId)
                .setIssuedAt(now)
                .setSubject(jwtSubject)
                .setIssuer(jwtIssuer)
                .claim("auth_token", args[0])
                .claim("permissions", linkedHashMap)
                .signWith(signatureAlgorithm, signingKey);

        long expMillis = nowMillis + jwtExpirationTtlInMinutes * 60 * 1000;
        Date exp = new Date(expMillis);
        jwtBuilder.setExpiration(exp);

        return jwtBuilder.compact();
    }
}
