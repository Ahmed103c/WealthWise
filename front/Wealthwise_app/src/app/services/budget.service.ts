import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';

export interface Utilisateur {
  id: number;
}

export interface Budget {
  id?: number;
  utilisateur: Utilisateur;
  montantAlloue: number;
  // Vous pouvez ajouter d'autres champs (ex. startDate, endDate, période) si nécessaire
}

export interface BudgetCategorie {
  id?: number;
  budgetId: number;
  categorieId: number;
  montantAlloue: number;
}

@Injectable({
  providedIn: 'root'
})
export class BudgetService {
  private apiUrl = 'http://localhost:8070/api/budget';

  constructor(private http: HttpClient) {}

  // Créer un budget
  creerBudget(budget: Budget): Observable<Budget> {
    return this.http.post<Budget>(`${this.apiUrl}/create`, budget)
      .pipe(catchError(this.handleError));
  }

  // Récupérer les budgets d’un utilisateur
  getBudgets(utilisateurId: number): Observable<Budget[]> {
    return this.http.get<Budget[]>(`${this.apiUrl}/${utilisateurId}`)
      .pipe(catchError(this.handleError));
  }

  // Allouer un budget à une catégorie
  allouerBudget(budgetId: number, categorieId: number, montant: number): Observable<BudgetCategorie> {
    // Les paramètres sont envoyés en query string
    const params = {
      budgetId: budgetId.toString(),
      categorieId: categorieId.toString(),
      montant: montant.toString()
    };
    return this.http.post<BudgetCategorie>(`${this.apiUrl}/allouer`, null, { params })
      .pipe(catchError(this.handleError));
  }

  private handleError(error: HttpErrorResponse) {
    console.error('Erreur API:', error);
    return throwError(() => new Error('Une erreur est survenue, veuillez réessayer.'));
  }
}
