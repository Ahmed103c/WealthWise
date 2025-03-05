import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Utilisateur {
  id: number;
  // Ajoutez d'autres propriétés si nécessaire
}

export interface Category {
  id: number;
  name: string;
}

export interface BudgetCategorie {
  id?: number;
  budgetId: number;
  category: Category;
  montantAlloue: number;
  montantDepense: number;
}

export interface Budget {
  id?: number;
  utilisateur: Utilisateur; // le back-end attend un objet Utilisateur avec son id
  montantAlloue: number;
  startDate?: string;
  endDate?: string;
  budgetCategories?: BudgetCategorie[];
}

@Injectable({
  providedIn: 'root'
})
export class BudgetService {
  private apiUrl = 'http://localhost:8070/api/budget';
  private categoryUrl = 'http://localhost:8070/api/predictions'; // <-- Au lieu de /api/category
  ;


  constructor(private http: HttpClient) {}

  // Créer un budget
// budget.service.ts
creerBudget(budget: Budget): Observable<Budget> {
  const params = new HttpParams()
    .set('userId', budget.utilisateur.id.toString())
    .set('montantAlloue', budget.montantAlloue.toString())
    .set('startDate', budget.startDate || '') // en format "yyyy-MM-dd"
    .set('endDate', budget.endDate || '');

  // Le 2e argument (body) est `null`, car le back attend des @RequestParam
  return this.http.post<Budget>(`${this.apiUrl}/create`, null, { params });
}


  // Récupérer les budgets par utilisateur
  getBudgets(utilisateurId: number): Observable<Budget[]> {
    return this.http.get<Budget[]>(`${this.apiUrl}/${utilisateurId}`);
  }

 // Allouer un budget à une catégorie
 allouerBudget(budgetId: number, categorieId: number, montant: number): Observable<BudgetCategorie> {
  const params = new HttpParams()
    .set('budgetId', budgetId.toString())
    .set('categorieId', categorieId.toString())
    .set('montant', montant.toString());
  return this.http.post<BudgetCategorie>(`${this.apiUrl}/allouer`, null, { params });
}

// Récupérer toutes les catégories depuis le back-end
getCategories(): Observable<Category[]> {
  return this.http.get<Category[]>(this.categoryUrl);
}
}
