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

  
  goToRegister() {
    this.router.navigate(['/register']);  // ✅ Redirection vers la page d'inscription
  }
}



