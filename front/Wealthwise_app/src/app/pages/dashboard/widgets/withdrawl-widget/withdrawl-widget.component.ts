import { Component } from '@angular/core';
import { AuthService } from '../../../../services/auth.service';

@Component({
  selector: 'app-withdrawl-widget',
  imports: [],
  templateUrl: './withdrawl-widget.component.html',
  styleUrl: './withdrawl-widget.component.scss',
})
export class WithdrawlWidgetComponent {
  constructor(private authservice: AuthService) {}
  withdrawl: number | null = null;
  transactions: any[] = [];
  ngOnInit() {
    this.authservice.getTransactionsByComptesId(1).subscribe(
      (data) => {
        console.log('📥 Transactions récupérés :', data);
        this.transactions = data;
        this.withdrawl = this.calculateTotalNegativeAmount(this.transactions);
      },
      (error) => {
        console.error('❌ Erreur lors de la récupération des comptes :', error);
      }
    );
  }
  calculateTotalNegativeAmount(transactions: any[]): number {
    return transactions
      .filter((transaction) => transaction.amount < 0)
      .reduce((total, transaction) => total + transaction.amount, 0);
  }
}
