import { Component, ElementRef, ViewChild, OnInit } from '@angular/core';
import { Chart, ChartConfiguration } from 'chart.js/auto';
import { forkJoin } from 'rxjs';
import { AuthService } from '../../../../services/auth.service';
import { TransactionService } from '../../../../services/transaction.service';
import { AlltransactionService } from '../../../../services/dashboardService/alltransaction.service';

@Component({
  selector: 'app-depense-graph',
  templateUrl: './depense-graph.component.html',
  styleUrls: ['./depense-graph.component.scss'],
})
export class DepenseGraphComponent implements OnInit {
  constructor(
    private authservice: AuthService,
    private alltransactionService: AlltransactionService
  ) {}
  transactions: any[] = [];
  /**************************************************************
   *
   *
   *
   *  D'abord on va importer les transactions de l'utilisateur
   *  connecté
   *
   *
   *
   *
   **************************************************************/
  afficherTransactions() {
    //   const transactions = this.alltransactionService.getStoredTransactions();
    //   console.log('📂 Transactions récupérées :', transactions);
    this.alltransactionService
      .getStoredTransactions()
      .subscribe((transactions) => {
        console.log('📂 Transactions mises à jour :', transactions);
      });
  }

  /**************************************************************
   *
   *
   *
   *  D'abord on va importer les transactions de l'utilisateur
   *  connecté
   *
   *
   *
   *
   **************************************************************/

  @ViewChild('chart', { static: true }) chart!: ElementRef;
  chartInstance: Chart | null = null; // Stocke l'instance du graphique

  //   ngOnInit() {
  //     //this.afficherTransactions();
  //     this.alltransactionService.getTransactions(); // Charge les transactions
  //     this.alltransactionService.allTransactions$.subscribe((transactions) => {
  //       console.log('📂 Transactions mises à jour :', transactions);
  //     });
  //     const config: ChartConfiguration<'line'> = {
  //       type: 'line',
  //       data: {
  //         labels: ['Lun', 'Mar', 'Mer', 'Jeu', 'Ven', 'Sam', 'Dim'],
  //         datasets: [
  //           {
  //             label: 'Dépenses',
  //             data: [10, 50, 20, 4, 6, 2, 10],
  //             borderColor: 'rgb(255,99,132)',
  //             backgroundColor: 'rgba(255,99,132,0.5)',
  //             fill: 'start',
  //           },
  //           {
  //             label: 'Gain',
  //             data: [2, 1, 5, 2, 10, 60, 40],
  //             borderColor: 'rgb(0,82,245)',
  //             backgroundColor: 'rgba(0,82,245,0.5)',
  //             fill: 'start',
  //           },
  //         ],
  //       },
  //       options: {
  //         maintainAspectRatio: false,
  //         elements: {
  //           line: {
  //             tension: 0.4,
  //           },
  //         },
  //         plugins: {
  //           legend: {
  //             labels: {
  //               color: 'whitesmoke', // Applique la couleur aux labels de la légende
  //             },
  //           },
  //         },
  //         scales: {
  //           x: {
  //             ticks: {
  //               color: 'whitesmoke', // Applique whitesmoke aux labels de l'axe X
  //             },
  //           },
  //           y: {
  //             ticks: {
  //               color: 'whitesmoke', // Applique whitesmoke aux labels de l'axe Y
  //             },
  //           },
  //         },
  //       },
  //     };

  //     new Chart(this.chart.nativeElement, config);
  //   }
  // }

  ngOnInit() {
    this.alltransactionService.getTransactions(); // Charge les transactions

    this.alltransactionService.allTransactions$.subscribe((transactions) => {
      console.log('📂 Transactions mises à jour :', transactions);

      if (!this.chart || !this.chart.nativeElement) {
        console.error("L'élément canvas du graphique est introuvable !");
        return;
      }
      // ✅ Détruire l'ancien graphique s'il existe déjà
      if (this.chartInstance) {
        this.chartInstance.destroy();
      }

      // Initialisation des tableaux de données
      const labels = ['Lun', 'Mar', 'Mer', 'Jeu', 'Ven', 'Sam', 'Dim'];
      let depenses = new Array(7).fill(0); // 7 jours
      let gains = new Array(7).fill(0);

      // Remplissage des données en fonction des transactions
      transactions.forEach((transaction) => {
        const jourIndex = this.getJourIndex(transaction.transactionDate); // Fonction pour récupérer l'index du jour
        console.log(jourIndex);
        if (transaction.amount < 0) {
          depenses[jourIndex] += transaction.amount;
          // console.log('transaction depense ' + transaction.amount);
        } else {
          gains[jourIndex] += transaction.amount;
          // console.log('transaction gain ' + transaction.amount);
        }
      });

      //console.log('dépenses : ' + depenses);
      // Configuration du graphique
      const config: ChartConfiguration<'line'> = {
        type: 'line',
        data: {
          labels,
          datasets: [
            {
              label: 'Dépenses',
              data: depenses,
              borderColor: 'rgb(255,99,132)',
              backgroundColor: 'rgba(255,99,132,0.5)',
              fill: 'start',
            },
            {
              label: 'Gains',
              data: gains,
              borderColor: 'rgb(0,82,245)',
              backgroundColor: 'rgba(0,82,245,0.5)',
              fill: 'start',
            },
          ],
        },
        options: {
          maintainAspectRatio: false,
          elements: {
            line: {
              tension: 0.4,
            },
          },
          plugins: {
            legend: {
              labels: {
                color: 'whitesmoke',
              },
            },
          },
          scales: {
            x: {
              ticks: {
                color: 'whitesmoke',
              },
            },
            y: {
              ticks: {
                color: 'whitesmoke',
              },
            },
          },
        },
      };

      // new Chart(this.chart.nativeElement, config);
      //  Stocke l'instance du graphique pour pouvoir la détruire plus tard
      this.chartInstance = new Chart(this.chart.nativeElement, config);
    });
  }

  getJourIndex(dateString: string): number {
    const date = new Date(dateString); // Convertir la string en objet Date
    const jour = date.getDay(); // Récupérer le jour (0 = Dimanche, 1 = Lundi, etc.)
    return jour === 0 ? 6 : jour - 1; // Adapter pour ['Lun', 'Mar', ...] (lundi = index 0)
  }
}
