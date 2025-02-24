package com.Ahmed.Banking.services.Implementations;

import com.Ahmed.Banking.models.*;
import com.Ahmed.repositories.CategoryRepository;
import com.Ahmed.repositories.CompteRepository;
import com.Ahmed.repositories.BudgetCategorieRepository;
import com.Ahmed.repositories.UtilisateurRepository;

import com.Ahmed.repositories.TransactionRepository;
import com.Ahmed.Banking.dto.TransactionDto;
import com.Ahmed.Banking.services.TransactionService;
import jakarta.transaction.Transactional;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TransactionServiceImpl implements TransactionService {


    private static final String FASTAPI_URL = "http://localhost:8000/predict"; // URL du modèle ML
    private static final String BASE_URL = "https://bankaccountdata.gocardless.com/api/v2";
    private static final String SECRET_ID = "2f7225b9-c4a2-4099-b607-3e4b45013428";  // 🔥 Replace with real credentials
    private static final String SECRET_KEY = "b990a265d0ca0d005b235c7bf1da15cf534e9c2e8822ae51874a87afe4bbb5c8166b6389393a84a46ca69b21d5fc17f7be64e77f50f6e92ad0068cd41c79c95c"; // 🔥 Replace with real credentials
    private static final String SANDBOX_INSTITUTION_ID = "SANDBOXFINANCE_SFIN0000";

    private final TransactionRepository transactionRepository;
    private final CompteRepository compteRepository;
    private final CategoryRepository categoryRepository;
    private final BudgetCategorieRepository budgetCategorieRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final RestTemplate restTemplate;
    private final CategoryService categoryService; // ✅ Ajout du service de catégorisation


    public TransactionServiceImpl(TransactionRepository transactionRepository,
                                  CompteRepository compteRepository,
                                  CategoryRepository categoryRepository,
                                  BudgetCategorieRepository budgetCategorieRepository,
                                  UtilisateurRepository utilisateurRepository,
                                  RestTemplate restTemplate, CategoryService categoryService) {
        this.transactionRepository = transactionRepository;
        this.compteRepository = compteRepository;
        this.categoryRepository = categoryRepository;
        this.budgetCategorieRepository = budgetCategorieRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.restTemplate = restTemplate;
        this.categoryService = categoryService;
    }

    // ✅ Step 1: Fetch Access Token Automatically
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

    // ✅ Step 2: Fetch User Accounts from GoCardless and Save in DB
    public List<String> fetchAndSaveUserAccounts(String requisitionId, Integer userId) {
        String accessToken = getAccessToken();
        String url = BASE_URL + "/requisitions/" + requisitionId;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, request, Map.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            List<String> accounts = (List<String>) response.getBody().get("accounts");

            for (String accountId : accounts) {
                Compte compte = new Compte();
                compte.setExternalId(accountId);
                compteRepository.save(compte);
            }
            return accounts;
        } else {
            throw new RuntimeException("❌ Failed to fetch user accounts");
        }
    }

    // ✅ Fetch transactions from GoCardless API & categorize them
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
                try {
                    Transaction transaction = new Transaction();
                    transaction.setCompte(compte);

                    transaction.setTransactionDate(LocalDate.parse(transactionData.get("bookingDate").toString()));

                    Map<String, Object> transactionAmount = (Map<String, Object>) transactionData.get("transactionAmount");
                    if (transactionAmount != null && transactionAmount.containsKey("amount")) {
                        String amountStr = transactionAmount.get("amount").toString().trim();
                        BigDecimal amount = new BigDecimal(amountStr.replace(",", ""));
                        transaction.setAmount(amount);
                    } else {
                        throw new RuntimeException("❌ Le champ montant est manquant !");
                    }

                    transaction.setDescription(transactionData.getOrDefault("remittanceInformationUnstructured", "N/A").toString());

                    // ✅ Auto-détection de la catégorie
                    Category category = categoryService.predictCategory(transaction.getDescription());
                    transaction.setCategory(category);

                    transactionRepository.save(transaction);
                    System.out.println("✅ Transaction enregistrée avec catégorie détectée : " + category.getName());

                } catch (Exception e) {
                    System.err.println("⚠️ Erreur lors du traitement d'une transaction API : " + e.getMessage());
                }
            }
        } else {
            throw new RuntimeException("❌ Impossible de récupérer les transactions");
        }
    }


    @Override
    public TransactionDto saveTransaction(TransactionDto transactionDto) {
        // ✅ Vérifier que le `compteId` n'est pas null
        if (transactionDto.getCompteId() == null) {
            throw new IllegalArgumentException("❌ Le champ `compteId` est obligatoire !");
        }

        // ✅ Récupération du compte
        Optional<Compte> compteOpt = compteRepository.findById(transactionDto.getCompteId());
        if (compteOpt.isEmpty()) {
            throw new RuntimeException("❌ Compte introuvable !");
        }
        Compte compte = compteOpt.get();
        Utilisateur utilisateur = compte.getUtilisateur();

        // ✅ Vérifier si `categoryId` est fourni, sinon prédire avec ML
        Category category;
        if (transactionDto.getCategoryId() == null || transactionDto.getCategoryId() == 0) {
            category = categoryService.predictCategory(transactionDto.getDescription());
        } else {
            Optional<Category> categoryOpt = categoryRepository.findById(transactionDto.getCategoryId());
            if (categoryOpt.isEmpty()) {
                throw new RuntimeException("❌ Catégorie introuvable !");
            }
            category = categoryOpt.get();
        }

        // ✅ Création de la transaction
        Transaction transaction = new Transaction();
        transaction.setAmount(transactionDto.getAmount());
        transaction.setTransactionDate(transactionDto.getTransactionDate());
        transaction.setDescription(transactionDto.getDescription());
        transaction.setCompte(compte);
        transaction.setCategory(category);

        // ✅ Vérifier que le solde du compte et utilisateur ne sont pas null
        if (compte.getBalance() == null) {
            compte.setBalance(BigDecimal.ZERO);
        }
        if (utilisateur.getBalance() == null) {
            utilisateur.setBalance(BigDecimal.ZERO);
        }

        // ✅ Mise à jour du solde du compte et utilisateur
        BigDecimal montant = transactionDto.getAmount() != null ? transactionDto.getAmount() : BigDecimal.ZERO;

        if (transactionDto.getType() == TransactionType.DEPENSE) {
            if (compte.getBalance().compareTo(montant) < 0) {
                throw new RuntimeException("❌ Solde insuffisant !");
            }
            compte.setBalance(compte.getBalance().subtract(montant));
            utilisateur.setBalance(utilisateur.getBalance().subtract(montant));
        } else {
            compte.setBalance(compte.getBalance().add(montant));
            utilisateur.setBalance(utilisateur.getBalance().add(montant));
        }

        // ✅ Sauvegarde du compte et de l'utilisateur
        utilisateurRepository.save(utilisateur);
        compteRepository.save(compte);

        // ✅ Vérifier et mettre à jour le budget si nécessaire
        Optional<BudgetCategorie> budgetCategorieOpt = budgetCategorieRepository.findByBudgetIdAndCategoryId(compte.getUtilisateur().getId(), category.getId());
        if (budgetCategorieOpt.isPresent()) {
            BudgetCategorie budgetCategorie = budgetCategorieOpt.get();

            if (budgetCategorie.getMontantDepense() == null) {
                budgetCategorie.setMontantDepense(BigDecimal.ZERO);
            }

            budgetCategorie.setMontantDepense(budgetCategorie.getMontantDepense().add(montant));

            if (budgetCategorie.getMontantDepense().compareTo(budgetCategorie.getMontantAlloue()) > 0) {
                throw new RuntimeException("⚠️ Attention ! Dépassement du budget alloué !");
            }

            budgetCategorieRepository.save(budgetCategorie);
        }

        // ✅ Sauvegarde de la transaction
        transaction = transactionRepository.save(transaction);
        return TransactionDto.fromEntity(transaction);
    }

    @Override
    public List<TransactionDto> getTransactionsByCompteId(Integer compteId) {
        List<Transaction> transactions = transactionRepository.findByCompteId(compteId);
        return transactions.stream()
                .map(TransactionDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<TransactionDto> getTransactionsByUserId(Integer userId) {
        List<Compte> comptes = compteRepository.findByUtilisateurId(userId);
        if (comptes.isEmpty()) {
            return Collections.emptyList();
        }

        List<Transaction> allTransactions = new ArrayList<>();
        
        for (Compte compte : comptes) {
            List<Transaction> transactions = transactionRepository.findByCompteId(compte.getId());
            allTransactions.addAll(transactions);
        }

        return allTransactions.stream()
                .map(TransactionDto::fromEntity)
                .collect(Collectors.toList());
    }




     @Override
    public List<TransactionDto> getTransactionsByUserId2(Integer userId) {
        List<Transaction> transactions = transactionRepository.findTransactionsByUserId(userId);
        return transactions.stream()
                .map(TransactionDto::fromEntity)
                .collect(Collectors.toList());
    }

    // ✅ CSV IMPORT FUNCTIONALITY
    @Override
    public void importTransactionsFromCsv(MultipartFile file) {
        try (Reader reader = new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8)) {
            Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader);

            List<Transaction> transactions = new ArrayList<>();
            for (CSVRecord record : records) {
                try {
                    Transaction transaction = new Transaction();
                    transaction.setAmount(new BigDecimal(record.get("amount")));
                    transaction.setTransactionDate(LocalDate.parse(record.get("transactionDate")));
                    transaction.setDescription(record.get("description"));

                    Integer compteId = Integer.parseInt(record.get("compteId"));
                    Optional<Compte> compte = compteRepository.findById(compteId);
                    if (compte.isEmpty()) {
                        System.out.println("Compte non trouvé pour ID: " + compteId);
                        continue;
                    }
                    transaction.setCompte(compte.get());

                    // ✅ Auto-détection de la catégorie
                    Category category = categoryService.predictCategory(transaction.getDescription());
                    transaction.setCategory(category);

                    transactions.add(transaction);
                    System.out.println("✅ Transaction CSV enregistrée avec catégorie détectée : " + category.getName());

                } catch (Exception e) {
                    System.err.println("⚠️ Erreur lors de l'importation d'une ligne CSV : " + e.getMessage());
                }
            }
            transactionRepository.saveAll(transactions);
        } catch (Exception e) {
            throw new RuntimeException("❌ Échec de l'importation du fichier CSV : " + e.getMessage());
        }
    }

    // ✅ CSV EXPORT FUNCTIONALITY
    @Override
    public byte[] exportTransactionsToCsv() {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader("id", "amount", "transactionDate", "description", "compteId", "categoryId"))) {

            List<Transaction> transactions = transactionRepository.findAll();
            for (Transaction transaction : transactions) {
                csvPrinter.printRecord(
                        transaction.getId(),
                        transaction.getAmount(),
                        transaction.getTransactionDate(),
                        transaction.getDescription(),
                        transaction.getCompte().getId(),
                        transaction.getCategory() != null ? transaction.getCategory().getId() : ""
                );
            }

            csvPrinter.flush();
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Échec de l'exportation en CSV : " + e.getMessage());
        }
    }
}
