import { Component } from '@angular/core';
import {Router, RouterLinkActive,RouterModule} from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss'],
  imports: [CommonModule, RouterLinkActive,RouterModule],
  standalone: true
})
export class DashboardComponent {

  constructor(private router: Router) {}
  navLinks = [
    { path: '/dashboard', label: 'Dashboard', icon: 'fas fa-chart-line' }, // 📊 Représente un tableau de bord
    { path: '/teams', label: 'Transactions', icon: 'fas fa-exchange-alt' }, // 🔄 Icône pour transactions financières
    { path: '/employees', label: 'Analyses & Statistiques', icon: 'fas fa-chart-pie' }, // 📈 Graphique circulaire pour statistiques
    { path: '/projects', label: 'Gestion de Budget', icon: 'fas fa-wallet' }, // 💰 Portefeuille pour budget
    { path: '/meetings', label: 'Historique', icon: 'fas fa-history' }, // ⏳ Horloge pour historique
    { path: '/infoperso', label: 'Informations Personnelles', icon: 'fas fa-id-card' }, // 🆔 Carte d'identité pour infos perso
    { path: '/settings', label: 'Réglages', icon: 'fas fa-cogs' } // ⚙️ Engrenages pour réglages
  ];

  logout() {
    localStorage.removeItem('token'); // Supprime le token JWT
    this.router.navigate(['/login']); // Redirige vers la page de connexion
  }
}

