package com.Ahmed.Banking.services.Implementations;

import com.Ahmed.Banking.models.Compte;
import com.Ahmed.Banking.models.PartCompte;
import com.Ahmed.Banking.models.Transaction;
import com.Ahmed.Banking.models.Utilisateur;
import com.Ahmed.repositories.CompteRepository;
import com.Ahmed.repositories.PartCompteRepository;
import com.Ahmed.repositories.TransactionRepository;
import com.Ahmed.repositories.UtilisateurRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;

@Service
public class CompteService {

    private final RestTemplate restTemplate;
    private final CompteRepository compteRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final TransactionRepository transactionRepository;
    private final PartCompteRepository partCompteRepository; // ✅ Ajout du repository


    private static final String BASE_URL = "https://bankaccountdata.gocardless.com/api/v2";
    private static final String SECRET_ID = "2f7225b9-c4a2-4099-b607-3e4b45013428";  // 🔥 Replace with real credentials
    private static final String SECRET_KEY = "b990a265d0ca0d005b235c7bf1da15cf534e9c2e8822ae51874a87afe4bbb5c8166b6389393a84a46ca69b21d5fc17f7be64e77f50f6e92ad0068cd41c79c95c"; // 🔥 Replace with real credentials
    private static final String SANDBOX_INSTITUTION_ID = "SANDBOXFINANCE_SFIN0000";

    public CompteService(RestTemplate restTemplate, CompteRepository compteRepository,
                         UtilisateurRepository utilisateurRepository, TransactionRepository transactionRepository, PartCompteRepository partCompteRepository) {
        this.restTemplate = restTemplate;
        this.compteRepository = compteRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.transactionRepository = transactionRepository;
        this.partCompteRepository = partCompteRepository;
    }

