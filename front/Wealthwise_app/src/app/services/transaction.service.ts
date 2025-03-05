import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Transaction {
  id?: number;
  compteId: number;
  transactionDate: string;
  description?: string;
  amount: number;
  type: string;
  recurrenceFrequency?: string;
  recurrenceEnd?: string;
  categoryId?: number;
  categoryName?: string;
}

export interface Compte {
  id: number;
  nom: string;
  balance: number;
}

@Injectable({
  providedIn: 'root',
})
export class TransactionService {
  private transactionUrl = 'http://localhost:8070/transactions';
  private compteUrl = 'http://localhost:8070/api/comptes';

  constructor(private http: HttpClient) {}

  // Ajout d'une transaction (POST)
  addTransaction(transaction: Transaction): Observable<Transaction> {
    return this.http.post<Transaction>(this.transactionUrl, transaction);
  }

  // Récupération des transactions d'un utilisateur via l'endpoint approprié
  getTransactionsByUserId(userId: number): Observable<Transaction[]> {
    return this.http.get<Transaction[]>(
      `${this.transactionUrl}/user/${userId}`
    );
  }

  // Récupération des transactions d'un compte (si besoin)
  getTransactionsByCompte(compteId: number): Observable<Transaction[]> {
    return this.http.get<Transaction[]>(
      `${this.transactionUrl}/compte/${compteId}`
    );
  }

  // Récupération des comptes de l'utilisateur
  getUserComptes(userId: number): Observable<Compte[]> {
    return this.http.get<Compte[]>(`${this.compteUrl}/utilisateur/${userId}`);
  }
}
