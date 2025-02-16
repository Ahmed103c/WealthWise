import { Component } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms'; // ✅ Pour `ngModel`
import { CommonModule } from '@angular/common'; // ✅ Pour `*ngIf`

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
  standalone: true, // ✅ Angular 19 standalone component
  imports: [FormsModule, CommonModule] // ✅ Ajout de `FormsModule` et `CommonModule`
})

export class LoginComponent {
  email = '';
  password = '';
  errorMessage = '';

  constructor(private authService: AuthService, private router: Router) {}

  onLogin() {
    this.authService.login(this.email, this.password).subscribe({
      next: (response) => {
        console.log("✅ Réponse reçue :", response); // ✅ Vérifier le token reçu

        if (response && response.token) {
          localStorage.setItem('token', response.token); // ✅ Stocker le token
          this.router.navigate(['/dashboard']); // ✅ Rediriger vers le dashboard
        } else {
          this.errorMessage = "Réponse invalide du serveur";
        }
      },
      error: (err) => {
        console.log("❌ Erreur lors du login :", err);
        this.errorMessage = "Email ou mot de passe incorrect"; // ✅ Afficher un message d'erreur
      }
    });
  }
}







