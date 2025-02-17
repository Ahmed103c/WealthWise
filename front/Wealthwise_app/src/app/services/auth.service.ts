import { Injectable } from '@angular/core';
import { HttpClient, HttpParams, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8070/utilisateurs';
  private tokenKey = 'jwtToken';

  constructor(private http: HttpClient) {}

  login(email: string, password: string): Observable<{ token: string }> {
    const url = `${this.apiUrl}/login`;
    const body = new HttpParams()
      .set('email', email)
      .set('password', password);

    const headers = new HttpHeaders({
      'Content-Type': 'application/x-www-form-urlencoded'
    });

    console.log("🔹 Envoi de la requête de login à :", url);
    console.log("🔹 Données envoyées :", body.toString());

    return this.http.post<{ token: string }>(url, body.toString(), { headers });
  }
  register(nom: string, prenom: string, email: string, motDePasse: string): Observable<number> {
    const url = `${this.apiUrl}/`; // URL du backend

    const body = {
      nom: nom,
      prenom: prenom,
      email: email,
      motDePasse: motDePasse
    };

    console.log("🚀 Envoi de la requête de register à :", url);
    console.log("📤 Données envoyées :", body);

    return this.http.post<number>(url, body);
  }



  storeToken(token: string): void {
    localStorage.setItem(this.tokenKey, token);
  }

  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  logout(): void {
    localStorage.removeItem(this.tokenKey);
  }

  isAuthenticated(): boolean {
    return this.getToken() !== null;
  }
}


