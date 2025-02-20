import { Component, OnInit } from '@angular/core';
import { TransactionService, Transaction } from '../../services/transaction.service';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';

@Component({
  selector: 'app-transaction',
  standalone: true,
  templateUrl: './transaction.component.html',
  styleUrls: ['./transaction.component.scss'],
  imports: [FormsModule, CommonModule, HttpClientModule],
})
export class TransactionComponent implements OnInit {
  transactions: Transaction[] = [];
  newTransaction: Transaction = {
    compteId: 1,
    date: '',
    description: '',
    montant: 0,
    type: 'credit',
  };
  selectedFile!: File;
  message = '';

  constructor(private transactionService: TransactionService) {}

  ngOnInit() {
    this.loadTransactions();
  }

  // ✅ Load transactions from backend
  loadTransactions() {
    this.transactionService.getTransactionsByCompte(1).subscribe({
      next: (data) => (this.transactions = data),
      error: (err) => console.error('Error loading transactions', err),
    });
  }

  // ✅ Add a new transaction
  addTransaction() {
    this.transactionService.addTransaction(this.newTransaction).subscribe({
      next: (res) => {
        this.message = '✅ Transaction added successfully!';
        this.transactions.push(res);
        this.newTransaction = { compteId: 1, date: '', description: '', montant: 0, type: 'credit' };
      },
      error: (err) => {
        this.message = '❌ Error adding transaction!';
        console.error(err);
      },
    });
  }

  // ✅ Handle CSV file selection
  onFileSelected(event: any) {
    this.selectedFile = event.target.files[0];
  }

  // ✅ Import transactions from CSV
  importTransactions() {
    if (!this.selectedFile) {
      this.message = '❌ Please select a file!';
      return;
    }

    this.transactionService.importTransactions(this.selectedFile).subscribe({
      next: (res) => {
        this.message = res;
        this.loadTransactions();
      },
      error: (err) => {
        this.message = '❌ Error importing transactions!';
        console.error(err);
      },
    });
  }

  // ✅ Export transactions to CSV
  exportTransactions() {
    this.transactionService.exportTransactions().subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = 'transactions.csv';
        a.click();
      },
      error: (err) => {
        this.message = '❌ Error exporting transactions!';
        console.error(err);
      },
    });
  }
}
