import { Component, ElementRef, ViewChild } from '@angular/core';
import { Chart, ChartConfiguration } from 'chart.js';
import { AlltransactionService } from '../../../services/dashboardService/alltransaction.service';

@Component({
  selector: 'app-statisitque-category',
  imports: [],
  templateUrl: './statisitque-category.component.html',
  styleUrl: './statisitque-category.component.scss',
})
export class StatisitqueCategoryComponent {
  constructor(private alltransactionService: AlltransactionService) {}

  @ViewChild('pieChart', { static: true })
  pieChart!: ElementRef<HTMLCanvasElement>;
  transactions: any[] = [];
  pieChartInstance!: Chart;
  ngOnInit() {
    this.alltransactionService.getTransactions(); // Charge les transactions

    this.alltransactionService.allTransactions$.subscribe((transactions) => {
      console.log('📂 Transactions mises à jour :', transactions);
      // this.transactions = transactions;

      const transactionsParCategorie =
        this.groupTransactionsByCategory(transactions);
      console.log('📊 Montant total par catégorie :', transactionsParCategorie);

      const transactionsNegatives = transactions.filter((t) => t.amount < 0);

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
      const labels = Array.from(categoriesMap.keys());
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
}
