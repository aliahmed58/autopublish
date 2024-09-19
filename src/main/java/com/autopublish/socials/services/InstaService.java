package com.autopublish.socials.services;

import com.autopublish.socials.entities.InstaAcc;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@Service
public class InstaService {

    @Value("${insta.client-id}")
    private String clientId;
    @Value("${insta.client-secret}")
    private String clientSecret;
    @Value("${insta.base-api}")
    private String baseApi;
    @Value("${insta.graph-api}")
    private String graphApi;

    private final HashSet<String> requiredPermissions = new HashSet<>(Arrays.asList(
            "instagram_business_basic", "instagram_business_content_publish"
    ));

    private static final Logger logger = LoggerFactory.getLogger(InstaService.class);

    @Autowired
    EntryService entryService;
    @Autowired
    CustomerService customerService;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ObjectMapper jsonObjectMapper;

    public InstaAcc getUserLongAccessToken(String code) {
        InstaAcc acc = getShortAccessToken(code);
        if (acc == null) {
            throw new RuntimeException("Failed to obtain short access token - see logs");
        }
        final String endpoint = String.format("%s/access_token?grant_type=ig_exchange_token" +
                "&client_secret=%s" +
                "&access_token=%s", graphApi, clientSecret, acc.getShortAccessToken());


        ResponseEntity<String> response = doGet(endpoint);
        if (response == null) {
            logger.error("Error while fetching long access token");
            return null;
        }

        if (response.hasBody()) {
            try {
                JsonNode resultNode = jsonObjectMapper.readTree(response.getBody());

                String longAccessToken = resultNode.get("access_token").asText();
                Long expiresIn = resultNode.get("expires_in").asLong();

                acc.setLongAccessToken(longAccessToken);
                acc.setExpiresIn(expiresIn);

                logger.info("User Id: {} | Long access token: {} | expires in: {}",
                        acc.getUserId(), acc.getLongAccessToken(), acc.getExpiresIn());

                return acc;

            } catch (JsonProcessingException e) {
                logger.error(e.getLocalizedMessage());
            }
        }
        return null;
    }

    public void testPublish(InstaAcc acc) {
        // only JPEGs allowed
        final String testImageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRJn4_6x3ulCCc57xbsUMWurtAKdN6sukxvwA&s";

        // STEP 1 - CREATE CONTAINER
        final String endpoint = String.format("%s/v20.0/%s/media?access_token=%s", graphApi,
                acc.getUserId(), acc.getLongAccessToken());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String jsonBody = "{" +
                "\"image_url\":" + "\"" + testImageUrl + "\"" +
                "}";
        HttpEntity<String> requestEntity = new HttpEntity<>(jsonBody, headers);
        ResponseEntity<String> response = null;
        try {
            response = restTemplate.postForEntity(endpoint, requestEntity,
                    String.class);
        } catch (RestClientException e) {
            logger.error(e.getLocalizedMessage());
        }
        if (response == null) {
            logger.error("Failed to create container");
            return;
        }
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Request failed with status " + response.getStatusCode());
        }
        String creationId = null;
        if (response.hasBody()) {
            try {
                JsonNode resNode = jsonObjectMapper.readTree(response.getBody());
                creationId = resNode.get("id").asText();
            } catch (JsonProcessingException e) {
                logger.error(e.getLocalizedMessage());
            }
        }

        if (creationId == null) {
            throw new RuntimeException("Failed to get creation id");
        }

        final String postEndpoint = String.format("%s/v20.0/%s/media_publish?access_token=%s",
                graphApi,
                acc.getUserId(), acc.getLongAccessToken());

        final String body = "{" +
                "\"creation_id\":" + "\"" + creationId + "\"" +
                "}";
        HttpEntity<String> reqEntity = new HttpEntity<>(body, headers);
        ResponseEntity<String> publishRes = null;
        try {
            publishRes = restTemplate.postForEntity(postEndpoint,
                    reqEntity, String.class);
        } catch (RestClientException e) {
            logger.error(e.getLocalizedMessage());
        }

