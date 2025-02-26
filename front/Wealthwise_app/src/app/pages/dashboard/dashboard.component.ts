import { Component, inject } from '@angular/core';
import { Router, RouterLinkActive, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { WidgetComponent } from '../../components/widget/widget.component';
import { Widget } from '../../models/dashboardWidget';
import { DashboardService } from '../../services/dashboardService/dashboard.service';
import { ChatbotComponent } from "./widgets/chatbot/chatbot.component";

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss'],
  providers: [DashboardService],
  imports: [CommonModule, RouterLinkActive, RouterModule, WidgetComponent, ChatbotComponent],
  standalone: true,
})
export class DashboardComponent {
  // data: Wigdet = {
  //   id: 1,
  //   label: 'Subscribers',
  //   content: SubscribersComponent,
  // };
  store = inject(DashboardService);
  showChatbot = false;

  toggleChatbot() {
    this.showChatbot = !this.showChatbot;
  }
}
