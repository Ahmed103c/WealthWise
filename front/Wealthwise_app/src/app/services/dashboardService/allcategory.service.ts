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
  getCategory() {
    this.alltransaction.getStoredTransactions().subscribe((transactions) => {
      if (transactions.length === 0) {
        console.log('⚠️ Aucune transaction disponible.');
        this.categoriesSubject.next([]); // Met à jour l'observable avec un tableau vide
        return;
      }

      const transactionsSemaine =
        this.filterTransactionsByCurrentWeek(transactions);

      // 🎯 Ne garder que les transactions négatives (dépenses)
      const expenseTransactions = transactionsSemaine.filter(
        (t) => t.amount < 0
      );

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

  /**
   * 🔹 Filtre les transactions pour ne garder que celles de la semaine en cours
   */
  filterTransactionsByCurrentWeek(transactions: any[]): any[] {
    const startOfWeek = this.getStartOfWeek();
    const endOfWeek = this.getEndOfWeek();

    return transactions.filter((transaction) => {
      const transactionDate = new Date(transaction.transactionDate);
      return transactionDate >= startOfWeek && transactionDate <= endOfWeek;
    });
  }

  /**
   * 🔹 Renvoie la date du lundi de la semaine en cours
   */
  getStartOfWeek(): Date {
    const today = new Date();
    const dayOfWeek = today.getDay();
    const diff = dayOfWeek === 0 ? -6 : 1 - dayOfWeek;

    const startOfWeek = new Date(today);
    startOfWeek.setDate(today.getDate() + diff);
    startOfWeek.setHours(0, 0, 0, 0);
    return startOfWeek;
  }

  /**
   * 🔹 Renvoie la date du dimanche de la semaine en cours
   */
  getEndOfWeek(): Date {
    const startOfWeek = this.getStartOfWeek();
    const endOfWeek = new Date(startOfWeek);
    endOfWeek.setDate(startOfWeek.getDate() + 6);
    endOfWeek.setHours(23, 59, 59, 999);
    return endOfWeek;
  }
}
