import { Component,OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import {Router, RouterModule} from '@angular/router';
import {FormsModule} from '@angular/forms';
import { UserService } from '../../services/user.service';


@Component({
  selector: 'app-infoperso',
  standalone: true,  // 🔹 Important pour activer l'import direct
  templateUrl: './infoperso.component.html',
  styleUrls: ['./infoperso.component.scss'],
  imports: [CommonModule, RouterModule,FormsModule]  // 🔹 Ajout des modules ici
})
export class InfopersoComponent implements OnInit {
  user: any = {}; // Contiendra les informations de l'utilisateur
  comptes: any[] = []; // Contiendra la liste des comptes

  constructor(private userService: UserService) {}

  ngOnInit() {
    this.loadUserProfile();
  }

  loadUserProfile() {
    this.userService.getUserProfile().subscribe(
      (data) => {
        this.user = {
          nom: data.nom,
          prenom: data.prenom,
          email: data.email
        };
        this.comptes = data.comptes || []; // Assurez-vous que "comptes" est bien un tableau
      },
      (error) => {
        console.error('Erreur lors du chargement du profil utilisateur', error);
      }
    );
  }

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



