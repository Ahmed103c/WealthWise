import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { StatistiqueService } from '../../services/statistiquesService/statistique.service';
import { WidgetComponent } from '../../components/widget/widget.component';
import { RouterLink, RouterModule } from '@angular/router';

@Component({
  selector: 'app-statistiques',
  imports: [CommonModule, WidgetComponent, RouterLink, RouterModule],
  templateUrl: './statistiques.component.html',
  styleUrl: './statistiques.component.scss',
})
export class StatistiquesComponent {
  store = inject(StatistiqueService);
}
