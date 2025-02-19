package com.Ahmed.Banking.services.Implementations;

import com.Ahmed.Banking.models.Compte;
import com.Ahmed.Banking.models.Transaction;
import com.Ahmed.Banking.models.Utilisateur;
import com.Ahmed.repositories.CompteRepository;
import com.Ahmed.repositories.TransactionRepository;
import com.Ahmed.repositories.UtilisateurRepository;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Service
public class CompteService {

    private final RestTemplate restTemplate;
    private final CompteRepository compteRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final TransactionRepository transactionRepository;

    private static final String BASE_URL = "https://bankaccountdata.gocardless.com/api/v2";
    private static final String SECRET_ID = "2f7225b9-c4a2-4099-b607-3e4b45013428";  // 🔥 Replace with real credentials
    private static final String SECRET_KEY = "b990a265d0ca0d005b235c7bf1da15cf534e9c2e8822ae51874a87afe4bbb5c8166b6389393a84a46ca69b21d5fc17f7be64e77f50f6e92ad0068cd41c79c95c"; // 🔥 Replace with real credentials
    private static final String SANDBOX_INSTITUTION_ID = "SANDBOXFINANCE_SFIN0000";

    public CompteService(RestTemplate restTemplate, CompteRepository compteRepository,
                         UtilisateurRepository utilisateurRepository, TransactionRepository transactionRepository) {
        this.restTemplate = restTemplate;
        this.compteRepository = compteRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.transactionRepository = transactionRepository;
    }

    public Compte saveCompte(Compte compte) {
        if (compte.getUtilisateur() == null || compte.getUtilisateur().getId() == null) {
            throw new IllegalArgumentException("Le compte doit être associé à un utilisateur !");
        }

        // Vérifier si l'utilisateur existe
        Optional<Utilisateur> utilisateurOpt = utilisateurRepository.findById(compte.getUtilisateur().getId());
        if (utilisateurOpt.isEmpty()) {
            throw new RuntimeException("❌ Utilisateur introuvable !");
        }

        Utilisateur utilisateur = utilisateurOpt.get();

        // ✅ Vérification et conversion avec BigDecimal
        BigDecimal compteBalance = compte.getBalance() != null ? compte.getBalance() : BigDecimal.ZERO;
        BigDecimal utilisateurBalance = utilisateur.getBalance() != null ? utilisateur.getBalance() : BigDecimal.ZERO;

        // ✅ Mettre à jour le solde total
        utilisateurBalance = utilisateurBalance.add(compteBalance);
        utilisateur.setBalance(utilisateurBalance);

        // ✅ Enregistrer l'utilisateur et le compte
        utilisateurRepository.save(utilisateur);
        return compteRepository.save(compte);
    }

    // ✅ OPTION 2: Get All User Accounts
    public List<Compte> getComptesByUtilisateurId(Integer userId) {
        return compteRepository.findByUtilisateurId(userId);
    }

