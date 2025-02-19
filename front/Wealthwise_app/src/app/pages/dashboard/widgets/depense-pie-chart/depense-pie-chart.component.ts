// import { Component, ElementRef, viewChild } from '@angular/core';
// import { Chart } from 'chart.js/auto';

// @Component({
//   selector: 'app-depense-pie-chart',
//   imports: [],
//   templateUrl: './depense-pie-chart.component.html',
//   styleUrl: './depense-pie-chart.component.scss',
// })
// export class DepensePieChartComponent {
//   chart = viewChild.required<ElementRef>('chart');

//   ngOnInit() {
//     new Chart(this.chart().nativeElement, {
//       type: 'pie', // Type du graphique, ici un graphique en secteurs (pie chart)
//       data: {
//         labels: ['Alimentation', 'Logement', 'Transport', 'Loisirs', 'Autres'], // Les catégories
//         datasets: [
//           {
//             label: 'Dépenses', // Légende
//             data: [30, 50, 15, 25, 10], // Valeurs associées aux catégories
//             backgroundColor: [
//               // Couleurs pour chaque secteur
//               'rgba(255, 99, 132, 0.7)', // Couleur 1
//               'rgba(54, 162, 235, 0.7)', // Couleur 2
//               'rgba(255, 206, 86, 0.7)', // Couleur 3
//               'rgba(75, 192, 192, 0.7)', // Couleur 4
//               'rgba(153, 102, 255, 0.7)', // Couleur 5
//             ],
//             borderColor: [
//               'rgba(255, 99, 132, 1)', // Bordure 1
//               'rgba(54, 162, 235, 1)', // Bordure 2
//               'rgba(255, 206, 86, 1)', // Bordure 3
//               'rgba(75, 192, 192, 1)', // Bordure 4
//               'rgba(153, 102, 255, 1)', // Bordure 5
//             ],
//             borderWidth: 1, // Largeur de la bordure des secteurs
//           },
//         ],
//       },
//       options: {
//         maintainAspectRatio: false,
//         responsive: true, // Rendre le graphique responsive

//         plugins: {
//           tooltip: {
//             enabled: true, // Afficher les infos au survol
//           },
//           legend: {
//             position: 'left', // Position des labels à droite
//             labels: {
//               boxWidth: 50, // Taille de la boîte pour chaque label
//               padding: 20, // Espacement entre les labels
//               font: {
//                 size: 20, // Taille de la police des labels
//               },
//               textAlign: 'left', // Alignement du texte à gauche
//             },
//           },
//         },
//       },
//     });
//   }
// }
import { Component, ElementRef, ViewChild, OnInit } from '@angular/core';
import { Chart, ChartConfiguration } from 'chart.js/auto';

@Component({
  selector: 'app-depense-pie-chart',
  templateUrl: './depense-pie-chart.component.html',
  styleUrls: ['./depense-pie-chart.component.scss'],
})
export class DepensePieChartComponent implements OnInit {
  @ViewChild('chart', { static: true }) chart!: ElementRef;

  ngOnInit() {
    const config: ChartConfiguration<'pie'> = {
      type: 'pie', // Type de graphique
      data: {
        labels: ['Alimentation', 'Logement', 'Transport', 'Loisirs', 'Autres'],
        datasets: [
          {
            label: 'Dépenses',
            data: [30, 50, 15, 25, 10],
            backgroundColor: [
              'rgba(255, 99, 132, 0.7)', // Rouge
              'rgba(54, 162, 235, 0.7)', // Bleu
              'rgba(255, 206, 86, 0.7)', // Jaune
              'rgba(75, 192, 192, 0.7)', // Turquoise
              'rgba(153, 102, 255, 0.7)', // Violet
            ],
            borderColor: [
              'rgba(255, 99, 132, 1)',
              'rgba(54, 162, 235, 1)',
              'rgba(255, 206, 86, 1)',
              'rgba(75, 192, 192, 1)',
              'rgba(153, 102, 255, 1)',
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
            enabled: true, // Afficher les infos au survol
          },
          legend: {
            position: 'left',
            labels: {
              boxWidth: 50,
              padding: 20,
              color: 'whitesmoke', // Couleur du texte des labels
              font: {
                size: 20,
              },
              textAlign: 'left',
            },
          },
        },
      },
    };

    new Chart(this.chart.nativeElement, config);
  }
}
