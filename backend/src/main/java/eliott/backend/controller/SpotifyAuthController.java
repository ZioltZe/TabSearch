package eliott.backend.controller;

import eliott.backend.model.spotify.SpotifyTokens;
import eliott.backend.security.spotify.SpotifyConfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Base64;
import java.util.Map;

@RestController
@RequestMapping("/spotify/auth")
public class SpotifyAuthController
{
    private final SpotifyConfig spotifyConfig;
    private final RestTemplate restTemplate;

    private static final String URI_AUTHORIZE = "https://accounts.spotify.com/authorize";
    private static final String URI_TOKEN = "https://accounts.spotify.com/api/token";

    /**
     * Following Spotify Authorization: <a href="https://developer.spotify.com/documentation/web-api/tutorials/code-flow">Spotify Auth</a>
     * @param spotifyConfig config class containing client id, secret key and callback uri
     */
    @Autowired
    public SpotifyAuthController(SpotifyConfig spotifyConfig)
    {
        this.spotifyConfig = spotifyConfig;
        this.restTemplate = new RestTemplate();
    }

    /**
     * Spotify API to ask for a token
     */
    @GetMapping("/login")
    public ResponseEntity<String> login()
    {
        // Other Queries
        String scope = "user-read-private user-read-email playlist-read-private";
        String responseType = "code";

        // The Spotify URL
        String authURL = UriComponentsBuilder.fromUriString(URI_AUTHORIZE)
                .queryParam("client_id", spotifyConfig.getClientId())
                .queryParam("response_type", responseType)
                .queryParam("redirect_uri", spotifyConfig.getRedirectUri())
                .queryParam("scope", scope)
                .build().toUriString();

        return ResponseEntity.ok(authURL);
    }

    /**
     * Spotify API to ask for an access token
     * @param code the token from a Spotify API token request
     * @return the access token
     */
    @GetMapping("/callback")
    public ResponseEntity<SpotifyTokens> callback(@RequestParam("code") String code)
    {
        // Body - grant_type, code, redirect_uri
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("code", code);
        body.add("redirect_uri", spotifyConfig.getRedirectUri());

        return sendTokenRequest(body);
    }

    @PostMapping("/refresh")
    public ResponseEntity<SpotifyTokens> refresh(@RequestParam("token") String token)
    {
        // Body - grant_type, code, redirect_uri
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "refresh_token");
        body.add("refresh_token", token);

        return sendTokenRequest(body);
    }

    private ResponseEntity<SpotifyTokens> sendTokenRequest(MultiValueMap<String, String> body)
    {
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, createTokenHeader());
        ResponseEntity<Map<String, Object>> responseEntity = restTemplate.exchange(
                URI_TOKEN,
                HttpMethod.POST,
                requestEntity,
                // Make sure that we know the response is in this format Map<String, Object>
                new ParameterizedTypeReference<>() {}
        );

        if (responseEntity.getStatusCode().is2xxSuccessful())
        {
            Map<String, Object> data = responseEntity.getBody();
            if (data != null)
            {
                String accessToken = (String) data.get("access_token");
                String refreshToken = (String) data.get("refresh_token");
                return ResponseEntity.ok(new SpotifyTokens(accessToken, refreshToken));
            }
            else { return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); }
        }
        else { return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); }
    }

    private HttpHeaders createTokenHeader()
    {
        HttpHeaders header = new HttpHeaders();
        // Header - Content-Type
        header.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        // Header - Authorization
        String credentials = spotifyConfig.getClientId() + ":" + spotifyConfig.getClientSecret();
        String base64Credentials = Base64.getEncoder().encodeToString(credentials.getBytes());
        header.add("Authorization", "Basic " + base64Credentials);

        return header;
    }
}
