import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class CompteService {
  private apiUrl = 'http://localhost:8070/api/comptes';
  private goCardlessUrl = 'http://localhost:8070/api/gocardless';
  private http = inject(HttpClient);

  constructor() {}

  /**
   * Récupérer les comptes de l'utilisateur
   * @param userId L'ID de l'utilisateur
   * @returns Une liste des comptes bancaires
   */
  getComptesByUserId(userId: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/utilisateur/${userId}`);
  }

  /**
   * Ajouter un compte manuellement avec toutes les informations
   * @param compte Données du compte à ajouter
   * @returns Un Observable de confirmation
   */
  ajouterCompteManuel(compte: any): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}`, compte);
  }

  /**
   * Démarrer l'authentification GoCardless pour récupérer les comptes
   * @param userId L'ID de l'utilisateur
   * @returns Un lien d'authentification GoCardless
   */
  authentifierAvecGoCardless(userId: number): Observable<string> {
    return this.http.post<string>(`${this.apiUrl}/authenticate/${userId}`, {});
  }

  /**
   * Récupérer les comptes depuis GoCardless après authentification
   * @param requisitionId ID de la requête après validation GoCardless
   * @param userId L'ID de l'utilisateur
   * @returns Liste des comptes liés à l'utilisateur
   */
  fetchComptesDepuisGoCardless(requisitionId: string, userId: number): Observable<any> {
    return this.http.post<any>(
      `${this.apiUrl}/fetch-accounts/${requisitionId}/${userId}`,
      {}
    );
  }
}