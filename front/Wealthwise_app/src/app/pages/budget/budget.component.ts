import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { BudgetService, Budget } from '../../services/budget.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-budget',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './budget.component.html',
  styleUrl: './budget.component.scss',
})
export class BudgetComponent implements OnInit {
  // Initialisation par défaut pour éviter que "budget" soit undefined lors du rendu
  budget: Budget = {
    utilisateur: { id: 0 },
    montantAlloue: 0,
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
      },
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
      },
    });
  }
}
