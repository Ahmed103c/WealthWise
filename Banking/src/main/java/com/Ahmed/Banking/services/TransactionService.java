package com.Ahmed.Banking.services;

import com.Ahmed.Banking.dto.TransactionDto;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface TransactionService {
    TransactionDto saveTransaction(TransactionDto transactionDto);
    List<TransactionDto> getTransactionsByCompteId(Integer compteId);
    void importTransactionsFromCsv(MultipartFile file);
    byte[] exportTransactionsToCsv();
    // ✅ Add missing method: Fetch transactions from GoCardless API
    void fetchAndSaveTransactions(String accessToken, String accountId);
}
