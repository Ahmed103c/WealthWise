import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Transaction {
  id?: number;
  compteId: number;
  transactionDate: string; // Format ISO (ex: "2023-09-05T00:00:00.000Z")
  description?: string;
  amount: number;
  type: string; // "credit" ou "debit"
  // Optionnellement, vous pouvez ajouter recurrenceFrequency, recurrenceEnd, categoryId, etc.
}

@Injectable({
  providedIn: 'root',
})
export class TransactionService {
  private apiUrl = 'http://localhost:8070/transactions';

  constructor(private http: HttpClient) {}

  // Ajoute une nouvelle transaction
  addTransaction(transaction: Transaction): Observable<Transaction> {
    return this.http.post<Transaction>(this.apiUrl, transaction);
  }

  // Récupère les transactions pour un compte donné
  getTransactionsByCompte(compteId: number): Observable<Transaction[]> {
    return this.http.get<Transaction[]>(`${this.apiUrl}/compte/${compteId}`);
  }

  // Importer des transactions depuis un fichier CSV
  importTransactions(file: File): Observable<string> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<string>(`${this.apiUrl}/import`, formData);
  }

  // Exporter les transactions en CSV
  exportTransactions(): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/export`, { responseType: 'blob' });
  }
}
