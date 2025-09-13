package com.security.oauth2.controller;

import com.security.oauth2.jwt.JwtUtils;
import com.security.oauth2.model.AuthorityEnum;
import com.security.oauth2.model.RoleEnum;
import com.security.oauth2.model.UserDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.net.URI;
import java.util.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final String clientId;
    private final String clientSecret;
    private final String authorizeUrl;
    private final String tokenUrl;
    private final String userUrl;
    private final String emailsUrl;
    private final String scope;
    private final String frontendUrl;
    private final JwtUtils jwtUtils;
    private final RestTemplate rest = new RestTemplate();

    private final String stateCookieName;
    private final int stateCookieMaxAge;

    public AuthController(
            @Value("${app.github.client-id}") String clientId,
            @Value("${app.github.client-secret}") String clientSecret,
            @Value("${app.github.authorize-url}") String authorizeUrl,
            @Value("${app.github.token-url}") String tokenUrl,
            @Value("${app.github.user-url}") String userUrl,
            @Value("${app.github.emails-url}") String emailsUrl,
            @Value("${app.github.scope}") String scope,
            @Value("${app.frontend.url}") String frontendUrl,
            JwtUtils jwtUtils,
            @Value("${security.oauth.state-cookie-name:OAUTH2_STATE}") String stateCookieName,
            @Value("${security.oauth.state-cookie-max-age-seconds:300}") int stateCookieMaxAge
    ) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.authorizeUrl = authorizeUrl;
        this.tokenUrl = tokenUrl;
        this.userUrl = userUrl;
        this.emailsUrl = emailsUrl;
        this.scope = scope;
        this.frontendUrl = frontendUrl;
        this.jwtUtils = jwtUtils;
        this.stateCookieName = stateCookieName;
        this.stateCookieMaxAge = stateCookieMaxAge;
    }

    @GetMapping("/github/login")
    public ResponseEntity<Void> redirectToGithub(HttpServletResponse response) {
        String state = UUID.randomUUID().toString();
        Cookie c = new Cookie(stateCookieName, state);
        c.setHttpOnly(true);
        c.setSecure(true);
        c.setPath("/"); // available for callback
        c.setMaxAge(stateCookieMaxAge);
        response.addCookie(c);

        String url = UriComponentsBuilder.fromUriString(authorizeUrl)
                .queryParam("client_id", clientId)
                .queryParam("scope", scope)
                .queryParam("state", state)
                .queryParam("allow_signup", "true")
                .build().toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(url));
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    @GetMapping("/github/callback")
    public ResponseEntity<?> callback(@RequestParam("code") String code,
                                      @RequestParam("state") String state,
                                      HttpServletRequest request,
                                      HttpServletResponse response) {

        // Verify state cookie
        String stateCookieValue = Arrays.stream(Optional.ofNullable(request.getCookies()).orElse(new Cookie[0]))
                .filter(c -> stateCookieName.equals(c.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);

        if (stateCookieValue == null || !stateCookieValue.equals(state)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Invalid state"));
        }

        // Exchange code for access token
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("code", code);

        HttpEntity<MultiValueMap<String,String>> tokenReq = new HttpEntity<>(body, headers);
        Map<String,Object> tokenResp = rest.postForObject(tokenUrl, tokenReq, Map.class);
        if (tokenResp == null || tokenResp.get("access_token") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error","Failed to obtain access_token"));
        }
        String ghAccessToken = tokenResp.get("access_token").toString();

        HttpHeaders ghHeaders = new HttpHeaders();
        ghHeaders.setBearerAuth(ghAccessToken);
        ghHeaders.setAccept(List.of(MediaType.APPLICATION_JSON));
        HttpEntity<Void> ghReq = new HttpEntity<>(ghHeaders);

        Map user = rest.exchange(userUrl, HttpMethod.GET, ghReq, Map.class).getBody();
        // get email (primary)
        String email = (String) user.get("email");
        if (email == null) {
            try {
                List<Map> emails = rest.exchange(emailsUrl, HttpMethod.GET, ghReq, List.class).getBody();
                if (emails != null) {
                    for (Map e : emails) {
                        Boolean primary = (Boolean)e.get("primary");
                        if (Boolean.TRUE.equals(primary)) { email = (String)e.get("email"); break; }
                    }
                }
            } catch (Exception ignored) {}
        }

        String login = (String) user.get("login");
        String name = (String) user.get("name");
        String avatarUrl = (String) user.get("avatar_url");
        String id = String.valueOf(user.get("id"));

        // Build your UserDto (using provided DTO)
        UserDto userDto = new UserDto(
                email != null ? email : (login + "@github"),
                RoleEnum.ROLE_USER,
                "N/A", // password not applicable for OAuth
                List.of(AuthorityEnum.READ_PRIVILEGES)
        );

        // Generate tokens
        String accessToken = jwtUtils.generateAccessToken(userDto);
        String refreshToken = jwtUtils.generateRefreshToken(userDto);

        // Option A (recommended for SPA): return JSON with tokens (frontend stores securely)
        Map<String,Object> resp = new HashMap<>();
        resp.put("access_token", accessToken);
        resp.put("refresh_token", refreshToken);
        resp.put("user", userDto);

        // Option B (more secure): set httpOnly secure cookie for refresh token
        Cookie refreshCookie = new Cookie("refresh_token", refreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true);
        refreshCookie.setPath("/auth/refresh");
       // refreshCookie.setMaxAge((int) (jwtUtils.refreshTokenExpirySeconds())); // expose getter or hardcode
        response.addCookie(refreshCookie);

        // clear state cookie
        Cookie clearState = new Cookie(stateCookieName, "");
        clearState.setPath("/");
        clearState.setMaxAge(0);
        response.addCookie(clearState);

        // redirect to frontend with tokens in fragment (if you prefer), but here return JSON
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody Map<String,String> body) {
        String refreshToken = body.get("refresh_token");
        if (refreshToken == null) return ResponseEntity.badRequest().body(Map.of("error","refresh_token missing"));
        try {
            var claims = jwtUtils.validateAndGetClaims(refreshToken);
            // ensure typ=refresh
            String typ = claims.getStringClaim("typ");
            if (!"refresh".equals(typ)) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error","invalid token type"));

            String email = claims.getSubject();
            // reconstruct user or include necessary claims in refresh token
            RoleEnum role = RoleEnum.valueOf(claims.getStringClaim("role"));
            UserDto user = new UserDto(email, role, "N/A", List.of(AuthorityEnum.READ_PRIVILEGES));
            String newAccess = jwtUtils.generateAccessToken(user);
            return ResponseEntity.ok(Map.of("access_token", newAccess));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error","invalid refresh token"));
        }
    }
}
