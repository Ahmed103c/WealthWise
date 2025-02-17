import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-register',
  standalone: true,
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']  ,// ✅ Correction ici,
  imports: [RouterModule]

})
export class RegisterComponent {}
