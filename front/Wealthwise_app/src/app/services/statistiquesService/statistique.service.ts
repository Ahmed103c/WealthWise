import { Injectable, signal } from '@angular/core';
import { Widget } from '../../models/dashboardWidget';
import { StatisitqueCategoryComponent } from '../../pages/statistiques/statisitque-category/statisitque-category.component';
import { StatistiqueDepenseGainComponent } from '../../pages/statistiques/statistique-depense-gain/statistique-depense-gain.component';

@Injectable({
  providedIn: 'root',
})
export class StatistiqueService {
  constructor() {}
  widgets = signal<Widget[]>([
    {
      id: 1,
      label: 'Ctégories des dépenses',
      content: StatisitqueCategoryComponent,
      backgroundColor: '#eda8a1',
      color: 'whitesmoke',
    },
    {
      id: 2,
      label: 'transactions : dépenses et gain ',
      content: StatistiqueDepenseGainComponent,
      backgroundColor: '#eda8a1',
      color: 'whitesmoke',
    },
  ]);
}
