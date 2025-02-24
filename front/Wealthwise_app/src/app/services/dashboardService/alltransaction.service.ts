import { Injectable } from '@angular/core';
import { AuthService } from '../auth.service';
import { forkJoin, BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class AlltransactionService {
  private allTransactionsSubject = new BehaviorSubject<any[]>([]);
  allTransactions$ = this.allTransactionsSubject.asObservable(); // Observable pour écouter les changements

  constructor(private authservice: AuthService) {}

  getTransactions() {
    this.authservice.getComptesIdsByUserId().subscribe(
      (comptesIds) => {
        console.log('🆔 IDs des comptes récupérés :', comptesIds);

        if (comptesIds.length === 0) {
          console.log('⚠️ Aucun compte trouvé.');
          return;
        }

        this.fetchTransactionsForAllComptes(comptesIds);
      },
      (error) => {
        console.error('❌ Erreur lors de la récupération des comptes :', error);
      }
    );
  }

  fetchTransactionsForAllComptes(comptesIds: number[]) {
    const transactionObservables = comptesIds.map((id) =>
      this.authservice.getTransactionsByComptesId(id)
    );

    forkJoin(transactionObservables).subscribe(
      (transactionsArray) => {
        const allTransactions = transactionsArray.flat();
        this.allTransactionsSubject.next(allTransactions); // Met à jour l'Observable
        console.log('💰 Transactions récupérées :', allTransactions);
      },
      (error) => {
        console.error(
          '❌ Erreur lors de la récupération des transactions :',
          error
        );
      }
    );
  }

  getStoredTransactions() {
    return this.allTransactions$;
  }
}
