import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private apiUrl = 'http://localhost:8070/utilisateurs'; 

  private http = inject(HttpClient); 

  /**
   * Effectue une requête POST pour authentifier l'utilisateur.
   * @param email Email de l'utilisateur
   * @param motDePasse Mot de passe de l'utilisateur
   * @returns Un Observable contenant le token JWT ou un message d'erreur
   */
  login(email: string, motDePasse: string): Observable<{ token: string }> {
    return this.http.post<{ token: string }>(`${this.apiUrl}/login`, { email, motDePasse });
  }

  /**
   * Déconnecte l'utilisateur en supprimant son token de session.
   */
  logout(): void {
    localStorage.removeItem('userToken'); // Suppression du token stocké
  }

  /**
   * Vérifie si l'utilisateur est connecté.
   * @returns `true` si l'utilisateur a un token valide, `false` sinon.
   */
  isLoggedIn(): boolean {
    return !!localStorage.getItem('userToken');
  }

  /**
   * Stocke le token dans le localStorage après un login réussi.
   * @param token Le token JWT reçu du backend.
   */
  setToken(token: string): void {
    localStorage.setItem('userToken', token);
  }
}
