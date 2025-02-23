// import { Injectable } from '@angular/core';
// import { AuthService } from '../auth.service';
// import { AlltransactionService } from './alltransaction.service';
// import { BehaviorSubject, forkJoin } from 'rxjs';

// @Injectable({
//   providedIn: 'root',
// })
// export class AllcategoryService {
//   transactions: any[] = [];
//   categories: any[] = [];

//   constructor(
//     private authservice: AuthService,
//     private alltransaction: AlltransactionService
//   ) {}

//   getCategory() {
//     this.alltransaction.getStoredTransactions().subscribe((transactions) => {
//       this.transactions = transactions; // Stocke les transactions
//       this.categories = []; // Réinitialise les catégories

//       if (transactions.length === 0) {
//         console.log('⚠️ Aucune transaction disponible.');
//         return;
//       }

//       const categoryObservables = transactions.map((transaction) =>
//         this.authservice.getCategoryFromDescription(transaction.description)
//       );

//       forkJoin(categoryObservables).subscribe(
//         (categories) => {
//           this.categories = categories;
//           console.log('📂 Catégories récupérées :', this.categories);
//         },
//         (error) => {
//           console.error(
//             '❌ Erreur lors de la récupération des catégories :',
//             error
//           );
//         }
//       );
//     });
//   }
// }

import { Injectable } from '@angular/core';
import { BehaviorSubject, forkJoin } from 'rxjs';
import { AuthService } from '../auth.service';
import { AlltransactionService } from './alltransaction.service';

@Injectable({
  providedIn: 'root',
})
export class AllcategoryService {
  private categoriesSubject = new BehaviorSubject<any[]>([]); // Stockage réactif des catégories
  categories$ = this.categoriesSubject.asObservable(); // Observable pour le composant

  constructor(
    private authservice: AuthService,
    private alltransaction: AlltransactionService
  ) {}

  // getCategory() {
  //   this.alltransaction.getStoredTransactions().subscribe((transactions) => {
  //     if (transactions.length === 0) {
  //       console.log('⚠️ Aucune transaction disponible.');
  //       this.categoriesSubject.next([]); // Met à jour l'observable avec un tableau vide
  //       return;
  //     }

  //     const categoryObservables = transactions.map((transaction) =>
  //       this.authservice.getCategoryFromDescription(transaction.description)
  //     );

  //     forkJoin(categoryObservables).subscribe(
  //       (categories) => {
  //         this.categoriesSubject.next(categories); // Met à jour les catégories
  //         console.log('📂 Catégories récupérées :', categories);
  //       },
  //       (error) => {
  //         console.error(
  //           '❌ Erreur lors de la récupération des catégories :',
  //           error
  //         );
  //         this.categoriesSubject.next([]);
  //       }
  //     );
  //   });
  // }
 
  getCategory() {
    this.alltransaction.getStoredTransactions().subscribe((transactions) => {
      if (transactions.length === 0) {
        console.log('⚠️ Aucune transaction disponible.');
        this.categoriesSubject.next([]); // Met à jour l'observable avec un tableau vide
        return;
      }

      // 🎯 Ne garder que les transactions négatives (dépenses)
      const expenseTransactions = transactions.filter((t) => t.amount < 0);

      if (expenseTransactions.length === 0) {
        console.log('⚠️ Aucune dépense trouvée.');
        this.categoriesSubject.next([]);
        return;
      }

      const categoryObservables = expenseTransactions.map((transaction) =>
        this.authservice.getCategoryFromDescription(transaction.description)
      );

      forkJoin(categoryObservables).subscribe(
        (categories) => {
          // 🎯 Associer chaque catégorie avec le montant de la transaction
          const categorizedTransactions = categories.map((category, index) => ({
            name: category.name, // 🛠️ Corrige ici pour extraire uniquement le nom de la catégorie
            amount: Math.abs(expenseTransactions[index].amount), // Convertir en positif
          }));

          this.categoriesSubject.next(categorizedTransactions);
          console.log(
            '📂 Catégories récupérées avec montant:',
            categorizedTransactions
          );
        },
        (error) => {
          console.error(
            '❌ Erreur lors de la récupération des catégories :',
            error
          );
          this.categoriesSubject.next([]);
        }
      );
    });
  }
}
