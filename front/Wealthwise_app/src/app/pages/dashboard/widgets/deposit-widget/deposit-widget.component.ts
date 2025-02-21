import { Component } from '@angular/core';
import { AuthService } from '../../../../services/auth.service';

@Component({
  selector: 'app-deposit-widget',
  imports: [],
  templateUrl: './deposit-widget.component.html',
  styleUrl: './deposit-widget.component.scss',
})
export class DepositWidgetComponent {
  constructor(private authservice: AuthService) {}
  deposit: number | null = null;
  transactions: any[] = [];
  ngOnInit() {
    this.authservice.getTransactionsByComptesId(1).subscribe(
      (data) => {
        console.log('📥 Transactions récupérés :', data);
        this.transactions = data;
        this.deposit = this.calculateTotalPositiveAmount(this.transactions);
      },
      (error) => {
        console.error('❌ Erreur lors de la récupération des comptes :', error);
      }
    );
  }
  calculateTotalPositiveAmount(transactions: any[]): number {
    return transactions
      .filter((transaction) => transaction.amount > 0)
      .reduce((total, transaction) => total + transaction.amount, 0);
  }
}
