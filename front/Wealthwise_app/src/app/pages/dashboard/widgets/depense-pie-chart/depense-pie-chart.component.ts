// // import { Component, ElementRef, viewChild } from '@angular/core';
// // import { Chart } from 'chart.js/auto';

// // @Component({
// //   selector: 'app-depense-pie-chart',
// //   imports: [],
// //   templateUrl: './depense-pie-chart.component.html',
// //   styleUrl: './depense-pie-chart.component.scss',
// // })
// // export class DepensePieChartComponent {
// //   chart = viewChild.required<ElementRef>('chart');

// //   ngOnInit() {
// //     new Chart(this.chart().nativeElement, {
// //       type: 'pie', // Type du graphique, ici un graphique en secteurs (pie chart)
// //       data: {
// //         labels: ['Alimentation', 'Logement', 'Transport', 'Loisirs', 'Autres'], // Les catégories
// //         datasets: [
// //           {
// //             label: 'Dépenses', // Légende
// //             data: [30, 50, 15, 25, 10], // Valeurs associées aux catégories
// //             backgroundColor: [
// //               // Couleurs pour chaque secteur
// //               'rgba(255, 99, 132, 0.7)', // Couleur 1
// //               'rgba(54, 162, 235, 0.7)', // Couleur 2
// //               'rgba(255, 206, 86, 0.7)', // Couleur 3
// //               'rgba(75, 192, 192, 0.7)', // Couleur 4
// //               'rgba(153, 102, 255, 0.7)', // Couleur 5
// //             ],
// //             borderColor: [
// //               'rgba(255, 99, 132, 1)', // Bordure 1
// //               'rgba(54, 162, 235, 1)', // Bordure 2
// //               'rgba(255, 206, 86, 1)', // Bordure 3
// //               'rgba(75, 192, 192, 1)', // Bordure 4
// //               'rgba(153, 102, 255, 1)', // Bordure 5
// //             ],
// //             borderWidth: 1, // Largeur de la bordure des secteurs
// //           },
// //         ],
// //       },
// //       options: {
// //         maintainAspectRatio: false,
// //         responsive: true, // Rendre le graphique responsive

// //         plugins: {
// //           tooltip: {
// //             enabled: true, // Afficher les infos au survol
// //           },
// //           legend: {
// //             position: 'left', // Position des labels à droite
// //             labels: {
// //               boxWidth: 50, // Taille de la boîte pour chaque label
// //               padding: 20, // Espacement entre les labels
// //               font: {
// //                 size: 20, // Taille de la police des labels
// //               },
// //               textAlign: 'left', // Alignement du texte à gauche
// //             },
// //           },
// //         },
// //       },
// //     });
// //   }
// // }
// import {
//   Component,
//   ElementRef,
//   ViewChild,
//   OnInit,
//   AfterViewInit,
//   OnChanges,
//   SimpleChange,
// } from '@angular/core';
// import { Chart, ChartConfiguration } from 'chart.js/auto';
// import { AuthService } from '../../../../services/auth.service';
// import { AllcategoryService } from '../../../../services/dashboardService/allcategory.service';
// import { CommonModule } from '@angular/common';
// import { finalize, tap } from 'rxjs';

// @Component({
//   selector: 'app-depense-pie-chart',
//   templateUrl: './depense-pie-chart.component.html',
//   styleUrls: ['./depense-pie-chart.component.scss'],
//   imports: [CommonModule],
// })
// export class DepensePieChartComponent implements OnInit {
//   category: string = '';
//   categories: any[] = [];
//   uniqueItems: any[] = [];
//   labels: string[] = []; // ✅ Définir labels
//   data: number[] = []; // ✅ Définir data
//   isloading: boolean = true;

//   @ViewChild('chart', { static: true }) chart!: ElementRef;
//   chartInstance: Chart | null = null;
//   constructor(
//     private authservice: AuthService,
//     private allcategoryservice: AllcategoryService
//   ) {
//     console.log('Composant construit ');
//   }

