export interface Compte {
  id: number;
  nom: string;
  externalId: string;
  institution: string;
  iban: string;
  currency: string;
  balance: number;
}
