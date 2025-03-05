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
  solde: number = 0;
  comptes: any[] = [];
  solde2: number = 0;
  totalSolde: number = 0;
  ngOnInit() {
    this.authservice.getBalanceByUserId().subscribe((data) => {
      this.solde2 = data;
    });
  }
}
