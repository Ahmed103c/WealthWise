package com.Ahmed.Banking.services.Implementations;

import com.Ahmed.repositories.CategoryRepository;
import com.Ahmed.repositories.CompteRepository;
import com.Ahmed.repositories.TransactionRepository;
import com.Ahmed.Banking.dto.TransactionDto;
import com.Ahmed.Banking.models.Transaction;
import com.Ahmed.Banking.models.Compte;
import com.Ahmed.Banking.models.Category;
import com.Ahmed.Banking.services.TransactionService;
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

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final CompteRepository compteRepository;
    private final CategoryRepository categoryRepository;
    private final RestTemplate restTemplate;

    private static final String BASE_URL = "https://bankaccountdata.gocardless.com/api/v2";
    private static final String SECRET_ID = "2f7225b9-c4a2-4099-b607-3e4b45013428";  // 🔥 Replace with real credentials
    private static final String SECRET_KEY = "b990a265d0ca0d005b235c7bf1da15cf534e9c2e8822ae51874a87afe4bbb5c8166b6389393a84a46ca69b21d5fc17f7be64e77f50f6e92ad0068cd41c79c95c"; // 🔥 Replace with real credentials
    private static final String SANDBOX_INSTITUTION_ID = "SANDBOXFINANCE_SFIN0000";

    public TransactionServiceImpl(TransactionRepository transactionRepository,
                                  CompteRepository compteRepository,
                                  CategoryRepository categoryRepository,
                                  RestTemplate restTemplate) {
        this.transactionRepository = transactionRepository;
        this.compteRepository = compteRepository;
        this.categoryRepository = categoryRepository;
        this.restTemplate = restTemplate;
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

            // ✅ FIX: Fetch unique account
            Optional<Compte> compteOptional = compteRepository.findByExternalId(accountId);
            if (compteOptional.isEmpty()) {
                throw new RuntimeException("❌ No account found for externalId: " + accountId);
            }
            Compte compte = compteOptional.get();

            for (Map<String, Object> transactionData : bookedTransactions) {
                Transaction transaction = new Transaction();
                transaction.setCompte(compte);

                // ✅ Handle Date Parsing Safely
                try {
                    transaction.setTransactionDate(LocalDate.parse(transactionData.get("bookingDate").toString()));
                } catch (Exception e) {
                    throw new RuntimeException("❌ Invalid bookingDate format: " + transactionData.get("bookingDate"), e);
                }

                // ✅ Extract and Convert Transaction Amount Safely
                Map<String, Object> transactionAmount = (Map<String, Object>) transactionData.get("transactionAmount");
                if (transactionAmount != null && transactionAmount.containsKey("amount")) {
                    String amountStr = transactionAmount.get("amount").toString().trim();
                    try {
                        // ✅ Convert amount safely, handling scientific notation
                        BigDecimal amount = new BigDecimal(amountStr.replace(",", ""));
                        transaction.setAmount(amount);
                    } catch (NumberFormatException e) {
                        throw new RuntimeException("❌ Invalid transaction amount format: " + amountStr, e);
                    }
                } else {
                    throw new RuntimeException("❌ Missing transaction amount field");
                }

                // ✅ Extract and Set Transaction Description Safely
                transaction.setDescription(transactionData.getOrDefault("remittanceInformationUnstructured", "N/A").toString());

                // ✅ Save Transaction
                transactionRepository.save(transaction);
            }
            System.out.println("✅ Transactions successfully imported for account: " + accountId);
        } else {
            throw new RuntimeException("❌ Failed to fetch transactions");
        }
    }



    @Override
    public TransactionDto saveTransaction(TransactionDto transactionDto) {
        return null;
    }

    @Override
    public List<TransactionDto> getTransactionsByCompteId(Integer compteId) {
        return List.of();
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

                    transactions.add(transaction);
                } catch (Exception e) {
                    System.err.println("Erreur lors de l'importation d'une ligne CSV : " + e.getMessage());
                }
            }
            transactionRepository.saveAll(transactions);
        } catch (Exception e) {
            throw new RuntimeException("Échec de l'importation du fichier CSV : " + e.getMessage());
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
