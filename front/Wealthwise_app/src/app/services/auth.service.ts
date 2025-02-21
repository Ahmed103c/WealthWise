import { Injectable } from '@angular/core';
import { HttpClient, HttpParams, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { jwtDecode } from 'jwt-decode';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private apiUrl = 'http://localhost:8070/utilisateurs';
  private tokenKey = 'jwtToken';

  constructor(private http: HttpClient) {}

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
  register(
    nom: string,
    prenom: string,
    email: string,
    motDePasse: string
  ): Observable<number> {
    const url = `${this.apiUrl}/`; // URL du backend

    const body = {
      nom: nom,
      prenom: prenom,
      email: email,
      motDePasse: motDePasse,
    };

    console.log('🚀 Envoi de la requête de register à :', url);
    console.log('📤 Données envoyées :', body);

    return this.http.post<number>(url, body);
  }
  getEmailFromToken(): string | null {
    const token = this.getToken();
    if (token) {
      try {
        const decoded: any = jwtDecode(token); // Utilisation correcte de jwtDecode
        console.log('🔹 Token décodé :', decoded);
        return decoded.sub || null;
      } catch (error) {
        console.error('❌ Erreur lors du décodage du token :', error);
        return null;
      }
    }
    return null;
  }

  // getSolde() :number | null{

  // }
  getComptesByUserId(userId: number): Observable<any> {
    const url = `http://localhost:8070/api/comptes/utilisateur/${userId}`;

    console.log("🔹 Récupération des comptes pour l'utilisateur ID :", userId);

    return this.http.get<any>(url);
  }

  storeToken(token: string): void {
    console.log('📥 Stockage du token dans localStorage :', token);
    localStorage.setItem(this.tokenKey, token);
  }

  getToken(): string | null {
    const token = localStorage.getItem(this.tokenKey);
    console.log('🔹 Token récupéré depuis le localStorage :', token);
    return token;
  }

  logout(): void {
    localStorage.removeItem(this.tokenKey);
  }

  isAuthenticated(): boolean {
    return this.getToken() !== null;
  }
}
