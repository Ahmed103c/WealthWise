import { Component, ElementRef, ViewChild, OnInit } from '@angular/core';
import { Chart, ChartConfiguration } from 'chart.js/auto';

@Component({
  selector: 'app-depense-graph',
  templateUrl: './depense-graph.component.html',
  styleUrls: ['./depense-graph.component.scss'],
})
export class DepenseGraphComponent implements OnInit {
  @ViewChild('chart', { static: true }) chart!: ElementRef;

  ngOnInit() {
    const config: ChartConfiguration<'line'> = {
      type: 'line',
      data: {
        labels: ['Lun', 'Mar', 'Mer', 'Jeu', 'Ven', 'Sam', 'Dim'],
        datasets: [
          {
            label: 'Dépenses',
            data: [10, 50, 20, 4, 6, 2, 10],
            borderColor: 'rgb(255,99,132)',
            backgroundColor: 'rgba(255,99,132,0.5)',
            fill: 'start',
          },
          {
            label: 'Gain',
            data: [2, 1, 5, 2, 10, 60, 40],
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
              color: 'whitesmoke', // Applique la couleur aux labels de la légende
            },
          },
        },
        scales: {
          x: {
            ticks: {
              color: 'whitesmoke', // Applique whitesmoke aux labels de l'axe X
            },
          },
          y: {
            ticks: {
              color: 'whitesmoke', // Applique whitesmoke aux labels de l'axe Y
            },
          },
        },
      },
    };

    new Chart(this.chart.nativeElement, config);
  }
}
