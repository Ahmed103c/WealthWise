import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NotificationService } from '../../../../services/notifications.service';

@Component({
  selector: 'app-notification',
  standalone: true,  // Déclare le composant comme standalone
  imports: [CommonModule],  // Importe CommonModule pour accéder à ngIf, ngFor, etc.
  template: `
    <div *ngIf="notifications.length > 0">
      <h3>Notifications</h3>
      <ul>
        <li *ngFor="let notif of notifications">
          {{ notif.message }} <small>({{ notif.type }})</small>
        </li>
      </ul>
    </div>
  `,
  styles: []
})
export class NotificationComponent implements OnInit {
  notifications: any[] = [];

  constructor(private notificationService: NotificationService) {}

  ngOnInit(): void {
    this.notificationService.getNotifications().subscribe(data => {
      this.notifications = data;
    });
  }
}

