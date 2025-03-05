import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserService } from '../../services/userservice.service';
import { AuthService } from '../../services/auth.service';
import { CompteService } from '../../services/compte.service';
import { ChangeDetectorRef } from '@angular/core';

@Component({
  selector: 'app-infoperso',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './infoperso.component.html',
  styleUrls: ['./infoperso.component.scss'],
})
export class InfopersoComponent implements OnInit {
  user: any = {}; // Informations utilisateur
  comptes: any[] = []; // Liste des comptes
  message: string = '';
  modeAjout: boolean = false; // Afficher ou cacher le formulaire d'ajout manuel
  modeGoCardless: boolean = false; // Afficher ou cacher la méthode GoCardless
  authenticationLink: string = ''; // Lien GoCardless
  requisitionId: string | null = null; // ✅ Variable interne (pas de stockage persistant)

  // Initialisation des données pour le formulaire
  nouveauCompte = {
    utilisateur: { id: 0 },
    nom: '',
    externalId: '',
    institution: '',
    iban: '',
    currency: 'EUR',
    balance: 0,
  };

  constructor(
    private authService: AuthService,
    private userService: UserService,
    private compteService: CompteService,
    private cdr: ChangeDetectorRef // ✅ Pour forcer la mise à jour de l'affichage
  ) {}

  ngOnInit(): void {
    const userId = this.authService.getUserIdFromToken();
    console.log('🔍 User ID récupéré :', userId);

    if (!userId) {
      this.message = 'Utilisateur non authentifié.';
      console.error("❌ Alerte : L'ID utilisateur est undefined !");
      return;
    }

    // ✅ Associer l'ID utilisateur à this.user
    this.user = { id: userId };

    // ✅ Associer aussi à this.nouveauCompte
    this.nouveauCompte.utilisateur.id = userId;

    console.log('✅ Initialisation this.nouveauCompte :', this.nouveauCompte);

    // ✅ Charger les données utilisateur et comptes
    this.loadUserData(userId);
  }

  // Charger les informations de l'utilisateur et ses comptes
  loadUserData(userId: number): void {
    this.userService.getUserProfile(userId).subscribe({
      next: (data) => {
        this.user = {
          id: userId, // ✅ Ajouter l'ID utilisateur ici !
          nom: data.nom,
          prenom: data.prenom,
          email: data.email,
          balance: data.balance || 0,
        };
        console.log('✅ Balance après mise à jour :', this.user.balance);
        console.log('✅ Utilisateur mis à jour :', this.user); // Debug
        this.loadComptes(userId);
      },
      error: (err) => {
        this.message = err.message;
        console.error(err);
      },
    });
  }

  // Charger les comptes bancaires
  loadComptes(userId: number): void {
    this.compteService.getComptesByUserId(userId).subscribe({
      next: (data) => {
        console.log('✅ Mise à jour de la liste des comptes :', data);
        this.comptes = data || []; // ✅ Mise à jour immédiate

        // ✅ Forcer Angular à voir la modification

        this.cdr.detectChanges();
      },
      error: (err) => {
        this.message = err.message;
        console.error('❌ Erreur lors du chargement des comptes', err);
      },
    });
  }

  // Ajouter un compte manuellement
  ajouterCompteManuel(): void {
    console.log("📌 Vérification avant l'envoi :", this.nouveauCompte);

    if (!this.user || !this.user.id || this.user.id === 0) {
      console.error('🚨 Erreur : ID utilisateur invalide :', this.user);
      alert(
        "Erreur : Impossible d'ajouter le compte, l'utilisateur n'est pas valide."
      );
      return;
    }

    // ✅ Associer le bon ID utilisateur avant l’envoi
    this.nouveauCompte.utilisateur.id = this.user.id;

    console.log('📌 Données envoyées :', this.nouveauCompte); // Debug

    this.compteService.ajouterCompteManuel(this.nouveauCompte).subscribe({
      next: () => {
        this.message = '✅ Compte ajouté avec succès !';
        this.modeAjout = false;
        this.loadComptes(this.nouveauCompte.utilisateur.id);
        this.loadUserData(this.nouveauCompte.utilisateur.id);
        this.toggleAjoutCompte();
      },
      error: (err) => {
        console.error("❌ Erreur lors de l'ajout du compte :", err);
      },
    });
  }

  authentifierAvecGoCardless() {
    console.log(
      "🔍 Vérification this.user avant l'authentification :",
      this.user
    );

    if (!this.user || !this.user.id) {
      console.error("🚨 Erreur : L'ID utilisateur est introuvable !");
      alert('Erreur : Votre session utilisateur est invalide.');
      return;
    }

    console.log('✅ User ID confirmé :', this.user.id);

    this.userService.authenticateWithGoCardless(this.user.id).subscribe(
      (response: any) => {
        if (response.authLink && response.requisitionId) {
          this.requisitionId = response.requisitionId;
          console.log('✅ Requisition ID stocké :', this.requisitionId);
          console.log('📌 Ouverture de GoCardless :', response.authLink);

          const newTab = window.open(response.authLink, '_blank');

          if (
            !newTab ||
            newTab.closed ||
            typeof newTab.closed === 'undefined'
          ) {
            alert('🚨 Pop-up bloquée ! Autorisez les pop-ups.');
          }
        } else {
          console.error('⚠ Erreur : AuthLink ou RequisitionId manquant.');
        }
      },
      (error) => {
        console.error("❌ Erreur lors de l'authentification GoCardless", error);
      }
    );
  }

  verifierEtRecupererComptes() {
    if (!this.requisitionId) {
      alert("⚠ Veuillez d'abord vous authentifier avec GoCardless !");
      return;
    }

    console.log('🔍 Vérification du Requisition ID :', this.requisitionId);

    this.userService
      .fetchAccountsFromGoCardless(this.requisitionId, this.user.id)
      .subscribe(
        (response: any) => {
          console.log('✅ Réponse reçue du backend :', response);

          if (!response || !response.accounts) {
            console.error('❌ Erreur : Réponse JSON invalide !');
            return;
          }

          // ✅ Recharger les comptes
          this.loadComptes(this.user.id);

          // ✅ Forcer la mise à jour de l'affichage
          this.cdr.detectChanges();
        },
        (error) => {
          console.error('❌ Erreur lors de la récupération des comptes', error);
        }
      );
  }

  toggleAjoutCompte() {
    if (this.modeAjout) {
      // ✅ Réinitialiser tous les champs y compris utilisateur
      this.nouveauCompte = {
        utilisateur: { id: 0 },
        nom: '',
        externalId: '',
        institution: '',
        iban: '',
        currency: '',
        balance: 0,
      };
    }

    this.modeAjout = !this.modeAjout;
  }

  saveChanges() {
    alert('Modifications enregistrées avec succès !');
  }
}
