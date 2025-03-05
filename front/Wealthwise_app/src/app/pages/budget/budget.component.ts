import { Component, OnInit } from '@angular/core';
import { BudgetService, Budget, BudgetCategorie, Utilisateur, Category } from '../../services/budget.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-budget',
  templateUrl: './budget.component.html',
  styleUrls: ['./budget.component.scss'],
  imports: [CommonModule, FormsModule]  // pour *ngIf et [(ngModel)]
})
export class BudgetComponent implements OnInit {
  // Propriétés pour la création de budget
  newBudget: Budget = {
    utilisateur: { id: 1 }, // Remplacez par l'utilisateur connecté
    montantAlloue: 0,
    startDate: '',  // valeur initiale en chaîne vide
    endDate: ''     // valeur initiale en chaîne vide
  };

  // Propriétés pour l'allocation d'un budget à une catégorie
  selectedBudgetId: number = 0;
  selectedCategorieId: number = 0;
  allocationMontant: number = 0;

  // Listes affichées
  budgets: Budget[] = [];
  categories: Category[] = [];  // sera chargé depuis le back

  budgetMessage: string = '';

  constructor(private budgetService: BudgetService) {}

  ngOnInit(): void {
    this.loadBudgets();
    this.loadCategories();  // Charger les catégories depuis le back
  }

  // Charge les budgets de l'utilisateur connecté
  loadBudgets(): void {
    const userId = 1; // Remplacez par l'ID de l'utilisateur connecté
    this.budgetService.getBudgets(userId).subscribe({
      next: (data) => {
        this.budgets = data;
      },
      error: (err) => {
        this.budgetMessage = '❌ Erreur lors du chargement des budgets';
        console.error(err);
      }
    });
  }

  // Charge les catégories depuis la base
  loadCategories(): void {
    this.budgetService.getCategories().subscribe({
      next: (data) => {
        console.log("📌 Catégories reçues :", data);  // DEBUG ici !
        this.categories = data;
      },
      error: (err) => {
        this.budgetMessage = '❌ Erreur lors du chargement des catégories';
        console.error("🚨 Erreur API Catégories :", err);
      }
    });
  }
  

  // Créer un nouveau budget
  creerBudget(): void {
    if (this.newBudget.montantAlloue <= 0) {
      this.budgetMessage = '❌ Le montant alloué doit être supérieur à 0';
      return;
    }
    if (!this.newBudget.startDate || !this.newBudget.endDate) {
      this.budgetMessage = '❌ Veuillez renseigner les dates de début et de fin';
      return;
    }

    this.budgetService.creerBudget(this.newBudget).subscribe({
      next: (data) => {
        this.budgetMessage = '✅ Budget créé avec succès';
        this.loadBudgets();
        // Réinitialiser le formulaire
        this.newBudget = {
          utilisateur: { id: 1 },
          montantAlloue: 0,
          startDate: undefined,
          endDate: undefined
        };
      },
      error: (err) => {
        this.budgetMessage = '❌ Erreur: ' + err.message;
        console.error(err);
      }
    });
  }

  // Allouer un montant à une catégorie pour un budget sélectionné
  allouerBudget(): void {
    if (this.selectedBudgetId === 0 || this.selectedCategorieId === 0 || this.allocationMontant <= 0) {
      this.budgetMessage = '❌ Veuillez remplir correctement les champs d\'allocation';
      return;
    }

    this.budgetService.allouerBudget(this.selectedBudgetId, this.selectedCategorieId, this.allocationMontant).subscribe({
      next: (data: BudgetCategorie) => {
        this.budgetMessage = '✅ Allocation réussie';
        this.loadBudgets();
        // Réinitialiser le formulaire d'allocation
        this.selectedBudgetId = 0;
        this.selectedCategorieId = 0;
        this.allocationMontant = 0;
      },
      error: (err) => {
        this.budgetMessage = '❌ Erreur: ' + err.message;
        console.error(err);
      }
    });
  }
}
