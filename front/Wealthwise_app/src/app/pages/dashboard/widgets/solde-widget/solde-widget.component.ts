import { Component } from '@angular/core';
import { AuthService } from '../../../../services/auth.service';

@Component({
  selector: 'app-solde-widget',
  imports: [],
  templateUrl: './solde-widget.component.html',
  styleUrl: './solde-widget.component.scss',
})
export class SoldeWidgetComponent {
  constructor(private authservice: AuthService) {}
  solde: number =0;
  comptes: any[] = [];

  totalSolde: number = 0;
  ngOnInit() {
    this.authservice.getComptesByUserId().subscribe(
      (data) => {
        console.log('📥 Comptes récupérés :', data);
        this.comptes = data;
        this.calculerTotalSolde();
      },
      (error) => {
        console.error('❌ Erreur lors de la récupération des comptes :', error);
      }
    );
  }
  calculerTotalSolde(): void {
    this.totalSolde = this.comptes.reduce(
      (acc, compte) => acc + (compte.balance || 0),
      0
    );
    console.log('💰 Total des soldes :', this.totalSolde);
  }
}
