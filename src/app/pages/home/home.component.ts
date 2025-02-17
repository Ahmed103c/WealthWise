import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
  imports: [RouterModule]
})
export class HomeComponent {
  features = [
    { icon: '🔐', title: 'Connexion Sécurisée', description: 'Intégrez facilement vos comptes bancaires en toute sécurité.' },
    { icon: '📊', title: 'Suivi Automatisé', description: 'Suivez vos dépenses et revenus en temps réel.' },
    { icon: '💳', title: 'Gestion Multi-Comptes', description: 'Regroupez toutes vos finances en un seul endroit.' },
    { icon: '📈', title: 'Rapports Dynamiques', description: 'Analysez vos finances avec des graphiques interactifs.' }
  ];

  startNow() {
    alert("Redirection vers l'inscription...");
  }
}
