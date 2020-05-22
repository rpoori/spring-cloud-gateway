package com.my.poc.gateway.filters;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;
import java.util.LinkedHashMap;

public class JwtTokenGenerator {

    @Value("${ac.jwt.secret.key}")
    private String jwtSigningKey;

    @Value("${ac.jwt.id}")
    private String acJWTId;

    @Value("${ac.jwt.subject}")
    private String acJWTSubject;

    @Value("${ac.jwt.issuer}")
    private String acJWTIssuer;

    @Value("${ac.jwt.expiration.ttl.mins}")
    private Long acJWTExpirationTtlInMinutes;

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
                .setId(acJWTId)
                .setIssuedAt(now)
                .setSubject(acJWTSubject)
                .setIssuer(acJWTIssuer)
                .claim("auth_token", args[0])
                .claim("permissions", linkedHashMap)
                .signWith(signatureAlgorithm, signingKey);

        long expMillis = nowMillis + acJWTExpirationTtlInMinutes * 60 * 1000;
        Date exp = new Date(expMillis);
        jwtBuilder.setExpiration(exp);

        return jwtBuilder.compact();
    }
}
