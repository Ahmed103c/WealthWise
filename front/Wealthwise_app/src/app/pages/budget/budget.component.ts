import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-budget',
  standalone: true,
  templateUrl: './budget.component.html',
  styleUrls: ['./budget.component.scss'],
})
export class BudgetComponent {
  budgetTotal: number = 0;
  categories: { nom: string; montant: number }[] = [];
  conseils: string = 'Veuillez entrer un budget total.';

  constructor(private http: HttpClient) {}

  ajouterCategorie() {
    this.categories.push({ nom: '', montant: 0 });
    this.mettreAJourConseils();
  }

  supprimerCategorie(index: number) {
    this.categories.splice(index, 1);
    this.mettreAJourConseils();
  }

  mettreAJourConseils() {
    let sommeBudgets = this.categories.reduce((total, cat) => total + (cat.montant || 0), 0);

    if (this.budgetTotal === 0) {
      this.conseils = 'Veuillez entrer un budget total.';
      return;
    }

    if (sommeBudgets > this.budgetTotal) {
      this.conseils = '⚠️ Attention, vous avez dépassé votre budget total !';
    } else {
      this.conseils = `💡 Vous avez encore ${this.budgetTotal - sommeBudgets}€ disponibles.`;
    }
  }

  calculerProgression(): number {
    let sommeBudgets = this.categories.reduce((total, cat) => total + (cat.montant || 0), 0);
    return this.budgetTotal > 0 ? (sommeBudgets / this.budgetTotal) * 100 : 0;
  }

  sauvegarderBudget() {
    const budgetData = {
      budgetTotal: this.budgetTotal,
      categories: this.categories,
    };

    this.http.post('http://localhost:8080/api/budget', budgetData).subscribe(
      (response) => {
        console.log('Budget sauvegardé avec succès !', response);
        alert('Budget sauvegardé avec succès !');
      },
      (error) => {
        console.error('Erreur lors de la sauvegarde du budget', error);
      }
    );
  }
}
