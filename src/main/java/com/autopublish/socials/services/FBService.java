package com.autopublish.socials.services;

import com.autopublish.socials.entities.*;
import com.autopublish.socials.repositories.CustomerFBPageRepository;
import com.autopublish.socials.repositories.FBPageRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.function.ServerRequest;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class FBService {

    private static final Logger logger = LoggerFactory.getLogger(FBService.class);
    private final HashSet<String> requiredPermissions = new HashSet<>(Arrays.asList(
            "CREATE_CONTENT", "MANAGE", "MODERATE"
    ));
    @Autowired
    EntryService entryService;
    @Autowired
    CustomerService customerService;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private FBPageRepository pageRepository;

    @Autowired
    private CustomerFBPageRepository customerFBPageRepository;

    @Autowired
    private ObjectMapper jsonObjectMapper;

    @Value("${facebook.appId}")
    private String appId;
    @Value("${facebook.appSecret}")
    private String appSecret;
    @Value("${facebook.graph-api-url}")
    private String facebookGraphApiUri;


    // Long-lived user access tokens have expiry of 60 days currently.
    // Long-lived page access tokens have no expiry. (can be revoked due to other security reasons tho)

    /**
     * ----------------------------- Step 1 ----------------------------------------
     * Send a request to https://graph.facebook.com/{api-version}/oauth/access_token?
     * grant_type=fb_exchange_token&
     * client_id={app+id}&
     * client_secret={app_secret}&
     * fb_exchange_token={access-token}
     * Which gives the response in the following format:
     * {
     * "access_token":"{long-lived-user-access-token}",
     * "token_type": "bearer",
     * "expires_in": 5183944            //The number of seconds until the token expires
     * }
     *
     * @param fbLoginResponse initial json response from facebook login, contains the short-lived access token in
     *                        authResponse.accessToken
     */
    @Async
    public void getUserLongAccessToken(String fbLoginResponse, String username) throws JsonProcessingException {
        Customer currentCustomer = customerService.getByUsername(username);
        assert currentCustomer != null;
        logger.info("1. FB Login through client {}", fbLoginResponse);
        logger.info("2. Fetching long-lived user access token");
        JsonNode jsonNode = jsonObjectMapper.readTree(fbLoginResponse);

        String shortAccessToken = jsonNode.get("authResponse").get("accessToken").asText();
        String fbUserId = jsonNode.get("authResponse").get("userID").asText();

        final String oAuthUri = facebookGraphApiUri + "/oauth/access_token?" +
                "grant_type=fb_exchange_token" +
                "&client_id=" + appId +
                "&client_secret=" + appSecret +
                "&fb_exchange_token=" + shortAccessToken;

        try {
            String result = restTemplate.getForObject(oAuthUri, String.class);
            JsonNode resultNode = jsonObjectMapper.readTree(result);

            logger.info("Long-lived UserAccessToken --> {}",
                    result);

            String longLivedUserAccessToken = resultNode.get("access_token").asText();
            currentCustomer.setFbUserAccessToken(longLivedUserAccessToken);
            getPageLongAccessToken(longLivedUserAccessToken,
                    fbUserId, currentCustomer);

            currentCustomer.setFbUserId(fbUserId);

            customerService.update(currentCustomer);
        } catch (Exception e) {
            logger.info(e.getLocalizedMessage());
        }
    }

    // check if user has permissions that allows us to post on his behalf
    // These permissions are in the ```data.tasks``` array in response
    public boolean checkUserHasPermissions(List<String> tasks) {
        if (new HashSet<>(tasks).containsAll(requiredPermissions)) {
            return true;
        }
        return false;
    }

    // returns Long Lived Page token that has no expiry - also returns page data which should be accessed here and saved
    // like the page id
    public void getPageLongAccessToken(String longLivedUserAccessToken, String fbUserId,
                                         Customer customer) throws JsonProcessingException {
        logger.info("Fetching long-lived Page Access Token");
        final String oAuthUri = facebookGraphApiUri + "/" + fbUserId + "/accounts?" +
                "access_token=" + longLivedUserAccessToken;

        try {
            String result = restTemplate.getForObject(oAuthUri, String.class);
            logger.info("Long-lived page access token response {}", result);
            JsonNode resultNode = jsonObjectMapper.readTree(result);
            JsonNode dataArray = resultNode.get("data");

            Set<CustomerFBPage> fbpages = new HashSet<>();
            if (dataArray.isArray()) {
                for (JsonNode node : dataArray) {
                    CustomerFBPage customerFBPages = new CustomerFBPage();
                    ((ObjectNode) node).remove("category_list");
                    String accessToken = node.get("access_token").textValue();
                    ((ObjectNode) node).remove("access_token");
                    String res = jsonObjectMapper.writeValueAsString(node);
                    FBPage page = jsonObjectMapper.readValue(res, FBPage.class);
                    boolean hasPermissionsRequired = this.checkUserHasPermissions(page.getTasks());
                    if (hasPermissionsRequired) {
                        customerFBPages.setCustomer(customer);
                        customerFBPages.setAccessToken(accessToken);
                        customerFBPages.setPage(page);
                        fbpages.add(customerFBPages);
                    } else {
                        logger.warn("User with {} does not have enough permissions to manage {} :" +
                                " {}", fbUserId, page.getName(), page.getPageId());
                    }
                }
            }
            customer.setCustomerPages(fbpages);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private LocalDateTime userAccessTokenExpiry(Long seconds) {
        // calculate from now + seconds to expiry for the long-lived user access token.
        // long-lived page access token have no expiry
        return LocalDateTime.now().plusSeconds(seconds);
    }

    @Async
    public void publishPost(List<Entry> entries, Customer customer) throws JsonProcessingException {
        // Get all pages where publish is enabled
        List<CustomerFBPage> customerFBPages =
                customerFBPageRepository.findByCustomerAndPublishEnabled(customer, true);

        // for every page that is enabled, publish entries
        for (CustomerFBPage customerFBPage : customerFBPages) {
            String pageId = customerFBPage.getPage().getPageId();

            String publishUri = facebookGraphApiUri + "/" + pageId + "/feed?access_token=" +
                    customerFBPage.getAccessToken();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

             for (Entry entry : entries) {
                 String message = "I got this recommendation on Reach150: \n" +
                         entry.getDescription() + " with a rating of: " + entry.getRating();
                String postBody = "{\"message\": \"" + entry.getDescription() + "\"" +
                        ",\"published\": \"true\" }";
                HttpEntity<String> entity = new HttpEntity<>(postBody, headers);
                ResponseEntity<String> response = restTemplate.postForEntity(publishUri, entity,
                        String.class);
                if (response.hasBody()) {
                    System.out.println(response.getBody());
                }
                else {
                    System.out.println(response.getStatusCode());
                }
            }
        }

    }

    @Async
    public void publishPhoto(List<FBPage> pages, Customer customer) {
    }

    // current response does not include token expiry, as opposed to written in facebook docs
    // doing a /me graph api and {user_id}/accounts hit to find out if the tokens are valid


    public List<FBPage> getPagesByCustomer(Customer customer) {
        List<CustomerFBPage> customerFBPages = customerFBPageRepository.findByCustomer(customer);
        List<FBPage> pages = new ArrayList<>();
        for (CustomerFBPage customerFBPage : customerFBPages) {
            pages.add(customerFBPage.getPage());
        }
        return pages;
    }

    public FBPage getPageById(String id) {
        return pageRepository.findByPageId(id).orElse(null);
    }

    public void setPublishingEnabled(Set<CustomerFBPage> pages, Customer customer) {
        for (CustomerFBPage page : pages) {
            String pageId = page.getPage().getPageId();
            String customerId = customer.getUsername();
            CustomerPageId customerPageId = new CustomerPageId(customerId, pageId);
            CustomerFBPage record = customerFBPageRepository.findById(customerPageId).orElse(null);
            if (record != null) {
                record.setPublishEnabled(page.isPublishEnabled());
                customerFBPageRepository.save(record);
            }
        }
        customerFBPageRepository.flush();
    }

}