import {
  Component,
  ViewChild,
  ElementRef,
  AfterViewInit,
  OnChanges,
  SimpleChanges,
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
})
//AfterViewInit
//implements OnChanges
export class DepensePieChartComponent {
  //@ViewChild('chart') chart!: ElementRef<HTMLCanvasElement>;

  // categories: any[] = [];
  // labels: string[] = [];
  // data: number[] = [];
  // uniqueItems: any[] = [];
  // isloading: boolean = true;
  // chartInstance!: Chart<'pie'>;

  constructor(
    private allcategoryservice: AllcategoryService,
    private alltransactionService: AlltransactionService
  ) {}

  //   ngOnInit() {
  //     this.allcategoryservice.getCategory();
  //     this.allcategoryservice.categories$.subscribe((categories) => {
  //       this.categories = categories;
  //       this.uniqueItems = [
  //         ...new Map(this.categories.map((item) => [item.name, item])).values(),
  //       ];
  //       console.log('✅ Catégories chargées:', this.categories);

  //       if (this.categories.length > 0) {
  //         this.isloading = false;
  //         this.processData(); // Prépare les données du graphique
  //         this.createChart(); // Crée le graphique si le canvas est dispo
  //       }
  //     });
  //   }
  //   ngOnChanges(changes: SimpleChanges) {
  //     if (changes['categories'] && this.categories.length > 0) {
  //       console.log(
  //         '📊 Catégories mises à jour, tentative de création du graphique...'
  //       );

  //       if (this.chart && this.chart.nativeElement) {
  //         console.log('✅ Canvas trouvé immédiatement, création du graphique...');
  //         this.createChart();
  //       } else {
  //         console.warn(
  //           '⚠️ Canvas non encore disponible, attendre via ngAfterViewChecked...'
  //         );
  //       }
  //     }
  //   }

  //   ngAfterViewChecked() {
  //     if (
  //       !this.chartInstance &&
  //       this.chart &&
  //       this.chart.nativeElement &&
  //       this.categories.length > 0
  //     ) {
  //       console.log(
  //         '✅ Canvas trouvé dans ngAfterViewChecked, création du graphique...'
  //       );
  //       this.createChart();
  //     }
  //   }

  //   processData() {
  //     const categoryMap = new Map<string, number>();
  //     this.categories.forEach(({ name, amount }) => {
  //       categoryMap.set(name, (categoryMap.get(name) || 0) + amount);
  //     });

  //     this.labels = Array.from(categoryMap.keys());
  //     this.data = Array.from(categoryMap.values());

  //     console.log('📊 Labels :', this.labels);
  //     console.log('📊 Data :', this.data);
  //   }

  //   createChart() {
  //     if (!this.chart || !this.chart.nativeElement) {
  //       console.error('⚠️ Canvas non disponible, attente...');
  //       return;
  //     }

  //     const config: ChartConfiguration<'pie'> = {
  //       type: 'pie',
  //       data: {
  //         labels: this.labels,
  //         datasets: [
  //           {
  //             label: 'Dépenses',
  //             data: this.data,
  //             backgroundColor: [
  //               'rgba(255, 99, 132, 0.7)',
  //               'rgba(54, 162, 235, 0.7)',
  //               'rgba(255, 206, 86, 0.7)',
  //               'rgba(75, 192, 192, 0.7)',
  //               'rgba(153, 102, 255, 0.7)',
  //             ],
  //             borderColor: [
  //               'rgba(255, 99, 132, 1)',
  //               'rgba(54, 162, 235, 1)',
  //               'rgba(255, 206, 86, 1)',
  //               'rgba(75, 192, 192, 1)',
  //               'rgba(153, 102, 255, 1)',
  //             ],
  //             borderWidth: 1,
  //           },
  //         ],
  //       },
  //       options: {
  //         maintainAspectRatio: false,
  //         responsive: true,
  //       },
  //     };

  //     this.chartInstance = new Chart(this.chart.nativeElement, config);
  //   }
  @ViewChild('pieChart', { static: true })
  pieChart!: ElementRef<HTMLCanvasElement>;
  transactions: any[] = [];
  pieChartInstance!: Chart;
  ngOnInit() {
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
            legend: {
              position: 'top',
              labels: {
                color: 'whitesmoke',
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
}
