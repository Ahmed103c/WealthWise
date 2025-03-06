import { Injectable } from '@angular/core';
import SockJS from 'sockjs-client'; // Import par défaut
import { Client, over } from 'stompjs';
import { BehaviorSubject, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private stompClient!: Client; // Assuré d'être initialisé dans connect()
  private notificationsSubject = new BehaviorSubject<any[]>([]);

  constructor() {
    this.connect();
  }

  private connect(): void {
    const socket = new SockJS('http://localhost:8070/ws');
    this.stompClient = over(socket);

    this.stompClient.connect({}, (frame: any) => {
      console.log('Connecté au WebSocket: ' + frame);
      // Abonnez-vous au topic pour l'utilisateur (exemple: userId = 1)
      this.stompClient.subscribe('/topic/notifications/1', (message: any) => {
        if (message.body) {
          const notif = JSON.parse(message.body);
          this.addNotification(notif);
        }
      });
    }, (error: any) => {
      console.error('Erreur WebSocket:', error);
    });
  }

  private addNotification(notification: any): void {
    const current = this.notificationsSubject.getValue();
    current.push(notification);
    this.notificationsSubject.next(current);
  }

  public getNotifications(): Observable<any[]> {
    return this.notificationsSubject.asObservable();
  }
}