//   // ngOnInit() {
//   //   // this.authservice.getCategoryFromDescription('tacos').subscribe((data) => {
//   //   //   this.category = data.name;
//   //   //   console.log('category est : ' + data.name);
//   //   // });

//   //   this.allcategoryservice.getCategory(); // Déclenche la récupération des catégories

//   //   this.allcategoryservice.categories$.subscribe((categories) => {
//   //     this.categories = categories; // Met à jour automatiquement l'affichage
//   //     this.uniqueItems = [
//   //       ...new Map(this.categories.map((item) => [item.name, item])).values(),
//   //     ];

//   //     console.log('📂 Catégories mises à jour :', this.categories);

//   //     if (this.categories.length > 0) {
//   //       this.isloading = false; // Désactive le chargement UNIQUEMENT si on a des données
//   //     }
//   //     // 🔹 Étape 1 : Regrouper les montants par catégorie
//   //     const categoryMap = new Map<string, number>();

//   //     categories.forEach((category) => {
//   //       const categoryName = category.name; // Extraire le nom
//   //       const amount = category.amount;

//   //       if (categoryMap.has(categoryName)) {
//   //         categoryMap.set(
//   //           categoryName,
//   //           categoryMap.get(categoryName)! + amount
//   //         );
//   //       } else {
//   //         categoryMap.set(categoryName, amount);
//   //       }
//   //     });

//   //     // 🔹 Étape 2 : Transformer les données pour Chart.js
//   //     const labels = Array.from(categoryMap.keys());
//   //     const data = Array.from(categoryMap.values());

//   //     // 🔹 Étape 3 : Créer le Pie Chart
//   //     const config: ChartConfiguration<'pie'> = {
//   //       type: 'pie',
//   //       data: {
//   //         labels: labels, // 🎯 Utiliser les noms des catégories
//   //         datasets: [
//   //           {
//   //             label: 'Dépenses',
//   //             data: data, // 🎯 Utiliser les montants totalisés
//   //             backgroundColor: [
//   //               'rgba(255, 99, 132, 0.7)', // Rouge
//   //               'rgba(54, 162, 235, 0.7)', // Bleu
//   //               'rgba(255, 206, 86, 0.7)', // Jaune
//   //               'rgba(75, 192, 192, 0.7)', // Turquoise
//   //               'rgba(153, 102, 255, 0.7)', // Violet
//   //             ],
//   //             borderColor: [
//   //               'rgba(255, 99, 132, 1)',
//   //               'rgba(54, 162, 235, 1)',
//   //               'rgba(255, 206, 86, 1)',
//   //               'rgba(75, 192, 192, 1)',
//   //               'rgba(153, 102, 255, 1)',
//   //             ],
//   //             borderWidth: 1,
//   //           },
//   //         ],
//   //       },
//   //       options: {
//   //         maintainAspectRatio: false,
//   //         responsive: true,
//   //         plugins: {
//   //           tooltip: { enabled: true },
//   //           legend: {
//   //             position: 'left',
//   //             labels: {
//   //               boxWidth: 50,
//   //               padding: 20,
//   //               color: 'whitesmoke',
//   //               font: { size: 20 },
//   //               textAlign: 'left',
//   //             },
//   //           },
//   //         },
//   //       },
//   //     };

//   //     // new Chart(this.chart.nativeElement, config);
//   //     this.chartInstance = new Chart(this.chart.nativeElement, config);
//   //   });

//   /*********************************************************
//    *
//    *  on va tester toutes les catégories  des transactions
//    *
//    *********************************************************/