        if (publishRes == null) {
            throw new RuntimeException("Response for publishing post is null");
        }
        if (publishRes.hasBody()) {
            try {
                JsonNode publishResultNode = jsonObjectMapper.readTree(publishRes.getBody());

                String mediaId = publishResultNode.get("id").asText();

                logger.info("Media published with id {}", mediaId);
            } catch (JsonProcessingException e) {
               logger.error(e.getLocalizedMessage());
            }
        }
    }

    private InstaAcc getShortAccessToken(String code) {
        final String endpoint = String.format("%s/oauth/access_token", baseApi);
        final String redirectUri = "https://localhost/insta/success";

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("client_id", clientId);
        formData.add("client_secret", clientSecret);
        formData.add("grant_type", "authorization_code");
        formData.add("redirect_uri", redirectUri);
        formData.add("code", code);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // Create the request entity
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(formData, headers);

        ResponseEntity<String> response = doPost(endpoint, requestEntity);
        if (response == null) return null;

        if (response.hasBody()) {
            String body = response.getBody();
            try {
                JsonNode resultNode = jsonObjectMapper.readTree(body);
                String res = jsonObjectMapper.writeValueAsString(resultNode);
                InstaAcc acc = jsonObjectMapper.readValue(res, InstaAcc.class);

                boolean hasPermissions = checkUserHasPermissions(acc.getPermissions());
                if (!hasPermissions) {
                    logger.warn("Permissions not granted by user - try again");
                    return null;
                }

                logger.info("Access token: {} | User Id: {} | Permissions Allowed: {}\n",
                        acc.getShortAccessToken(), acc.getUserId(), acc.getPermissions());

                return acc;
            } catch (JsonProcessingException e) {
                logger.error(e.getLocalizedMessage());
            }
        }
        return null;
    }

    private void refreshLongAccessToken(InstaAcc acc) {
        final String endpoint = String.format("%s/refresh_access_token" +
                "?grant_type=ig_refresh_token" +
                "&access_token=%s", graphApi, acc.getLongAccessToken());

        ResponseEntity<String> response = doGet(endpoint);
        if (response == null) {
            logger.error("Error while fetching response");
            throw new NullPointerException("Response was null");
        }

        if (response.hasBody()) {
            try {
                JsonNode resultNode = jsonObjectMapper.readTree(response.getBody());

                String newLongAccessToken = resultNode.get("access_token").asText();
                Long expiresIn = resultNode.get("expires_in").asLong();

                acc.setLongAccessToken(newLongAccessToken);
                acc.setExpiresIn(expiresIn);
            }
            catch (JsonProcessingException e) {
                logger.error(e.getLocalizedMessage());
            }
        }
    }

    private boolean checkUserHasPermissions(List<String> tasks) {
        return new HashSet<>(tasks).containsAll(requiredPermissions);
    }

    private ResponseEntity<String> doGet(String url) {
        ResponseEntity<String> response = null;
        try {
            response = restTemplate.getForEntity(url, String.class);
        } catch (RestClientException e) {
            logger.error(e.getLocalizedMessage());
        }

        if (response == null) {
            throw new RuntimeException("Failed to get response");
        }

        if (!response.getStatusCode().is2xxSuccessful()) {
            logger.error("Request failed with status code {}", response.getStatusCode());
            return null;
        }
        return response;
    }

    private ResponseEntity<String> doPost(String url,
                                          HttpEntity<MultiValueMap<String, String>> requestEntity) {

        ResponseEntity<String> response = null;
        try {
            response = restTemplate.exchange(url, HttpMethod.POST,
                    requestEntity, String.class);
        } catch (HttpClientErrorException e) {
            System.out.println("Response Body: " + e.getResponseBodyAsString() + e.getCause());
            logger.error(e.getLocalizedMessage());
        }

        if (response == null) {
            throw new RuntimeException("Failed to fetch response");
        }

        if (!response.getStatusCode().is2xxSuccessful()) {
            logger.error("Request failed with code {}", response.getStatusCode());
            return null;
        }

        return response;
    }

}
