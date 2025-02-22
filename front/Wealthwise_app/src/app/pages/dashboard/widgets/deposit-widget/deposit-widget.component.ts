import { Component } from '@angular/core';
import { AuthService } from '../../../../services/auth.service';
import { forkJoin } from 'rxjs';

@Component({
  selector: 'app-deposit-widget',
  imports: [],
  templateUrl: './deposit-widget.component.html',
  styleUrl: './deposit-widget.component.scss',
})
export class DepositWidgetComponent {
  // constructor(private authservice: AuthService) {}
  // deposit: number | null = null;
  // transactions: any[] = [];
  // ngOnInit() {
  //   this.authservice.getTransactionsByComptesId(1).subscribe(
  //     (data) => {
  //       console.log('📥 Transactions récupérés :', data);
  //       this.transactions = data;
  //       this.deposit = this.calculateTotalPositiveAmount(this.transactions);
  //     },
  //     (error) => {
  //       console.error('❌ Erreur lors de la récupération des comptes :', error);
  //     }
  //   );
  // }
  // calculateTotalPositiveAmount(transactions: any[]): number {
  //   return transactions
  //     .filter((transaction) => transaction.amount > 0)
  //     .reduce((total, transaction) => total + transaction.amount, 0);
  // }
  constructor(private authservice: AuthService) {}

  deposit: number = 0;
  transactions: any[] = [];

  ngOnInit() {
    this.authservice.getComptesIdsByUserId().subscribe(
      (comptesIds) => {
        console.log('🆔 IDs des comptes récupérés :', comptesIds);

        if (comptesIds.length === 0) {
          console.log('⚠️ Aucun compte trouvé.');
          return;
        }

        // Récupérer les transactions pour chaque compte
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
        // Fusionner toutes les transactions en une seule liste
        this.transactions = transactionsArray.flat();
        console.log('📥 Transactions combinées :', this.transactions);
        
        // Calculer le dépôt total
        this.deposit = this.calculateTotalPositiveAmount(this.transactions);
      },
      (error) => {
        console.error(
          '❌ Erreur lors de la récupération des transactions :',
          error
        );
      }
    );
  }

  calculateTotalPositiveAmount(transactions: any[]): number {
    return transactions
      .filter((transaction) => transaction.amount > 0)
      .reduce((total, transaction) => total + transaction.amount, 0);
  }
}
