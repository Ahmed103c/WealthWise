import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-notification',
  imports: [],
  standalone: true,
  templateUrl: './notification.component.html',
  styleUrl: './notification.component.scss',
})
export class NotificationComponent {
  @Input() message: string = 'Aucune notification reçue'; // Valeur par défaut pour éviter les erreurs
}
