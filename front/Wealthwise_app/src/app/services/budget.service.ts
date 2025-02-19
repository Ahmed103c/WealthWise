import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class BudgetService {
  private apiUrl = 'http://localhost:8070/api/budget';

  constructor(private http: HttpClient) {}

  // ✅ Créer un budget
  creerBudget(utilisateurId: number, montantAlloue: number, startDate: string, endDate: string): Observable<any> {
    const body = {
      utilisateur: { id: utilisateurId },
      montantAlloue: montantAlloue,
      startDate: startDate,
      endDate: endDate
    };

    return this.http.post(`${this.apiUrl}/create`, body);
  }

  // ✅ Récupérer les budgets par utilisateur
  getBudgets(utilisateurId: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/${utilisateurId}`);
  }
}
