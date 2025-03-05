import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { AlltransactionService } from '../../../services/dashboardService/alltransaction.service';
import { Chart, ChartConfiguration } from 'chart.js';

@Component({
  selector: 'app-statistique-depense-gain',
  imports: [],
  templateUrl: './statistique-depense-gain.component.html',
  styleUrl: './statistique-depense-gain.component.scss',
})
export class StatistiqueDepenseGainComponent implements OnInit {
  transactions: any[] = [];

  constructor(private alltransactionService: AlltransactionService) {}
  @ViewChild('chartMensuel', { static: false })
  chartMensuel!: ElementRef<HTMLCanvasElement>;
  chartInstanceMensuel!: Chart;

  ngOnInit() {
    this.alltransactionService.getTransactions();
    this.alltransactionService.allTransactions$.subscribe((transactions) => {
      console.log('📂 Transactions mises à jour :', transactions);

      // Transactions sur plusieurs mois
      const { labels, depenses, gains } =
        this.filterTransactionsByMonths(transactions);
      console.log('Transactions par mois:', labels, depenses, gains);

      if (!this.chartMensuel || !this.chartMensuel.nativeElement) {
        console.error(
          "L'élément canvas du graphique mensuel est introuvable !"
        );
        return;
      }

      // ✅ Détruire l'ancien graphique mensuel s'il existe
      if (this.chartInstanceMensuel) {
        this.chartInstanceMensuel.destroy();
      }

      /************************************************************************************************* */

      // Trier les labels et ajuster les données en conséquence
      const sortedIndices = labels
        .map((_, i) => i)
        .sort(
          (a, b) =>
            new Date(labels[a]).getTime() - new Date(labels[b]).getTime()
        );

      const sortedLabels = sortedIndices.map((i) => labels[i]);
      const sortedDepenses = sortedIndices.map((i) => depenses[i]);
      const sortedGains = sortedIndices.map((i) => gains[i]);

      const configMensuel: ChartConfiguration<'bar'> = {
        type: 'bar',
        data: {
          labels: sortedLabels,
          datasets: [
            {
              label: 'Dépenses',
              data: sortedDepenses,
              backgroundColor: 'rgba(255, 99, 132, 0.6)',
              borderColor: 'rgb(255, 99, 132)',
              borderWidth: 1,
              barThickness: 40,
            },
            {
              label: 'Gains',
              data: sortedGains,
              backgroundColor: 'rgba(0, 82, 245, 0.6)',
              borderColor: 'rgb(0, 82, 245)',
              borderWidth: 1,
              barThickness: 40,
            },
          ],
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          scales: {
            x: {
              ticks: {
                color: 'whitesmoke',
                font: { size: 14 },
              },
              grid: {
                color: 'rgba(255, 255, 255, 0.2)',
              },
            },
            y: {
              beginAtZero: true,
              ticks: {
                color: 'whitesmoke',
                font: { size: 14 },
              },
              grid: {
                color: (ctx) =>
                  ctx.tick.value === 0 ? 'white' : 'rgba(255, 255, 255, 0.2)', // Mettre y=0 en blanc
                lineWidth: (ctx) => (ctx.tick.value === 0 ? 2 : 1), // Épaissir la ligne y=0
              },
            },
          },
          plugins: {
            legend: {
              position: 'top',
              labels: {
                color: 'whitesmoke',
                font: { size: 16 },
              },
            },
            tooltip: {
              backgroundColor: 'rgba(0,0,0,0.8)',
              titleFont: { size: 14 },
              bodyFont: { size: 12 },
            },
          },
        },
      };

      /************************************************************************************************* */
      this.chartInstanceMensuel = new Chart(
        this.chartMensuel.nativeElement,
        configMensuel
      );
    });
  }
  filterTransactionsByMonths(transactions: any[]): {
    labels: string[];
    depenses: number[];
    gains: number[];
  } {
    const transactionsByMonth: {
      [key: string]: { depenses: number; gains: number; dateKey: string };
    } = {};

    transactions.forEach((transaction) => {
      const date = new Date(transaction.transactionDate);
      const mois = date.toLocaleString('fr-FR', {
        month: 'short',
        year: 'numeric',
      }); // Ex: "Janv. 2024"

      const dateKey = date.toISOString().slice(0, 7); // Format "YYYY-MM" pour trier

      if (!transactionsByMonth[mois]) {
        transactionsByMonth[mois] = { depenses: 0, gains: 0, dateKey };
      }

      if (transaction.amount < 0) {
        transactionsByMonth[mois].depenses += transaction.amount;
      } else {
        transactionsByMonth[mois].gains += transaction.amount;
      }
    });

    // const labels = Object.keys(transactionsByMonth);
    // const depenses = labels.map((mois) => transactionsByMonth[mois].depenses);
    // const gains = labels.map((mois) => transactionsByMonth[mois].gains);
    // Trier les labels par date réelle

    const sortedEntries = Object.entries(transactionsByMonth).sort(
      ([, a], [, b]) => a.dateKey.localeCompare(b.dateKey)
    );

    const labels = sortedEntries.map(([mois]) => mois);
    const depenses = sortedEntries.map(([, data]) => data.depenses);
    const gains = sortedEntries.map(([, data]) => data.gains);

    return { labels, depenses, gains };
  }
}
