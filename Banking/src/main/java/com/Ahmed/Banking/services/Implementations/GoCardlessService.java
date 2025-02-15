package com.Ahmed.Banking.services.Implementations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class GoCardlessService {

    private final RestTemplate restTemplate;

    @Value("${gocardless.base-url}")
    private String baseUrl;

    @Value("${gocardless.secret-id}")
    private String secretId;

    @Value("${gocardless.secret-key}")
    private String secretKey;

    private static final String SANDBOX_INSTITUTION_ID = "SANDBOXFINANCE_SFIN0000";
    private static final String REDIRECT_URL = "http://www.yourwebpage.com";

    public GoCardlessService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // ✅ Step 1: Get Access Token
    public String getAccessToken() {
        String url = baseUrl + "/token/new/";

        Map<String, String> payload = new HashMap<>();
        payload.put("secret_id", secretId.trim());
        payload.put("secret_key", secretKey.trim());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<Map<String, String>> request = new HttpEntity<>(payload, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, request, Map.class);
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                System.out.println("✅ Access Token Retrieved: " + response.getBody().get("access"));
                return (String) response.getBody().get("access");
            } else {
                System.err.println("❌ Failed to get access token. Status: " + response.getStatusCode());
            }
        } catch (Exception e) {
            System.err.println("❌ Error Getting Access Token: " + e.getMessage());
        }
        return null;
    }

    // ✅ Step 2: Create an Agreement with the Sandbox Bank
    public String createAgreement(String accessToken) {
        String url = baseUrl + "/agreements/enduser/";

        Map<String, Object> payload = Map.of(
                "institution_id", SANDBOX_INSTITUTION_ID,
                "max_historical_days", 90,
                "access_valid_for_days", 30,
                "access_scope", List.of("balances", "details", "transactions")
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, request, Map.class);
            if (response.getStatusCode() == HttpStatus.CREATED && response.getBody() != null) {
                return (String) response.getBody().get("id");
            } else {
                System.err.println("❌ Failed to create agreement. Status: " + response.getStatusCode());
            }
        } catch (Exception e) {
            System.err.println("❌ Error Creating Agreement: " + e.getMessage());
        }
        return null;
    }

    // ✅ Step 3: Generate an Authentication Link (Requisition)
    public String createRequisition(String accessToken, String agreementId) {
        String url = baseUrl + "/requisitions/";

        Map<String, Object> payload = Map.of(
                "redirect", REDIRECT_URL,
                "institution_id", SANDBOX_INSTITUTION_ID,
                "agreement", agreementId,
                "user_language", "EN"
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, request, Map.class);
            if (response.getStatusCode() == HttpStatus.CREATED && response.getBody() != null) {
                return (String) response.getBody().get("link");
            } else {
                System.err.println("❌ Failed to generate authentication link. Status: " + response.getStatusCode());
            }
        } catch (Exception e) {
            System.err.println("❌ Error Generating Authentication Link: " + e.getMessage());
        }
        return null;
    }

    // ✅ Automatically Authenticate the User
    public String authenticateUser() {
        String accessToken = getAccessToken();
        if (accessToken == null) {
            return "❌ Failed to get access token";
        }

        String agreementId = createAgreement(accessToken);
        if (agreementId == null) {
            return "❌ Failed to create agreement";
        }

        String authLink = createRequisition(accessToken, agreementId);
        return authLink != null ? authLink : "❌ Failed to generate authentication link";
    }
}
