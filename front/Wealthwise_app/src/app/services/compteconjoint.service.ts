
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class CompteConjointService {
  private apiUrl = 'http://localhost:8070/api/comptes';

  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) { }

  private getHeaders(): HttpHeaders {
    return new HttpHeaders({
      'Content-Type': 'application/json', // Le Content-Type peut rester en JSON, même si on envoie des query params
      'Authorization': `Bearer ${this.authService.getToken()}`
    });
  }

  /**
   * Crée un compte conjoint en passant tous les champs en query params
   */
  creerCompteConjoint(
    nom: string,
    externalId: string,
    institution: string,
    iban: string,
    currency: string | null,
    balance: number | null,
    proprietaireId: number,
    emailsUtilisateurs: string[],
    partsMontants: number[]
  ): Observable<any> {
    // Construire les query params
    let params = new HttpParams()
      .set('nom', nom)
      .set('externalId', externalId)
      .set('institution', institution)
      .set('iban', iban)
      .set('proprietaireId', proprietaireId.toString());

    // currency et balance étant optionnels, on les ajoute seulement s'ils sont définis
    if (currency) {
      params = params.set('currency', currency);
    }
    if (balance !== null) {
      params = params.set('balance', balance.toString());
    }

    // Ajouter chaque emailUtilisateurs dans les query params
    emailsUtilisateurs.forEach(email => {
      params = params.append('emailsUtilisateurs', email);
    });

    // Ajouter chaque partsMontants dans les query params
    partsMontants.forEach(part => {
      params = params.append('partsMontants', part.toString());
    });

    // Effectuer l'appel en POST, avec un body vide ou {}
    return this.http.post<any>(
      `${this.apiUrl}/conjoint`,
      {},
      { headers: this.getHeaders(), params }
    );
  }

  // ... autres méthodes (getComptesConjoints, ajouterUtilisateur, etc.)
  
  // Ajoute un utilisateur à un compte conjoint
  ajouterUtilisateur(compteId: number, email: string, partMontant: number): Observable<any> {
    const payload = { email, partMontant };
    return this.http.post<any>(`${this.apiUrl}/${compteId}/ajouter-utilisateur`, payload, { headers: this.getHeaders() });
  }

  // Récupère les comptes conjoints pour un utilisateur donné
  getComptesConjoints(userId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/utilisateur/${userId}`, { headers: this.getHeaders() });
  }
}
