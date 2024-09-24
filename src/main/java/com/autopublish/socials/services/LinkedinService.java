package com.autopublish.socials.services;

import com.autopublish.socials.entities.LinkedinAccessToken;
import com.autopublish.socials.entities.LinkedinUserInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Set;

@Service
public class LinkedinService {

    @Value("${linkedin.client-id}")
    private String clientId;

    @Value("${linkedin.client-secret}")
    private String clientSecret;

    @Value("${linkedin.redirect-uri}")
    private String redirectUri;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private final static Logger logger = LoggerFactory.getLogger(LinkedinService.class);

    // constants - preferably in a diff class
    private final String AUTH_BASE_URL = "https://www.linkedin.com/oauth/v2/authorization";
    private final String ACCESS_TOKEN_URL = "https://www.linkedin.com/oauth/v2/accessToken";
    private final String USER_INFO_URL = "https://api.linkedin.com/v2/userinfo";

    private final String GRANT_TYPE = "authorization_code";

    public LinkedinAccessToken getAccessToken(String authCode) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", GRANT_TYPE);
        params.add("code", authCode);
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("redirect_uri", redirectUri);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(ACCESS_TOKEN_URL,
                    requestEntity ,String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                LinkedinAccessToken accessToken = objectMapper.readValue(response.getBody(),
                        LinkedinAccessToken.class);

                logger.info("Body: {}", response.getBody());
                return accessToken;
            }

        } catch (RestClientException | JsonProcessingException e) {
            logger.error(e.getLocalizedMessage());
        }

        return null;

    }

    public LinkedinUserInfo getUserProfile(LinkedinAccessToken tokenObj) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + tokenObj.getAccessToken());

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(USER_INFO_URL,
                    HttpMethod.GET, requestEntity, String.class);

            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                LinkedinUserInfo userProfile = objectMapper.readValue(responseEntity.getBody(),
                        LinkedinUserInfo.class);

                userProfile.setUrn();
                logger.info("Profile obj: {}", userProfile);
                return userProfile;
            }
        } catch (RestClientException | JsonProcessingException e) {
            logger.error(e.getLocalizedMessage());
        }

        return null;
    }

    public String getOauth20Url(Set<String> scopes, String state) throws UnsupportedEncodingException {
        StringBuilder builder = new StringBuilder(AUTH_BASE_URL);

        String scopeString = getScopeString(scopes);

        builder.append("?response_type=code")
                .append("&client_id=").append(clientId)
                .append("&redirect_uri=").append(redirectUri)
                .append("&state=").append(state)
                .append("&scope=").append(URLEncoder.encode(scopeString,
                        String.valueOf(StandardCharsets.UTF_8)));

        return builder.toString();
    }

    private String getScopeString(Set<String> scope) {
        if (scope.isEmpty()) {
            throw new RuntimeException("Scopes cannot be empty");
        }
        StringBuilder scopeBuilder = new StringBuilder();

        for (String s : scope) {
           scopeBuilder.append(s).append(" ");
        }

        return scopeBuilder.toString();
    }
}

