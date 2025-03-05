import {
  Component,
  ViewChild,
  ElementRef,
  AfterViewInit,
  OnChanges,
  SimpleChanges,
  ViewEncapsulation,
} from '@angular/core';
import { Chart, ChartConfiguration, ChartType, LabelItem } from 'chart.js';
import { AllcategoryService } from '../../../../services/dashboardService/allcategory.service';
import { CommonModule } from '@angular/common';
import { AlltransactionService } from '../../../../services/dashboardService/alltransaction.service';
import { Transaction } from '../../../../services/transaction.service';

@Component({
  selector: 'app-depense-pie-chart',
  templateUrl: './depense-pie-chart.component.html',
  styleUrls: ['./depense-pie-chart.component.css'],
  imports: [CommonModule],
  encapsulation: ViewEncapsulation.None,
})
export class DepensePieChartComponent {

  constructor(
    private allcategoryservice: AllcategoryService,
    private alltransactionService: AlltransactionService
  ) {}


  @ViewChild('pieChart', { static: true })
  pieChart!: ElementRef<HTMLCanvasElement>;
  transactions: any[] = [];
  pieChartInstance!: Chart;
  semaineEncours : string ='';
  ngOnInit() {
    this.semaineEncours=this.getCurrentWeek();
    this.alltransactionService.getTransactions(); // Charge les transactions

    this.alltransactionService.allTransactions$.subscribe((transactions) => {
      console.log('📂 Transactions mises à jour :', transactions);
      // this.transactions = transactions;

      const transactionsSemaine =
        this.filterTransactionsByCurrentWeek(transactions);

      const transactionsParCategorie =
        this.groupTransactionsByCategory(transactionsSemaine);
      console.log('📊 Montant total par catégorie :', transactionsParCategorie);

      const transactionsNegatives = transactionsSemaine.filter(
        (t) => t.amount < 0
      );

      // Grouper les montants par catégorie
      const categoriesMap = new Map<string, number>();
      transactionsNegatives.forEach((transaction) => {
        if (transaction.categoryName) {
          categoriesMap.set(
            transaction.categoryName,
            (categoriesMap.get(transaction.categoryName) || 0) +
              Math.abs(transaction.amount)
          );
        }
      });

      // Extraire les labels et data pour le Pie Chart
      // const labels = Array.from(categoriesMap.keys());
      const labels = Array.from(categoriesMap.entries()).map(
        ([category, amount]) => `${category} : ${amount}€`
      );
      const data = Array.from(categoriesMap.values());

      // Vérifier si le canvas est présent
      if (!this.pieChart || !this.pieChart.nativeElement) {
        console.error("L'élément canvas du Pie Chart est introuvable !");
        return;
      }

      // Détruire l'ancien graphique s'il existe déjà
      if (this.pieChartInstance) {
        this.pieChartInstance.destroy();
      }

      // Configurer le Pie Chart
      const config: ChartConfiguration<'pie'> = {
        type: 'pie',
        data: {
          labels,
          datasets: [
            {
              label: 'Dépenses par Catégorie',
              data,
              backgroundColor: [
                'rgba(255, 99, 132, 0.6)',
                'rgba(54, 162, 235, 0.6)',
                'rgba(255, 206, 86, 0.6)',
                'rgba(75, 192, 192, 0.6)',
                'rgba(153, 102, 255, 0.6)',
                'rgba(255, 159, 64, 0.6)',
              ],
              borderWidth: 1,
            },
          ],
        },
        options: {
          maintainAspectRatio: false,
          responsive: true,
          plugins: {
            tooltip: {
              enabled: true,
            },
            legend: {
              position: 'left',
              labels: {
                boxWidth: 50,
                padding: 20,
                color: 'whitesmoke',
                font: {
                  size: 20,
                },
                textAlign: 'left',
              },
            },
          },
        },
      };

      // Initialiser le Pie Chart
      this.pieChartInstance = new Chart(this.pieChart.nativeElement, config);
    });
  }
  getJourIndex(dateString: string): number {
    const date = new Date(dateString); // Convertir la string en objet Date
    const jour = date.getDay(); // Récupérer le jour (0 = Dimanche, 1 = Lundi, etc.)
    return jour === 0 ? 6 : jour - 1; // Adapter pour ['Lun', 'Mar', ...] (lundi = index 0)
  }

  getStartOfWeek(): Date {
    const today = new Date();
    const dayOfWeek = today.getDay(); // 0 = Dimanche, 1 = Lundi, ..., 6 = Samedi
    const diff = dayOfWeek === 0 ? -6 : 1 - dayOfWeek; // Si dimanche, on recule de 6 jours

    const startOfWeek = new Date(today);
    startOfWeek.setDate(today.getDate() + diff); // On ajuste la date pour obtenir le lundi

    startOfWeek.setHours(0, 0, 0, 0); // On remet à minuit pour éviter les erreurs
    return startOfWeek;
  }

  getEndOfWeek(): Date {
    const startOfWeek = this.getStartOfWeek();
    const endOfWeek = new Date(startOfWeek);
    endOfWeek.setDate(startOfWeek.getDate() + 6); // Dimanche = Lundi + 6 jours

    endOfWeek.setHours(23, 59, 59, 999); // Fin de la journée
    return endOfWeek;
  }

  filterTransactionsByCurrentWeek(transactions: any[]): any[] {
    const startOfWeek = this.getStartOfWeek();
    const endOfWeek = this.getEndOfWeek();

    return transactions.filter((transaction) => {
      const transactionDate = new Date(transaction.transactionDate);
      return transactionDate >= startOfWeek && transactionDate <= endOfWeek;
    });
  }
  // Fonction pour regrouper les transactions par catégorie et sommer les montants
  groupTransactionsByCategory(transactions: any[]) {
    const categoryMap = new Map<string, number>();

    transactions
      .filter((transaction) => transaction.amount < 0)
      .forEach((transaction) => {
        if (transaction.categoryName) {
          if (categoryMap.has(transaction.categoryName)) {
            categoryMap.set(
              transaction.categoryName,
              categoryMap.get(transaction.categoryName)! + transaction.amount
            );
          } else {
            categoryMap.set(transaction.categoryName, transaction.amount);
          }
        }
      });

    // Convertir en tableau d'objets [{ categoryName: ..., totalAmount: ... }, ...]
    const groupedTransactions = Array.from(
      categoryMap,
      ([categoryName, totalAmount]) => ({
        categoryName,
        totalAmount,
      })
    );

    return groupedTransactions;
  }
  getCurrentWeek(): string {
    const today = new Date();
    const firstDayOfWeek = new Date(
      today.setDate(today.getDate() - today.getDay() + 1)
    ); // Lundi
    const lastDayOfWeek = new Date(
      today.setDate(today.getDate() - today.getDay() + 7)
    ); // Dimanche
    const options: Intl.DateTimeFormatOptions = {
      day: 'numeric',
      month: 'long',
      year: 'numeric',
    };
    return `${firstDayOfWeek.toLocaleDateString(
      'fr-FR',
      options
    )} - ${lastDayOfWeek.toLocaleDateString('fr-FR', options)}`;
  }
}
