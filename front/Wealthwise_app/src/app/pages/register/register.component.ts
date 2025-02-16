import { Component } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';
import {FormsModule} from '@angular/forms';
import {CommonModule} from '@angular/common';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss'],
  imports: [FormsModule, CommonModule] ,// ✅ Ajout de `FormsModule` et `CommonModule`
  standalone: true
})
export class RegisterComponent {
  nom: string = '';
  prenom: string = '';
  email: string = '';
  motDePasse: string = '';
  errorMessage: string = '';

  constructor(private authService: AuthService, private router: Router) {}

  onRegister(): void {
    console.log("📩 Données envoyées :", {
      nom: this.nom,
      prenom: this.prenom,
      email: this.email,
      motDePasse: this.motDePasse
    });

    this.authService.register(this.nom, this.prenom, this.email, this.motDePasse)
      .subscribe({
        next: (id) => {
          console.log("✔ Inscription réussie, ID :", id);
          this.router.navigate(['/login']); // Redirection après inscription
        },
        error: (err) => {
          console.error("❌ Erreur lors de l'inscription :", err);
          this.errorMessage = "Échec de l'inscription. Vérifiez vos informations.";
        }
      });
  }
}

