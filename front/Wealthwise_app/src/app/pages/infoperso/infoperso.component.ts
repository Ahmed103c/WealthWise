import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import {Router, RouterModule} from '@angular/router';


@Component({
  selector: 'app-infoperso',
  standalone: true,  // 🔹 Important pour activer l'import direct
  templateUrl: './infoperso.component.html',
  styleUrls: ['./infoperso.component.scss'],
  imports: [CommonModule, RouterModule]  // 🔹 Ajout des modules ici
})
export class InfopersoComponent {
}