    // ✅ STEP 1: Get Access Token Automatically
    public String getAccessToken() {
        String url = BASE_URL + "/token/new/";
        Map<String, String> payload = Map.of("secret_id", SECRET_ID, "secret_key", SECRET_KEY);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> request = new HttpEntity<>(payload, headers);

        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, request, Map.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            return (String) response.getBody().get("access");
        } else {
            throw new RuntimeException("❌ Failed to get access token");
        }
    }

    // ✅ STEP 2: Automate Account Fetching
    public String automateCompteFetching(Integer userId) {
        String accessToken = getAccessToken();
        String agreementId = createAgreement(accessToken);
        if (agreementId == null) {
            throw new RuntimeException("❌ Failed to create agreement");
        }
        return createRequisition(accessToken, agreementId);
    }

    // ✅ STEP 3: Create an Agreement in GoCardless API
    public String createAgreement(String accessToken) {
        String url = BASE_URL + "/agreements/enduser/";
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

        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, request, Map.class);
        return response.getBody() != null ? (String) response.getBody().get("id") : null;
    }

    public String createRequisition(String accessToken, String agreementId) {
        String url = BASE_URL + "/requisitions/";
        Map<String, Object> payload = Map.of(
                "redirect", "http://www.yourwebpage.com",
                "institution_id", SANDBOX_INSTITUTION_ID,
                "agreement", agreementId,
                "user_language", "EN"
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, request, Map.class);
        if (response.getStatusCode() == HttpStatus.CREATED && response.getBody() != null) {
            String requisitionId = (String) response.getBody().get("id"); // ✅ Store requisition ID
            String authenticationLink = (String) response.getBody().get("link");

            System.out.println("🆔 Requisition ID: " + requisitionId); // ✅ Log this in the backend

            return authenticationLink; // ✅ Send link to user
        }
        throw new RuntimeException("❌ Failed to generate requisition link");
    }

    private Compte fetchAccountDetails(String accessToken, String accountId) {
        // ✅ Étape 1 : Récupération des détails du compte
        String detailsUrl = BASE_URL + "/accounts/" + accountId + "/details/";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<Map> detailsResponse = restTemplate.exchange(detailsUrl, HttpMethod.GET, request, Map.class);

        if (detailsResponse.getStatusCode() != HttpStatus.OK || detailsResponse.getBody() == null) {
            throw new RuntimeException("❌ Impossible de récupérer les détails du compte : " + accountId);
        }

        // ✅ Extraction des données du compte
        Map<String, Object> accountData = (Map<String, Object>) detailsResponse.getBody().get("account");
        if (accountData == null) {
            throw new RuntimeException("❌ Impossible d'extraire les détails du compte : " + accountId);
        }

        String iban = (String) accountData.get("iban");
        String currency = (String) accountData.get("currency");
        String institution = (String) accountData.get("name"); // 🔍 Utilisation de `name` comme institution

        // ✅ Étape 2 : Récupération du solde du compte
        String balanceUrl = BASE_URL + "/accounts/" + accountId + "/balances/";

        ResponseEntity<Map> balanceResponse = restTemplate.exchange(balanceUrl, HttpMethod.GET, request, Map.class);

        if (balanceResponse.getStatusCode() != HttpStatus.OK || balanceResponse.getBody() == null) {
            throw new RuntimeException("❌ Impossible de récupérer le solde du compte : " + accountId);
        }

        List<Map<String, Object>> balances = (List<Map<String, Object>>) balanceResponse.getBody().get("balances");
        BigDecimal balanceAmount = BigDecimal.ZERO;

        if (balances != null && !balances.isEmpty()) {
            Map<String, Object> firstBalance = balances.get(0); // 🔍 Prendre le premier solde
            Map<String, Object> balanceAmountData = (Map<String, Object>) firstBalance.get("balanceAmount");
            if (balanceAmountData != null && balanceAmountData.get("amount") != null) {
                balanceAmount = new BigDecimal(balanceAmountData.get("amount").toString());
            }
        }

        // ✅ Création de l'objet `Compte` avec les données récupérées
        return Compte.builder()
                .externalId(accountId)
                .iban(iban)
                .currency(currency)
                .institution(institution)
                .balance(balanceAmount)
                .build();
    }


    // ✅ STEP 5: Fetch and Save User's Accounts
    public List<String> fetchAndSaveUserAccounts(String requisitionId, Integer userId) {
        String accessToken = getAccessToken();
        String url = BASE_URL + "/requisitions/" + requisitionId;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, request, Map.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            List<String> accounts = (List<String>) response.getBody().get("accounts");

            // 🔍 Vérifier si l'utilisateur existe
            Utilisateur utilisateur = utilisateurRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("❌ Utilisateur introuvable !"));

            // ✅ Initialisation du solde total de l'utilisateur
            if (utilisateur.getBalance() == null) {
                utilisateur.setBalance(BigDecimal.ZERO);
            }

            BigDecimal totalNewBalance = BigDecimal.ZERO; // ✅ Accumulateur pour les nouveaux comptes

            for (String accountId : accounts) {
                // 🔍 Récupérer les détails du compte depuis l'API
                Compte compte = fetchAccountDetails(accessToken, accountId);

                // Associer l'utilisateur au compte
                compte.setUtilisateur(utilisateur);

                // ✅ Ajouter le solde du compte au total des nouveaux comptes
                totalNewBalance = totalNewBalance.add(compte.getBalance());

                // Sauvegarde du compte
                compteRepository.save(compte);
            }

            // ✅ Mise à jour du solde utilisateur avec le total des nouveaux comptes
            utilisateur.setBalance(utilisateur.getBalance().add(totalNewBalance));

            // ✅ Sauvegarde de l'utilisateur avec son nouveau solde
            utilisateurRepository.save(utilisateur);

            return accounts;
        } else {
            throw new RuntimeException("❌ Impossible de récupérer les comptes de l'utilisateur");
        }
    }



    public void fetchAndSaveTransactions(String accessToken, String accountId) {
        String url = BASE_URL + "/accounts/" + accountId + "/transactions/";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, request, Map.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            System.out.println("🔍 Raw API Response: " + response.getBody());

            Map<String, Object> transactionsData = (Map<String, Object>) response.getBody().get("transactions");
            List<Map<String, Object>> bookedTransactions = (List<Map<String, Object>>) transactionsData.get("booked");

            Optional<Compte> compteOptional = compteRepository.findByExternalId(accountId);
            if (compteOptional.isEmpty()) {
                throw new RuntimeException("❌ Compte introuvable !");
            }
            Compte compte = compteOptional.get();

            for (Map<String, Object> transactionData : bookedTransactions) {
                Transaction transaction = new Transaction();
                transaction.setCompte(compte);

                try {
                    transaction.setTransactionDate(LocalDate.parse(transactionData.get("bookingDate").toString()));
                } catch (Exception e) {
                    throw new RuntimeException("❌ Invalid bookingDate format: " + transactionData.get("bookingDate"), e);
                }

                Map<String, Object> transactionAmount = (Map<String, Object>) transactionData.get("transactionAmount");
                if (transactionAmount != null && transactionAmount.containsKey("amount")) {
                    String amountStr = transactionAmount.get("amount").toString().trim();
                    try {
                        BigDecimal amount = new BigDecimal(amountStr.replace(",", ""));
                        transaction.setAmount(amount);
                    } catch (NumberFormatException e) {
                        throw new RuntimeException("❌ Invalid transaction amount format: " + amountStr, e);
                    }
                } else {
                    throw new RuntimeException("❌ Missing transaction amount field");
                }

                transaction.setDescription(transactionData.getOrDefault("remittanceInformationUnstructured", "N/A").toString());

                transactionRepository.save(transaction);
            }
            System.out.println("✅ Transactions successfully imported for account: " + accountId);
        } else {
            throw new RuntimeException("❌ Impossible de récupérer les transactions");
        }
    }

}