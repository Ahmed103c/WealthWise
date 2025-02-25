
package com.Ahmed.Banking.services.Implementations;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@ConditionalOnProperty(name = "ml.enabled", havingValue = "true", matchIfMissing = true) // Active par défaut
public class BudgetMLService {
    public boolean isBudgetExceeded(Integer userId, BigDecimal montant) {
        // Logique ML...
        return false; // Remplacer par une vraie implémentation plus tard
    }
}
