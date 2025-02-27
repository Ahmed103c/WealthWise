import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {
  TransactionService,
  Transaction,
} from '../../services/transaction.service';

@Component({
  selector: 'app-transaction',
  standalone: true,
  imports: [CommonModule, FormsModule],
  // template: `./transaction.component.html`,4
  templateUrl: './transaction.component.html',
  styleUrls: ['./transaction.component.scss'],
})
export class TransactionComponent implements OnInit {
  newTransaction: Transaction = {
    compteId: 0,
    transactionDate: '', // Initialisé à vide
    description: '',
    amount: 0,
    type: 'credit',
  };
  transactions: Transaction[] = [];
  transactionMessage: string = '';
  searchCompteId: number = 0;

  constructor(private transactionService: TransactionService) {}

  ngOnInit(): void {
    // Optionnel : charger automatiquement les transactions si vous avez un compte par défaut
  }

  addTransaction(): void {
    this.transactionService.addTransaction(this.newTransaction).subscribe({
      next: (res) => {
        this.transactionMessage = 'Transaction ajoutée avec succès !';
        this.loadTransactions();
        // Réinitialiser le formulaire tout en conservant le compteId
        this.newTransaction = {
          compteId: this.newTransaction.compteId,
          transactionDate: '',
          description: '',
          amount: 0,
          type: 'credit',
        };
      },
      error: (err) => {
        this.transactionMessage = err.message;
        console.error(err);
      },
    });
  }

  loadTransactions(): void {
    if (!this.searchCompteId) {
      this.transactionMessage = 'Veuillez fournir un ID de compte valide.';
      return;
    }
    this.transactionService
      .getTransactionsByCompte(this.searchCompteId)
      .subscribe({
        next: (data) => {
          this.transactions = data;
        },
        error: (err) => {
          this.transactionMessage = err.message;
          console.error(err);
        },
      });
  }

  // Cette fonction formate une date ISO pour extraire "yyyy-MM-dd"
  formatDate(date: string): string {
    if (!date) return '';
    return date.indexOf('T') !== -1 ? date.split('T')[0] : date;
  }

  // Méthode pour gérer le changement de date en toute sécurité
  onDateChange(event: Event): void {
    const target = event.target as HTMLInputElement;
    this.newTransaction.transactionDate = target ? target.value : '';
  }
}
