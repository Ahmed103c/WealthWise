import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { BudgetService, Budget } from '../../services/budget.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-budget',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="budget-container">
      <h2>Créer un budget</h2>
      <form (ngSubmit)="creerBudget()">
        <div>
          <label for="montantAlloue">Montant Alloué :</label>
          <input type="number" id="montantAlloue" name="montantAlloue"
                 [(ngModel)]="budget.montantAlloue" required>
        </div>
        <button type="submit">Créer Budget</button>
      </form>
      <div *ngIf="message" class="message">{{ message }}</div>
      <hr>
      <h2>Liste des Budgets</h2>
      <div *ngIf="budgets && budgets.length; else noBudget">
        <ul>
          <li *ngFor="let budget of budgets">
            <strong>ID :</strong> {{ budget.id }} -
            <strong>Montant Alloué :</strong> {{ budget.montantAlloue | currency }}
          </li>
        </ul>
      </div>
      <ng-template #noBudget>
        <p>Aucun budget trouvé.</p>
      </ng-template>
    </div>
  `,
  styles: [`
    .budget-container {
      /* Ajoutez une marge à droite pour laisser de l'espace si la navbar est en position fixe */
      margin-right: 1000px;
      padding:20rem;
    }
    h2 {
      margin-top: 1rem;
    }
    form div {
      margin-bottom: 1rem;
    }
    .message {
      color: red;
      margin-top: 1rem;
    }
  `]
})
export class BudgetComponent implements OnInit {
  // Initialisation par défaut pour éviter que "budget" soit undefined lors du rendu
  budget: Budget = {
    utilisateur: { id: 0 },
    montantAlloue: 0
  };
  budgets: Budget[] = [];
  message: string = '';

  constructor(
    private authService: AuthService,
    private budgetService: BudgetService
  ) {}

  ngOnInit(): void {
    const userId = this.authService.getUserIdFromToken();
    if (!userId) {
      this.message = 'Utilisateur non authentifié.';
      return;
    }
    this.budget.utilisateur.id = userId;
    this.loadBudgets(userId);
  }

  creerBudget(): void {
    this.budgetService.creerBudget(this.budget).subscribe({
      next: (res) => {
        this.message = 'Budget créé avec succès !';
        this.loadBudgets(this.budget.utilisateur.id);
      },
      error: (err) => {
        this.message = err.message;
        console.error(err);
      }
    });
  }

  loadBudgets(userId: number): void {
    this.budgetService.getBudgets(userId).subscribe({
      next: (data) => {
        this.budgets = data;
      },
      error: (err) => {
        this.message = err.message;
        console.error(err);
      }
    });
  }
}
