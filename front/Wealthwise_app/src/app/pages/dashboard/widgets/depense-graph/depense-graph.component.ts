import { Component, ElementRef, viewChild } from '@angular/core';
import { Chart } from 'chart.js/auto';
@Component({
  selector: 'app-depense-graph',
  imports: [],
  templateUrl: './depense-graph.component.html',
  styleUrl: './depense-graph.component.scss',
})
export class DepenseGraphComponent {
  chart = viewChild.required<ElementRef>('chart');
  ngOnInit() {
    new Chart(this.chart().nativeElement, {
      type: 'line',
      data: {
        labels: ['Lun', 'Mar', 'Mer', 'Jeu', 'Ven', 'Sam', 'Dim'],
        datasets: [
          {
            label: 'dépenses',
            data: [10, 50, 20, 4, 6, 2, 10],
            borderColor: 'rgb(255,99,132)',
            backgroundColor: 'rgb(255,99,132,0.5)',
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
      },
    });
  }
}
