import { Component,OnInit} from '@angular/core';
import { CommonModule } from '@angular/common';
import {Router, RouterModule} from '@angular/router';
import {AuthService} from '../../services/auth.service';
import {HttpClient} from '@angular/common/http';




@Component({
  selector: 'app-navbar',
  standalone: true,  // 🔹 Important pour activer l'import direct
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss'],
  imports: [CommonModule, RouterModule] // 🔹 Ajout des modules ici
})
export class NavbarComponent implements OnInit {
  constructor(private router: Router,private authService: AuthService) {}
  navLinks = [
    { path: '/main/dashboard', label: 'Dashboard', icon: 'fas fa-chart-line' }, // 📊 Représente un tableau de bord
    { path: '/teams', label: 'Transactions', icon: 'fas fa-exchange-alt' }, // 🔄 Icône pour transactions financières
    { path: '/employees', label: 'Analyses & Statistiques', icon: 'fas fa-chart-pie' }, // 📈 Graphique circulaire pour statistiques
    { path: '/projects', label: 'Gestion de Budget', icon: 'fas fa-wallet' }, // 💰 Portefeuille pour budget
    { path: '/meetings', label: 'Historique', icon: 'fas fa-history' }, // ⏳ Horloge pour historique
    { path: '/main/infoperso', label: 'Informations Personnelles', icon: 'fas fa-id-card' }, // 🆔 Carte d'identité pour infos perso
    { path: '/settings', label: 'Réglages', icon: 'fas fa-cogs' } // ⚙️ Engrenages pour réglages
  ];
  userEmail: string | null = null;

  ngOnInit() {
    this.userEmail = this.authService.getEmailFromToken();
    console.log("👤 Utilisateur connecté :", this.userEmail);
  }
  logout() {
    localStorage.removeItem('token'); // Supprime le token JWT
    this.router.navigate(['/login']); // Redirige vers la page de connexion
  }

}


