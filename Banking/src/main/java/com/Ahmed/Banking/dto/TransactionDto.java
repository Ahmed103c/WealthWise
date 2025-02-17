package com.Ahmed.Banking.dto;

import com.Ahmed.Banking.models.Transaction;
import com.Ahmed.Banking.models.TransactionType;
import com.Ahmed.Banking.models.RecurrenceFrequency;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
public class TransactionDto {

    private Integer id;

    @NotNull(message = "Le montant est obligatoire")
    private BigDecimal amount;

    @NotNull(message = "La date de transaction est obligatoire")
    private LocalDate transactionDate;

    private String description;

    @NotNull(message = "Le type de transaction est obligatoire")
    private TransactionType type;

    private RecurrenceFrequency recurrenceFrequency;

    private LocalDate recurrenceEnd;

    @NotNull(message = "L'ID du compte est obligatoire")
    private Integer compteId;

    private Integer categoryId;

    // ✅ Conversion d'une entité `Transaction` en `TransactionDto`
    public static TransactionDto fromEntity(Transaction transaction) {
        return TransactionDto.builder()
                .id(transaction.getId())
                .amount(transaction.getAmount())
                .transactionDate(transaction.getTransactionDate())
                .description(transaction.getDescription())
                .type(transaction.getType())
                .recurrenceFrequency(transaction.getRecurrenceFrequency())
                .recurrenceEnd(transaction.getRecurrenceEnd())
                .compteId(transaction.getCompte().getId())
                .categoryId(transaction.getCategory() != null ? transaction.getCategory().getId() : null)
                .build();
    }

    // ✅ Conversion d'un `TransactionDto` en une entité `Transaction`
    public static Transaction toEntity(TransactionDto dto) {
        Transaction transaction = new Transaction();
        transaction.setId(dto.getId());
        transaction.setAmount(dto.getAmount());
        transaction.setTransactionDate(dto.getTransactionDate());
        transaction.setDescription(dto.getDescription());
        transaction.setType(dto.getType());
        transaction.setRecurrenceFrequency(dto.getRecurrenceFrequency());
        transaction.setRecurrenceEnd(dto.getRecurrenceEnd());

        return transaction;
    }
}
