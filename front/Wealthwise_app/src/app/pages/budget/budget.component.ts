import { Component, OnInit } from '@angular/core';
import { BudgetService, Budget } from '../../services/budget.service';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { NgFor, NgIf } from '@angular/common';
import { NgChartsModule } from 'ng2-charts'; // ✅ Correct import pour Angular 19 standalone
import { ChartConfiguration, ChartType } from 'chart.js';

@Component({
  selector: 'app-budget',
  standalone: true,
  templateUrl: './budget.component.html',
  styleUrls: ['./budget.component.scss'],
  imports: [FormsModule, ReactiveFormsModule, NgFor, NgIf, NgChartsModule] // ✅ Correct imports
})
export class BudgetComponent implements OnInit {
  budgetTotal: number = 0;
  budgets: any[] = [];
  categories: { nom: string; montant: number }[] = [];
  utilisateurId: number = 1; // 🔥 Remplace avec l'ID utilisateur réel
  message: string = '';

  // ✅ Données du graphique
  pieChartData: ChartConfiguration<'pie'>['data'] = {
    labels: [],
    datasets: [{ data: [], backgroundColor: ['#845162', '#29104A', '#522C5D', '#D88EA2'] }]
  };
  pieChartType: ChartType = 'pie';

  constructor(
    private authService: AuthService,
    private budgetService: BudgetService,
    private router: Router
  ) {}

  ngOnInit() {
    this.loadBudgets();
  }

  // ✅ Charger les budgets
  loadBudgets() {
    this.budgetService.getBudgets(this.utilisateurId).subscribe({
      next: (data) => {
        if (Array.isArray(data)) {
          this.budgets = data;
        } else {
          this.message = "⚠️ Aucun budget trouvé.";
        }
      },
      error: (err) => {
        this.message = "❌ Erreur lors du chargement des budgets.";
        console.error(err);
      }
    });
  }

  // ✅ Créer un budget
  creerBudget() {
    const budget: Budget = {
      utilisateur: { id: this.utilisateurId },
      montantAlloue: this.budgetTotal
    };

    this.budgetService.creerBudget(budget).subscribe({
      next: (res) => {
        this.message = "✅ Budget créé avec succès !";
        this.budgets.push(res);
        this.loadBudgets();
      },
      error: (err) => {
        this.message = "❌ Erreur lors de la création du budget.";
        console.error(err);
      }
    });
  }

  ajouterCategorie() {
    this.categories.push({ nom: '', montant: 0 });
    this.updateChart();
  }

  supprimerCategorie(index: number) {
    this.categories.splice(index, 1);
    this.updateChart();
  }

  updateChart() {
    this.pieChartData.labels = this.categories.map(c => c.nom);
    this.pieChartData.datasets[0].data = this.categories.map(c => c.montant);
  }
}
