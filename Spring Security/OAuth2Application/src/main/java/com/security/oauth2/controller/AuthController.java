package com.security.oauth2.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
public class AuthController {

    @Value("${oauth2.github.client-id}")
    private String clientId;

    @Value("${oauth2.github.client-secret}")
    private String clientSecret;

    @Value("${oauth2.github.redirect-uri}")
    private String redirectUri;

//    // Secret key for signing JWT - generate once, keep secret!
//    private final SecretKey jwtSecret = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    private final RestTemplate restTemplate = new RestTemplate();

    // Step 1: Provide URL to frontend to redirect user to GitHub login
    @GetMapping("/oauth2/authorize")
    public String authorize() {
        return "https://github.com/login/oauth/authorize" +
                "?client_id=" + clientId +
                "&redirect_uri=" + redirectUri +
                "&scope=read:user user:email";
    }

//    // Step 2: GitHub redirects here with ?code=...
//    @GetMapping("/oauth2/callback")
//    public ResponseEntity<?> callback(@RequestParam String code) {
//        // Exchange code for access token
//        String accessToken = getAccessToken(code);
//
//        // Use access token to get GitHub user info
//        Map<String, Object> userInfo = getUserInfo(accessToken);
//
////        // Create JWT with user info (stateless auth token)
////        String jwt = createJwtToken(userInfo);
//
//        // Return JWT to client (in real app, probably JSON response)
//        return ResponseEntity.ok(Map.of(
//                //"token", jwt,
//                "user", userInfo
//        ));
//    }

    private Map<String, Object> getAccessToken(String code) {
        String url = "https://github.com/login/oauth/access_token";

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(MediaType.parseMediaTypes("application/json"));

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("code", code);
        body.add("redirect_uri", redirectUri);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

        Map<String, Object> responseBody = response.getBody();

        if (responseBody == null || !responseBody.containsKey("access_token")) {
            throw new RuntimeException("Failed to get access token");
        }

        return responseBody;
    }

    private Map<String, Object> getUserInfo(String accessToken) {
        String url = "https://api.github.com/user";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, request, Map.class);

        return response.getBody();
    }

//    private String createJwtToken(Map<String, Object> userInfo) {
//        long nowMillis = System.currentTimeMillis();
//        long expMillis = nowMillis + 3600_000; // 1 hour expiry
//
//        return Jwts.builder()
//                .setSubject(userInfo.get("login").toString())
//                .setIssuedAt(new Date(nowMillis))
//                .setExpiration(new Date(expMillis))
//                .claim("name", userInfo.get("name"))
//                .claim("avatar_url", userInfo.get("avatar_url"))
//                .signWith(jwtSecret)
//                .compact();
//    }
}
