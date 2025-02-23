import { Compte } from './compte.model'; // ✅ Import du modèle Compte

export interface Utilisateur {
  id: number;
  nom: string;
  prenom: string;
  email: string;
  comptes: Compte[]; // ✅ Plus d'erreur ici
}
