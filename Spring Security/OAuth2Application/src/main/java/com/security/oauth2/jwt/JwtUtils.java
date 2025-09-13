package com.security.oauth2.jwt;


import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.*;
import com.security.oauth2.model.UserDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.stereotype.Component;
import org.springframework.security.oauth2.jwt.*;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class JwtUtils {

    private final RSAPrivateKey privateKey;
    private final RSAPublicKey publicKey;
    private final String issuer;
    private final String audience;
    private final long accessTokenExpirySeconds;
    private final long refreshTokenExpirySeconds;

    public JwtUtils(
            @Value("${app.jwt.private-key-pem}") String privateKeyPemRaw,
            @Value("${app.jwt.public-key-pem}") String publicKeyPemRaw,
            @Value("${app.jwt.issuer}") String issuer,
            @Value("${app.jwt.audience}") String audience,
            @Value("${app.jwt.access-token-expiry-seconds}") long accessTokenExpirySeconds,
            @Value("${app.jwt.refresh-token-expiry-seconds}") long refreshTokenExpirySeconds
    ) throws Exception {
        String pkPem = PemUtils.readPemFromFileIfPath(privateKeyPemRaw);
        String pubPem = PemUtils.readPemFromFileIfPath(publicKeyPemRaw);
        this.privateKey = PemUtils.readPrivateKeyFromString(pkPem);
        this.publicKey = PemUtils.readPublicKeyFromString(pubPem);
        this.issuer = issuer;
        this.audience = audience;
        this.accessTokenExpirySeconds = accessTokenExpirySeconds;
        this.refreshTokenExpirySeconds = refreshTokenExpirySeconds;
    }

    public String generateAccessToken(UserDto user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", user.email());
        claims.put("role", user.role().name());
        claims.put("authorities", user.authorities().stream().map(Enum::name).collect(Collectors.toList()));
        // optionally other claims

        return generateToken(user.email(), claims, accessTokenExpirySeconds, "access");
    }

    public String generateRefreshToken(UserDto user) {
        return generateToken(user.email(), Map.of("role", user.role().name()), refreshTokenExpirySeconds, "refresh");
    }

    private String generateToken(String subject, Map<String,Object> claims, long expirySeconds, String typ) {
        try {
            JWSSigner signer = new RSASSASigner(privateKey);
            JWTClaimsSet.Builder cb = new JWTClaimsSet.Builder()
                    .subject(subject)
                    .issuer(issuer)
                    .audience(audience)
                    .issueTime(Date.from(Instant.now()))
                    .expirationTime(Date.from(Instant.now().plusSeconds(expirySeconds)))
                    .claim("typ", typ);

            claims.forEach(cb::claim);
            JWTClaimsSet jwtClaims = cb.build();

            JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.RS256).type(JOSEObjectType.JWT).build();
            SignedJWT signedJWT = new SignedJWT(header, jwtClaims);
            signedJWT.sign(signer);
            return signedJWT.serialize();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public JWTClaimsSet validateAndGetClaims(String token) throws Exception {
        SignedJWT signedJWT = SignedJWT.parse(token);
        JWSVerifier verifier = new RSASSAVerifier(publicKey);
        if (!signedJWT.verify(verifier)) throw new RuntimeException("Invalid signature");
        JWTClaimsSet cs = signedJWT.getJWTClaimsSet();
        Date exp = cs.getExpirationTime();
        if (exp.before(new Date())) throw new RuntimeException("Token expired");
        // optionally validate issuer/audience
        if (!issuer.equals(cs.getIssuer())) throw new RuntimeException("Invalid issuer");
        if (!cs.getAudience().contains(audience)) throw new RuntimeException("Invalid audience");
        return cs;
    }

    // Provide JwtDecoder for Spring Security resource server integration
    public JwtDecoder jwtDecoder() {
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withPublicKey(publicKey).build();

        // add validators (issuer + expiry + audience)
        OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(issuer);
        OAuth2TokenValidator<Jwt> audienceValidator = jwt -> {
            List<String> aud = jwt.getAudience();
            if (aud.contains(audience)) return OAuth2TokenValidatorResult.success();
            return OAuth2TokenValidatorResult.failure(new OAuth2Error("invalid_token", "The required audience is missing", null));
        };
        decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(withIssuer, audienceValidator));
        return decoder;
    }
}