    public Compte saveCompte(Compte compte) {
        if (compte.isConjoint()) {
            return saveJointAccount(compte);
        } else {
            // Pour un compte individuel, on s'assure que le solde est correctement initialisé
            compte.setBalance(Optional.ofNullable(compte.getBalance()).orElse(BigDecimal.ZERO));
            Compte savedCompte = compteRepository.save(compte);

            // Mise à jour de la balance de l'utilisateur associé
            Utilisateur utilisateur = savedCompte.getUtilisateur();
            if (utilisateur != null) {
                utilisateur.mettreAJourBalance();
                utilisateurRepository.save(utilisateur);
            }
            return savedCompte;
        }
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
    public Map<String, String> automateCompteFetching(Integer userId) {
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

    public Map<String, String> createRequisition(String accessToken, String agreementId) {
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
            String requisitionId = (String) response.getBody().get("id");  // ✅ Récupérer l'ID de la réquisition
            String authenticationLink = (String) response.getBody().get("link");

            System.out.println("🆔 Requisition ID: " + requisitionId);  // ✅ Log pour déboguer

            // ✅ Retourner à la fois l'authLink et le requisitionId
            Map<String, String> result = new HashMap<>();
            result.put("authLink", authenticationLink);
            result.put("requisitionId", requisitionId);
            return result;
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


    public List<String> fetchAndSaveUserAccounts(String requisitionId, Integer userId) {
        String accessToken = getAccessToken();
        String url = BASE_URL + "/requisitions/" + requisitionId;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, request, Map.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            List<String> accounts = (List<String>) response.getBody().get("accounts");

            Utilisateur utilisateur = utilisateurRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("❌ Utilisateur introuvable !"));

            if (utilisateur.getBalance() == null) {
                utilisateur.setBalance(BigDecimal.ZERO);
            }

            BigDecimal totalNewBalance = BigDecimal.ZERO;

            for (String accountId : accounts) {
                // ✅ Vérifier si le compte existe déjà par son externalId
                if (compteRepository.findByExternalId(accountId).isPresent()) {
                    System.out.println("⚠️ Le compte avec externalId " + accountId + " existe déjà, il ne sera pas ajouté.");
                    continue; // ⏭ Passer au compte suivant
                }

                // 🔍 Récupérer les détails du compte depuis GoCardless
                Compte compte = fetchAccountDetails(accessToken, accountId);

                // Associer l'utilisateur au compte
                compte.setUtilisateur(utilisateur);
                totalNewBalance = totalNewBalance.add(compte.getBalance());

                // ✅ Sauvegarde du compte uniquement si `externalId` est unique
                compteRepository.save(compte);
            }

            // ✅ Mise à jour du solde utilisateur
            utilisateur.setBalance(utilisateur.getBalance().add(totalNewBalance));
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
    @Transactional
    public Compte creerCompteConjoint(String nom, String externalId, String institution,
                                      String iban, String currency, BigDecimal balance,
                                      Integer proprietaireId, List<String> emailsUtilisateurs,
                                      List<BigDecimal> partsMontants) {
        // 1. Création du compte conjoint
        Compte compte = new Compte(nom, externalId, institution, iban, currency, balance, true);
        Utilisateur proprietaire = utilisateurRepository.findById(proprietaireId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        compte.setUtilisateur(proprietaire);
        compte = compteRepository.save(compte);

        // 2. Calculer la somme des parts attribuées aux co-utilisateurs
        BigDecimal totalCoUserPercentage = partsMontants.stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Vérifier que la somme des parts des co-utilisateurs est inférieure à 100%
        if(totalCoUserPercentage.compareTo(BigDecimal.valueOf(100)) >= 0) {
            throw new RuntimeException("La somme des parts des co-utilisateurs doit être inférieure à 100%");
        }

        // 3. Calculer la part du propriétaire
        BigDecimal ownerPercentage = BigDecimal.valueOf(100).subtract(totalCoUserPercentage);

        // 4. Créer une PartCompte pour le propriétaire
        PartCompte ownerPart = new PartCompte(proprietaire, compte, ownerPercentage);
        partCompteRepository.save(ownerPart);

        // 5. Créer les PartCompte pour chaque co-utilisateur
        for (int i = 0; i < emailsUtilisateurs.size(); i++) {
            Utilisateur coUser = utilisateurRepository.findByEmail(emailsUtilisateurs.get(i))
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
            BigDecimal coUserPercentage = partsMontants.get(i);
            PartCompte coUserPart = new PartCompte(coUser, compte, coUserPercentage);
            partCompteRepository.save(coUserPart);
            // Mettre à jour la balance de chaque co-utilisateur
            coUser.mettreAJourBalance();
            utilisateurRepository.save(coUser);
        }

        // 6. Recalculer et sauvegarder la balance du propriétaire
        proprietaire.mettreAJourBalance();
        utilisateurRepository.save(proprietaire);

        return compte;
    }




    @Transactional
    public void recalculerPartProprietaire(Compte compte) {
        List<PartCompte> parts = partCompteRepository.findByCompteId(compte.getId());

        // ✅ Somme des parts déjà attribuées aux autres utilisateurs
        BigDecimal sommeParts = parts.stream()
                .filter(p -> !p.getUtilisateur().getId().equals(compte.getUtilisateur().getId())) // Exclure le créateur
                .map(PartCompte::getPourcentage)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // ✅ Vérifier qu'on ne dépasse pas 100%
        if (sommeParts.compareTo(new BigDecimal("100")) > 0) {
            throw new RuntimeException("❌ La somme des parts ne peut pas dépasser 100% !");
        }

        // ✅ Mise à jour de la part du créateur
        Optional<PartCompte> partOpt = partCompteRepository.findByCompteIdAndUtilisateurId(compte.getId(), compte.getUtilisateur().getId());

        PartCompte partProprietaire = partOpt.orElseGet(() -> {
            PartCompte nouvellePart = PartCompte.builder()
                    .compte(compte)
                    .utilisateur(compte.getUtilisateur())
                    .pourcentage(BigDecimal.ZERO) // ✅ Mettre 0% temporairement
                    .build();
            return partCompteRepository.save(nouvellePart);
        });

        BigDecimal nouvellePart = new BigDecimal("100").subtract(sommeParts);
        partProprietaire.setPourcentage(nouvellePart);
        partCompteRepository.save(partProprietaire);
    }


    @Transactional
    public void mettreAJourBalancesUtilisateurs(Compte compte) {
        if (!compte.isConjoint() || compte.getParts() == null || compte.getParts().isEmpty()) {
            return;
        }

        // On travaille sur une copie de la liste pour éviter les modifications concurrentes
        List<PartCompte> parts = new ArrayList<>(compte.getParts());
        for (PartCompte part : parts) {
            Utilisateur utilisateur = part.getUtilisateur();
            if (utilisateur != null) {
                // Au lieu d'écraser la balance avec la part du compte conjoint,
                // on recalcule la balance totale de l'utilisateur
                utilisateur.mettreAJourBalance();
                utilisateurRepository.save(utilisateur);
            }
        }
    }



    /**
     * ✅ Ajoute de nouveaux utilisateurs à un compte conjoint et recalcule les parts.
     */
    @Transactional
    public void ajouterUtilisateurCompteConjoint(Integer compteId, String email, BigDecimal partMontant) {
        // Vérifier que le compte est bien conjoint
        Compte compte = compteRepository.findById(compteId)
                .orElseThrow(() -> new RuntimeException("❌ Compte introuvable !"));
        if (!compte.isConjoint()) {
            throw new RuntimeException("❌ Ce compte n'est pas un compte conjoint !");
        }

        // Vérifier l'existence de l'utilisateur
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("❌ Utilisateur avec l'e-mail " + email + " introuvable !"));

        // Vérifier que l'utilisateur n'est pas déjà associé
        Optional<PartCompte> partCompteOpt = partCompteRepository.findByCompteIdAndUtilisateurId(compte.getId(), utilisateur.getId());
        if (partCompteOpt.isPresent()) {
            throw new RuntimeException("⚠️ Cet utilisateur fait déjà partie du compte !");
        }

        // Ajouter la nouvelle part
        PartCompte nouvellePart = PartCompte.builder()
                .compte(compte)
                .utilisateur(utilisateur)
                .pourcentage(partMontant)
                .build();
        partCompteRepository.save(nouvellePart);

        // Recalculer la part du propriétaire et mettre à jour les balances
        recalculerPartProprietaire(compte);
        mettreAJourBalancesUtilisateurs(compte);

        System.out.println("✅ Utilisateur " + utilisateur.getEmail() + " ajouté au compte " + compte.getNom());
    }






    private Compte saveJointAccount(Compte compte) {
        if (compte.getUtilisateur() == null) {
            throw new RuntimeException("Un compte conjoint doit avoir un propriétaire.");
        }

        // S'assurer que le solde est défini
        compte.setBalance(Optional.ofNullable(compte.getBalance()).orElse(BigDecimal.ZERO));
        compte = compteRepository.save(compte);

        // Si aucune part n'existe, créer une part par défaut pour le propriétaire à 100%
        if (compte.getParts() == null || compte.getParts().isEmpty()) {
            PartCompte defaultPart = new PartCompte(compte.getUtilisateur(), compte, BigDecimal.valueOf(100));
            partCompteRepository.save(defaultPart);
        }

        // Récupérer la liste actualisée des parts
        List<PartCompte> parts = partCompteRepository.findByCompteId(compte.getId());

        // Mettre à jour la balance de chaque utilisateur associé au compte conjoint
        for (PartCompte part : parts) {
            Utilisateur utilisateur = part.getUtilisateur();
            if (utilisateur != null) {
                // Ici, on peut choisir de recalculer toute la balance de l'utilisateur
                utilisateur.mettreAJourBalance();
                utilisateurRepository.save(utilisateur);
            }
        }

        return compte;
    }


}


