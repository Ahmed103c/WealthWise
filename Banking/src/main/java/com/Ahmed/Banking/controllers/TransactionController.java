package com.Ahmed.Banking.controllers;

import com.Ahmed.Banking.dto.TransactionDto;
import com.Ahmed.Banking.services.TransactionService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    // Ajouter une transaction manuellement
    @PostMapping
    public ResponseEntity<?> saveTransaction(@RequestBody TransactionDto transactionDto) {
        try {
            TransactionDto savedTransaction = transactionService.saveTransaction(transactionDto);
            return ResponseEntity.ok(savedTransaction);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Récupérer les transactions d'un compte spécifique
    @GetMapping("/compte/{compteId}")
    public ResponseEntity<List<TransactionDto>> getTransactionsByCompteId(@PathVariable Integer compteId) {
        return ResponseEntity.ok(transactionService.getTransactionsByCompteId(compteId));
    }

    // Importer des transactions depuis un fichier CSV
    @PostMapping("/import")
    public ResponseEntity<?> importTransactionsFromCsv(@RequestParam("file") MultipartFile file) {
        try {
            transactionService.importTransactionsFromCsv(file);
            return ResponseEntity.ok("Transactions importées avec succès !");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Échec de l'importation : " + e.getMessage());
        }
    }

    // Exporter les transactions en CSV
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportTransactionsToCsv() {
        byte[] data = transactionService.exportTransactionsToCsv();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=transactions.csv")
                .body(data);
    }

}
