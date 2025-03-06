import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NgChartsModule } from 'ng2-charts';
import { CompteConjointService } from '../../services/compteconjoint.service';
import { AuthService } from '../../services/auth.service';
import { ChartData, ChartOptions } from 'chart.js';

@Component({
  standalone: true,
  selector: 'app-compteconjoint',
  templateUrl: './compteconjoint.component.html',
  styleUrls: ['./compteconjoint.component.scss'],
  imports: [CommonModule, FormsModule, NgChartsModule]
})
export class CompteConjointComponent implements OnInit {

  // Données du formulaire pour créer un compte conjoint
  nouveauCompte = {
    nom: '',
    externalId: 'auto-generated',
    institution: '',
    iban: '',
    currency: 'EUR',
    balance: 100,
    emails: '',  // Exemple: "user1@gmail.com, user2@gmail.com"
    parts: ''    // Exemple: "30,30" (correspond aux parts des co-owners)
  };
  modeCreation: boolean = false;

  comptesConjoints: any[] = [];
  selectedCompte: any = null;

  // Champs pour ajouter un utilisateur
  emailValue: string = '';
  partValue: number = 0;

  // ID de l'utilisateur connecté
  id!: number;

  // Paramètres pour les charts
  pieChartData: ChartData<'pie'> = { labels: [], datasets: [{ data: [] }] };
  pieChartOptions: ChartOptions<'pie'> = {};
  pieChartType: 'pie' = 'pie';

  lineChartData: ChartData<'line'> = { labels: [], datasets: [{ data: [] }] };
  lineChartOptions: ChartOptions<'line'> = {};
  lineChartType: 'line' = 'line';

  constructor(
    private compteConjointService: CompteConjointService,
    private authService: AuthService
  ) { }

  ngOnInit(): void {
    const userId = this.authService.getUserIdFromToken();
    if (userId !== null) {
      this.id = userId;
    } else {
      console.error("Aucun ID utilisateur trouvé dans le token !");
      this.id = 0;
    }
    this.getComptesConjoints();
  }

  creerCompteConjoint(): void {
    // Séparer les emails et les parts par virgule
    const emailsArray = this.nouveauCompte.emails
      .split(',')
      .map(email => email.trim())
      .filter(email => email !== '');
    const partsArrayString = this.nouveauCompte.parts
      .split(',')
      .map(part => part.trim())
      .filter(part => part !== '');

    // Vérifier que le nombre d'emails correspond au nombre de parts
    if (emailsArray.length !== partsArrayString.length) {
      console.error("Le nombre d'emails et de parts doit être identique.");
      return;
    }
    // Convertir chaque part en nombre
    const partsArray = partsArrayString.map(part => Number(part));

    this.compteConjointService.creerCompteConjoint(
      this.nouveauCompte.nom,
      this.nouveauCompte.externalId,
      this.nouveauCompte.institution,
      this.nouveauCompte.iban,
      this.nouveauCompte.currency,
      this.nouveauCompte.balance,
      this.id,               // proprietaireId
      emailsArray,           // emailsUtilisateurs
      partsArray             // partsMontants
    ).subscribe(response => {
      console.log('Compte conjoint créé avec succès :', response);
      this.getComptesConjoints();
      // Réinitialiser le formulaire
      this.nouveauCompte = {
        nom: '',
        externalId: 'auto-generated',
        institution: '',
        iban: '',
        currency: 'EUR',
        balance: 100,
        emails: '',
        parts: ''
      };
    }, error => {
      console.error('Erreur lors de la création du compte conjoint', error);
    });
  }

  getComptesConjoints(): void {
    this.compteConjointService.getComptesConjoints(this.id).subscribe(accounts => {
      // Filtrer les comptes pour ne garder que ceux qui sont joints
      this.comptesConjoints = accounts.filter(account => account.conjoint);
    }, error => {
      console.error('Erreur lors de la récupération des comptes conjoints', error);
    });
  }

  voirDetails(compte: any): void {
    this.selectedCompte = compte;
    this.updateCharts(compte);
  }

  ajouterUtilisateur(): void {
    if (!this.emailValue || !this.partValue || this.partValue <= 0) {
      return;
    }
    this.compteConjointService.ajouterUtilisateur(this.selectedCompte.id, this.emailValue, this.partValue)
      .subscribe(response => {
        this.voirDetails(this.selectedCompte);
        this.getComptesConjoints();
        this.emailValue = '';
        this.partValue = 0;
      }, error => {
        console.error('Erreur lors de l\'ajout d\'un utilisateur', error);
      });
  }

  updateCharts(compte: any): void {
    if (compte && compte.parts) {
      const labels = compte.parts.map((p: any) => p.utilisateur.email);
      const data = compte.parts.map((p: any) => p.pourcentage);
      this.pieChartData = {
        labels,
        datasets: [{ data }]
      };
    }
    // Ajoutez ici la logique pour le line chart, si nécessaire
  }
  ouvrirModalCreation() {
    this.modeCreation = true;
  }

  fermerModalCreation() {
    this.modeCreation = false;
  }
}
