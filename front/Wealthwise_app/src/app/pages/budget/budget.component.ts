import { Component } from '@angular/core';
import { BudgetService } from '../../services/budget.service';
import { FormsModule } from '@angular/forms'; // ✅ Ajoute FormsModule

@Component({
  selector: 'app-budget',
  templateUrl: './budget.component.html',
  styleUrls: ['./budget.component.scss'],
  imports:[FormsModule]
})
export class BudgetComponent {
  montantAlloue: number = 500; // Valeur par défaut
  utilisateurId: number = 1; // ⚠️ ID utilisateur à changer si besoin
  startDate: string = "2025-02-18";
  endDate: string = "2025-02-18";

  constructor(private budgetService: BudgetService) {}

  // ✅ Fonction pour créer un budget
  creerBudget() {
    this.budgetService.creerBudget(this.utilisateurId, this.montantAlloue, this.startDate, this.endDate).subscribe(
      response => {
        console.log('✅ Budget créé avec succès :', response);
        alert('Budget créé avec succès !');
      },
      error => {
        console.error('❌ Erreur lors de la création du budget :', error);
        alert('Erreur lors de la création du budget.');
      }
    );
  }
}
