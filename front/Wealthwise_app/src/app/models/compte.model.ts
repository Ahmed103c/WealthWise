export interface Compte {
  id: number;
  externalId: string;
  institution: string;
  iban: string;
  currency: string;
  balance: number;
}
