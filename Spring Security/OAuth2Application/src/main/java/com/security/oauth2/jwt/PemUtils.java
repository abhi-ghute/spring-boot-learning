package com.security.oauth2.jwt;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.*;
import java.util.Base64;

public final class PemUtils {

    private PemUtils() {}

    public static RSAPublicKey readPublicKeyFromString(String pem) throws Exception {
        String cleaned = pem.replaceAll("-----BEGIN PUBLIC KEY-----", "")
                .replaceAll("-----END PUBLIC KEY-----","")
                .replaceAll("\\s","");

        byte[] decoded = Base64.getDecoder().decode(cleaned);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return (RSAPublicKey) kf.generatePublic(spec);
    }

    public static RSAPrivateKey readPrivateKeyFromString(String pem) throws Exception {
        String cleaned = pem.replaceAll("-----BEGIN (?:RSA )?PRIVATE KEY-----", "")
                .replaceAll("-----END (?:RSA )?PRIVATE KEY-----","")
                .replaceAll("\\s","");

        byte[] decoded = Base64.getDecoder().decode(cleaned);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return (RSAPrivateKey) kf.generatePrivate(spec);
    }

    // Optional: read from file if env var contains file path
    public static String readPemFromFileIfPath(String maybePathOrPem) throws IOException {
        if (maybePathOrPem == null) return null;
        File f = new File(maybePathOrPem);
        if (f.exists() && f.isFile()) {
            return new String(java.nio.file.Files.readAllBytes(f.toPath()), StandardCharsets.UTF_8);
        }
        return maybePathOrPem; // assume it's PEM content
    }
}
