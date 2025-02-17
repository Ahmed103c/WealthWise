import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import {Router, RouterModule} from '@angular/router';
import {FormsModule} from '@angular/forms';


@Component({
  selector: 'app-infoperso',
  standalone: true,  // 🔹 Important pour activer l'import direct
  templateUrl: './infoperso.component.html',
  styleUrls: ['./infoperso.component.scss'],
  imports: [CommonModule, RouterModule,FormsModule]  // 🔹 Ajout des modules ici
})
export class InfopersoComponent {
  user = {
    fullName: 'Jean Dupont',
    email: 'jean.dupont@email.com',
    phone: '+33 6 12 34 56 78',
    accountType: 'Compte Premium',
    balance: 15230.75,
    iban: 'FR76 3000 4000 5000 6000 7890 123',
    verified: true
  };

  // Etat des champs en mode édition ou non
  editMode: { [key: string]: boolean } = {
    fullName: false,
    email: false,
    phone: false
  };


  // Permet de basculer entre mode affichage et édition
  toggleEdit(field: string) {
    this.editMode[field] = !this.editMode[field];
  }

  // Fonction pour sauvegarder les modifications
  saveChanges() {
    alert('Modifications enregistrées avec succès !');
  }
}



