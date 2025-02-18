import { Injectable, signal } from '@angular/core';
import { Wigdet } from '../../models/dashboardWidget';
import { SoldeWidgetComponent } from '../../pages/dashboard/widgets/solde-widget/solde-widget.component';
import { DepositWidgetComponent } from '../../pages/dashboard/widgets/deposit-widget/deposit-widget.component';
import { WithdrawlWidgetComponent } from '../../pages/dashboard/widgets/withdrawl-widget/withdrawl-widget.component';
import { GrowthWidgetComponent } from '../../pages/dashboard/widgets/growth-widget/growth-widget.component';
import { DepensePieChartComponent } from '../../pages/dashboard/widgets/depense-pie-chart/depense-pie-chart.component';
import { GoalsComponent } from '../../pages/dashboard/widgets/goals/goals.component';
import { DepenseGraphComponent } from '../../pages/dashboard/widgets/depense-graph/depense-graph.component';
import { NotificationsComponent } from '../../pages/dashboard/widgets/notifications/notifications.component';

@Injectable()
export class DashboardService {
  widgets = signal<Wigdet[]>([
    {
      id: 1,
      label: 'Solde',
      content: SoldeWidgetComponent,
    },
    {
      id: 2,
      label: 'Deposit',
      content: DepositWidgetComponent,
    },
    {
      id: 3,
      label: 'Withdrawl',
      content: WithdrawlWidgetComponent,
    },
    {
      id: 4,
      label: 'Growth',
      content: GrowthWidgetComponent,
    },
    {
      id: 5,
      label: 'Dépenses Catégories',
      content: DepensePieChartComponent,
    },
    {
      id: 6,
      label: 'Goals',
      content: GoalsComponent,
    },
    {
      id: 7,
      label: 'Notifications 🔔',
      content: NotificationsComponent,
    },
    {
      id: 8,
      label: 'Dépenses graph',
      content: DepenseGraphComponent,
    },
  ]);
  constructor() {}
}
