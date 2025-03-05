export interface Compte {
  nom:string;
  id: number;
  externalId: string;
  institution: string;
  iban: string;
  currency: string;
  balance: number;
}