//   // const config: ChartConfiguration<'pie'> = {
//   //   type: 'pie', // Type de graphique
//   //   data: {
//   //     labels: ['Alimentation', 'Logement', 'Transport', 'Loisirs', 'Autres'],
//   //     datasets: [
//   //       {
//   //         label: 'Dépenses',
//   //         data: [30, 50, 15, 25, 10],
//   //         backgroundColor: [
//   //           'rgba(255, 99, 132, 0.7)', // Rouge
//   //           'rgba(54, 162, 235, 0.7)', // Bleu
//   //           'rgba(255, 206, 86, 0.7)', // Jaune
//   //           'rgba(75, 192, 192, 0.7)', // Turquoise
//   //           'rgba(153, 102, 255, 0.7)', // Violet
//   //         ],
//   //         borderColor: [
//   //           'rgba(255, 99, 132, 1)',
//   //           'rgba(54, 162, 235, 1)',
//   //           'rgba(255, 206, 86, 1)',
//   //           'rgba(75, 192, 192, 1)',
//   //           'rgba(153, 102, 255, 1)',
//   //         ],
//   //         borderWidth: 1,
//   //       },
//   //     ],
//   //   },
//   //   options: {
//   //     maintainAspectRatio: false,
//   //     responsive: true,
//   //     plugins: {
//   //       tooltip: {
//   //         enabled: true, // Afficher les infos au survol
//   //       },
//   //       legend: {
//   //         position: 'left',
//   //         labels: {
//   //           boxWidth: 50,
//   //           padding: 20,
//   //           color: 'whitesmoke', // Couleur du texte des labels
//   //           font: {
//   //             size: 20,
//   //           },
//   //           textAlign: 'left',
//   //         },
//   //       },
//   //     },
//   //   },
//   // };

//   // new Chart(this.chart.nativeElement, config);
//   // }

//   ngOnInit() {
//     this.allcategoryservice.getCategory();

//     this.allcategoryservice.categories$.subscribe((categories) => {
//       this.categories = categories;
//       console.log(this.categories);
//       if (this.categories.length > 0) {
//         this.isloading = false;
//       }

//       // Regrouper les montants par catégorie
//       const categoryMap = new Map<string, number>();
//       categories.forEach(({ name, amount }) => {
//         categoryMap.set(name, (categoryMap.get(name) || 0) + amount);
//       });
//       console.log('category map ' + categoryMap);
//       this.labels = Array.from(categoryMap.keys());
//       console.log('labels' + this.labels);
//       this.data = Array.from(categoryMap.values());
//       console.log('data' + this.data);
//       if (this.chartInstance) {
//         this.createChart();
//       } else {
//         console.error('❌ Canvas non trouvé !');
//       }

//     });
//   }

//   // ngAfterViewInit() {
//   //   console.log('🔥 ngAfterViewInit() exécuté !');

//   //   if (!this.chart) {
//   //     console.error('⚠️ Erreur : Canvas non trouvé !');
//   //     return;
//   //   }

//   //   if (!this.isloading) {
//   //     this.createChart();
//   //   }
//   //   else{
//   //     console.log("Ahmed "+this.isloading);

//   //   }
//   // }

//   createChart() {
//     console.log('📊 Création du graphique...');

//     const config: ChartConfiguration<'pie'> = {
//       type: 'pie',
//       data: {
//         labels: this.labels,
//         datasets: [
//           {
//             label: 'Dépenses',
//             data: this.data,
//             backgroundColor: [
//               'rgba(255, 99, 132, 0.7)', // Rouge
//               'rgba(54, 162, 235, 0.7)', // Bleu
//               'rgba(255, 206, 86, 0.7)', // Jaune
//               'rgba(75, 192, 192, 0.7)', // Turquoise
//               'rgba(153, 102, 255, 0.7)', // Violet
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
//     console.log('✅ Graphique généré !');
//   }
// }
import { Component, ViewChild, ElementRef, AfterViewInit, OnChanges, SimpleChanges } from '@angular/core';
import { Chart, ChartConfiguration } from 'chart.js';
import { AllcategoryService } from '../../../../services/dashboardService/allcategory.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-depense-pie-chart',
  templateUrl: './depense-pie-chart.component.html',
  styleUrls: ['./depense-pie-chart.component.css'],
  imports: [CommonModule],
})
//AfterViewInit
export class DepensePieChartComponent implements OnChanges {
  @ViewChild('chart') chart!: ElementRef<HTMLCanvasElement>;

