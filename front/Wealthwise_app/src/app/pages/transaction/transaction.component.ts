import { Component, OnInit } from '@angular/core';
import {
  TransactionService,
  Transaction,
  Compte,
} from '../../services/transaction.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-transaction',
  templateUrl: './transaction.component.html',
  styleUrls: ['./transaction.component.scss'],
  imports: [CommonModule, FormsModule], // Modules nécessaires pour *ngIf et [(ngModel)]
})
export class TransactionComponent implements OnInit {
  transactions: Transaction[] = [];
  filteredTransactions: Transaction[] = [];
  comptes: Compte[] = [];

  newTransaction: Transaction = {
    compteId: 0,
    transactionDate: '',
    description: '',
    amount: 0,
    type: 'CREDIT',
    recurrenceFrequency: 'NONE',
    recurrenceEnd: '',
  };

  selectedCompteId: number = 0; // Filtrage par compte
  filterStartDate: string = ''; // Date de début
  filterEndDate: string = ''; // Date de fin

  // Optionnel : filtre par catégorie si besoin
  selectedCategoryId: number = 0;

  recurrenceOptions = ['NONE', 'DAILY', 'WEEKLY', 'MONTHLY', 'YEARLY'];

  transactionMessage: string = '';

  constructor(private transactionService: TransactionService) {}

  ngOnInit(): void {
    this.loadTransactions();
    this.loadComptes();
  }

  loadTransactions(): void {
    const userId = 1; // Remplacez par l'ID de l'utilisateur connecté
    this.transactionService.getTransactionsByUserId(userId).subscribe({
      next: (data) => {
        this.transactions = data;
        this.applyFilters();
      },
      error: (err) => {
        this.transactionMessage =
          '❌ Erreur lors du chargement des transactions';
        console.error(err);
      },
    });
  }

  loadComptes(): void {
    const userId = 1; // Remplacez par l'ID de l'utilisateur connecté
    this.transactionService.getUserComptes(userId).subscribe({
      next: (comptes) => (this.comptes = comptes),
      error: (err) => {
        this.transactionMessage = '❌ Erreur lors du chargement des comptes';
        console.error(err);
      },
    });
  }
  applyFilters(): void {
    console.log('applyFilters called');
    const startDate = this.filterStartDate
      ? new Date(this.filterStartDate)
      : null;
    const endDate = this.filterEndDate ? new Date(this.filterEndDate) : null;

    this.filteredTransactions = this.transactions.filter((transaction) => {
      const tDate = new Date(transaction.transactionDate);

      // Filtre par compte
      if (
        this.selectedCompteId &&
        transaction.compteId !== this.selectedCompteId
      ) {
        return false;
      }

      // Filtre par date de début
      if (startDate && tDate < startDate) {
        return false;
      }

      // Filtre par date de fin
      if (endDate && tDate > endDate) {
        return false;
      }

      // Filtre par catégorie (optionnel)
      if (
        this.selectedCategoryId &&
        transaction.categoryId !== this.selectedCategoryId
      ) {
        return false;
      }

      return true;
    });
  }

  addTransaction(): void {
    if (!this.newTransaction.compteId) {
      this.transactionMessage = '❌ Veuillez sélectionner un compte.';
      return;
    }
    this.transactionService.addTransaction(this.newTransaction).subscribe({
      next: (res) => {
        this.transactionMessage = '✅ Transaction ajoutée avec succès';
        this.loadTransactions();
        // Réinitialiser le formulaire d'ajout
        this.newTransaction = {
          compteId: 0,
          transactionDate: '',
          description: '',
          amount: 0,
          type: 'credit',
          recurrenceFrequency: 'NONE',
          recurrenceEnd: '',
        };
      },
      error: (err) => {
        this.transactionMessage = '❌ Erreur: ' + err.message;
        console.error(err);
      },
    });
  }
}
