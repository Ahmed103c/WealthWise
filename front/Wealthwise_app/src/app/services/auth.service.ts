import { Injectable } from '@angular/core';
import { HttpClient, HttpParams, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import {jwtDecode } from 'jwt-decode';  // Import correct de jwt-decode (export par défaut)

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private apiUrl = 'http://localhost:8070/utilisateurs';
  private tokenKey = 'jwtToken';

  constructor(private http: HttpClient) {}

  // Connexion de l'utilisateur
  login(email: string, password: string): Observable<{ token: string }> {
    const url = `${this.apiUrl}/login`;
    const body = new HttpParams().set('email', email).set('password', password);
    const headers = new HttpHeaders({
      'Content-Type': 'application/x-www-form-urlencoded',
    });
    console.log('🔹 Envoi de la requête de login à :', url);
    console.log('🔹 Données envoyées :', body.toString());
    return this.http.post<{ token: string }>(url, body.toString(), { headers });
  }

  // Inscription de l'utilisateur
  register(
    nom: string,
    prenom: string,
    email: string,
    motDePasse: string
  ): Observable<number> {
    const url = `${this.apiUrl}/`;
    const body = { nom, prenom, email, motDePasse };
    console.log('🚀 Envoi de la requête de register à :', url);
    console.log('📤 Données envoyées :', body);
    return this.http.post<number>(url, body);
  }

  // Extraction de l'email depuis le token
  getEmailFromToken(): string | null {
    const token = this.getToken();
    if (token) {
      try {
        const decoded: any = jwtDecode(token);
        console.log('🔹 Token décodé :', decoded);
        return decoded.sub || null;
      } catch (error) {
        console.error('❌ Erreur lors du décodage du token :', error);
        return null;
      }
    }
    return null;
  }

  // Extraction de l'ID utilisateur depuis le token (si inclus)
  getUserIdFromToken(): number | null {
    const token = this.getToken();
    if (token) {
      try {
        const decoded: any = jwtDecode(token);
        return decoded.userId || null;
      } catch (error) {
        console.error('❌ Erreur lors du décodage du token (userId) :', error);
        return null;
      }
    }
    return null;
  }

  // Stocke le token dans le localStorage
  storeToken(token: string): void {
    console.log('📥 Stockage du token dans localStorage :', token);
    localStorage.setItem(this.tokenKey, token);
  }

  // Récupère le token depuis le localStorage
  getToken(): string | null {
    const token = localStorage.getItem(this.tokenKey);
    console.log('🔹 Token récupéré depuis le localStorage :', token);
    return token;
  }

  // Déconnexion
  logout(): void {
    localStorage.removeItem(this.tokenKey);
  }

  // Vérifie si l'utilisateur est authentifié
  isAuthenticated(): boolean {
    return this.getToken() !== null;
  }
  getComptesByUserId(): Observable<any> {
    const url = `http://localhost:8070/api/comptes/utilisateur/${this.getUserIdFromToken()}`;

    console.log(
      "Récupération des comptes pour l'utilisateur ID :",
      this.getUserIdFromToken()
    );

    return this.http.get<any>(url);
  }
  getTransactionsByComptesId(comptesId: number): Observable<any> {
    const url = `http://localhost:8070/transactions/compte/${comptesId}`;

    console.log('Récupération des transcations pour compte ID :', comptesId);

    return this.http.get<any>(url);
  }
}