  categories: any[] = [];
  labels: string[] = [];
  data: number[] = [];
  uniqueItems: any[] = [];
  isloading: boolean = true;
  chartInstance!: Chart<'pie'>;

  constructor(private allcategoryservice: AllcategoryService) {}

  ngOnInit() {
    this.allcategoryservice.getCategory();
    this.allcategoryservice.categories$.subscribe((categories) => {
      this.categories = categories;
      this.uniqueItems = [
        ...new Map(this.categories.map((item) => [item.name, item])).values(),
      ];
      console.log('✅ Catégories chargées:', this.categories);

      if (this.categories.length > 0) {
        this.isloading = false;
        this.processData(); // Prépare les données du graphique
        this.createChart(); // Crée le graphique si le canvas est dispo
      }
    });
  }

  // ngAfterViewInit() {
  //   console.log('🔥 ngAfterViewInit() exécuté !');

  //   if (!this.chart) {
  //     console.error(
  //       '❌ Canvas non trouvé immédiatement, tentative de récupération...'
  //     );
  //     setTimeout(() => {
  //       if (this.chart && this.chart.nativeElement) {
  //         console.log(
  //           '✅ Canvas trouvé après attente :',
  //           this.chart.nativeElement
  //         );
  //         this.createChart(); // Maintenant, le canvas est dispo !
  //       } else {
  //         console.error('❌ Échec : Canvas toujours non trouvé après attente.');
  //       }
  //     }, 20000); // ⏳ Attendre 500ms avant de réessayer
  //     return;
  //   }

  //   console.log('✅ Canvas trouvé :', this.chart.nativeElement);
  //   this.createChart();
  // }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['categories'] && this.categories.length > 0) {
      console.log(
        '📊 Catégories mises à jour, tentative de création du graphique...'
      );

      if (this.chart && this.chart.nativeElement) {
        console.log('✅ Canvas trouvé immédiatement, création du graphique...');
        this.createChart();
      } else {
        console.warn(
          '⚠️ Canvas non encore disponible, attendre via ngAfterViewChecked...'
        );
      }
    }
  }

  ngAfterViewChecked() {
    if (
      !this.chartInstance &&
      this.chart &&
      this.chart.nativeElement &&
      this.categories.length > 0
    ) {
      console.log(
        '✅ Canvas trouvé dans ngAfterViewChecked, création du graphique...'
      );
      this.createChart();
    }
  }

  processData() {
    const categoryMap = new Map<string, number>();
    this.categories.forEach(({ name, amount }) => {
      categoryMap.set(name, (categoryMap.get(name) || 0) + amount);
    });

    this.labels = Array.from(categoryMap.keys());
    this.data = Array.from(categoryMap.values());

    console.log('📊 Labels :', this.labels);
    console.log('📊 Data :', this.data);
  }

  createChart() {
    if (!this.chart || !this.chart.nativeElement) {
      console.error('⚠️ Canvas non disponible, attente...');
      return;
    }

    const config: ChartConfiguration<'pie'> = {
      type: 'pie',
      data: {
        labels: this.labels,
        datasets: [
          {
            label: 'Dépenses',
            data: this.data,
            backgroundColor: [
              'rgba(255, 99, 132, 0.7)',
              'rgba(54, 162, 235, 0.7)',
              'rgba(255, 206, 86, 0.7)',
              'rgba(75, 192, 192, 0.7)',
              'rgba(153, 102, 255, 0.7)',
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
      },
    };

    this.chartInstance = new Chart(this.chart.nativeElement, config);
  }
}
