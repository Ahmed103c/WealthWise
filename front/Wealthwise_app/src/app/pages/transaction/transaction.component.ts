import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TransactionService, Transaction } from '../../services/transaction.service';

@Component({
  selector: 'app-transaction',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="transaction-container">
      <h2>Ajouter une Transaction</h2>
      <form (ngSubmit)="addTransaction()">
        <div>
          <label for="compteId">ID Compte :</label>
          <input type="number" id="compteId" name="compteId" [(ngModel)]="newTransaction.compteId" required>
        </div>
        <div>
          <label for="transactionDate">Date :</label>
          <input type="date" id="transactionDate" name="transactionDate"
                 [value]="formatDate(newTransaction.transactionDate)"
                 (change)="onDateChange($event)" required>
        </div>
        <div>
          <label for="description">Description :</label>
          <input type="text" id="description" name="description" [(ngModel)]="newTransaction.description">
        </div>
        <div>
          <label for="amount">Montant :</label>
          <input type="number" id="amount" name="amount" [(ngModel)]="newTransaction.amount" required>
        </div>
        <div>
          <label for="type">Type :</label>
          <select id="type" name="type" [(ngModel)]="newTransaction.type" required>
            <option value="  CREDIT">CREDIT</option>
            <option value="debit">Débit</option>
          </select>
        </div>
        <button type="submit">Ajouter Transaction</button>
      </form>

      <div *ngIf="transactionMessage" class="message">{{ transactionMessage }}</div>
      
      <hr>
      
      <h2>Liste des Transactions</h2>
      <div class="search-container">
        <label for="accountSearch">ID Compte :</label>
        <input type="number" id="accountSearch" name="accountSearch" [(ngModel)]="searchCompteId">
        <button (click)="loadTransactions()">Charger Transactions</button>
      </div>
      
      <div *ngIf="transactions && transactions.length; else noTransactions">
        <ul>
          <li *ngFor="let tx of transactions">
            <strong>ID :</strong> {{ tx.id }} -
            <strong>Date :</strong> {{ tx.transactionDate | date:'shortDate' }} -
            <strong>Description :</strong> {{ tx.description }} -
            <strong>Montant :</strong> {{ tx.amount | currency }} -
            <strong>Type :</strong> {{ tx.type }}
          </li>
        </ul>
      </div>
      <ng-template #noTransactions>
        <p>Aucune transaction trouvée pour ce compte.</p>
      </ng-template>
    </div>
  `,
  styles: [`
    .transaction-container {
      max-width: 600px;
      margin: 0 auto;
      padding: 1rem;
    }
    form div, .search-container {
      margin-bottom: 1rem;
    }
    .message {
      color: red;
      margin-top: 1rem;
    }
    ul {
      list-style-type: none;
      padding: 0;
    }
    li {
      padding: 0.5rem;
      border-bottom: 1px solid #ccc;
    }
  `]
})
export class TransactionComponent implements OnInit {
  newTransaction: Transaction = {
    compteId: 0,
    transactionDate: '',  // Initialisé à vide
    description: '',
    amount: 0,
    type: 'credit'
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
          type: 'credit' 
        };
      },
      error: (err) => {
        this.transactionMessage = err.message;
        console.error(err);
      }
    });
  }

  loadTransactions(): void {
    if (!this.searchCompteId) {
      this.transactionMessage = 'Veuillez fournir un ID de compte valide.';
      return;
    }
    this.transactionService.getTransactionsByCompte(this.searchCompteId).subscribe({
      next: (data) => {
        this.transactions = data;
      },
      error: (err) => {
        this.transactionMessage = err.message;
        console.error(err);
      }
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
