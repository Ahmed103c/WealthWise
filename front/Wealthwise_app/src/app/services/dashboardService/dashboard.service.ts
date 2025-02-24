import { Injectable, signal } from '@angular/core';
import { Widget } from '../../models/dashboardWidget';
import { SoldeWidgetComponent } from '../../pages/dashboard/widgets/solde-widget/solde-widget.component';
import { DepositWidgetComponent } from '../../pages/dashboard/widgets/deposit-widget/deposit-widget.component';
import { WithdrawlWidgetComponent } from '../../pages/dashboard/widgets/withdrawl-widget/withdrawl-widget.component';
import { GrowthWidgetComponent } from '../../pages/dashboard/widgets/growth-widget/growth-widget.component';
import { DepensePieChartComponent } from '../../pages/dashboard/widgets/depense-pie-chart/depense-pie-chart.component';
import { GoalsComponent } from '../../pages/dashboard/widgets/goals/goals.component';
import { DepenseGraphComponent } from '../../pages/dashboard/widgets/depense-graph/depense-graph.component';
import { NotificationsComponent } from '../../pages/dashboard/widgets/notifications/notifications.component';
import { AuthService } from '../auth.service';
import { ChatbotComponent } from '../../pages/dashboard/widgets/chatbot/chatbot.component';

@Injectable()
export class DashboardService {
  constructor(private authservice: AuthService) {}
  widgets = signal<Widget[]>([
    {
      id: 1,
      label: 'Solde',
      content: SoldeWidgetComponent,
      backgroundColor: '#845162',
      color: 'whitesmoke',
    },
    {
      id: 2,
      label: 'Deposit',
      content: DepositWidgetComponent,
      backgroundColor: '#845162',
      color: 'whitesmoke',
    },
    {
      id: 3,
      label: 'Withdrawl',
      content: WithdrawlWidgetComponent,
      backgroundColor: '#845162',
      color: 'whitesmoke',
    },
    {
      id: 4,
      label: 'Growth',
      content: GrowthWidgetComponent,
      backgroundColor: '#845162',
      color: 'whitesmoke',
    },
    {
      id: 5,
      label: 'Dépenses Catégories',
      content: DepensePieChartComponent,
      backgroundColor: '#E3B6B1',
      color: 'whitesmoke',
    },
    {
      id: 6,
      label: 'Goals',
      content: GoalsComponent,
      backgroundColor: '#E3B6B1',
      color: 'whitesmoke',
    },
    {
      id: 7,
      label: 'Notifications 🔔',
      content: NotificationsComponent,
      backgroundColor: '#E3B6B1',
      color: 'whitesmoke',
    },
    {
      id: 8,
      label: 'Dépenses graph',
      content: DepenseGraphComponent,
      backgroundColor: '#E3B6B1',
      color: 'whitesmoke',
    },
    {
      id: 9,
      label: 'chatbot',
      content: ChatbotComponent,
      backgroundColor: '#E3B6B1',
      color: 'whitesmoke',
    },
  ]);
}
