import { Component } from '@angular/core';
import { NotificationComponent } from './notification/notification.component';

@Component({
  selector: 'app-notifications',
  imports: [NotificationComponent],
  templateUrl: './notifications.component.html',
  styleUrl: './notifications.component.scss',
})
export class NotificationsComponent {
  notifications = [
    { id: 1, message: 'Votre compte a été vérifié' },
    { id: 2, message: 'Vous avez un nouveau message' },
    { id: 3, message: 'Nouvelle mise à jour disponible' },
    { id: 4, message: 'Suspect de Fraude !' },
    { id: 5, message: 'Nouvelle transcation' },
  ];
}
