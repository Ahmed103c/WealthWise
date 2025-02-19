import { Component, Host, input } from '@angular/core';
import { Widget } from '../../models/dashboardWidget';
import { NgComponentOutlet } from '@angular/common';

@Component({
  selector: 'app-widget',
  imports: [NgComponentOutlet],
  standalone: true,
  templateUrl: './widget.component.html',
  styleUrl: './widget.component.scss',
  styles: `
    :Host{
      display :block;
      border-radius:16px;
    }`,
  // host: {
  //   '[style.grid-area]':
  //     '"span " + (data?.rows ?? 1) + " / span " + (data?.columns ?? 1)',
  // },
  // host: {
  //   '[style.grid-row]': '"span " + (data?.rows ?? 1)',
  //   '[style.grid-column]': '"span " + (data?.columns ?? 1)',
  // },
})
export class WidgetComponent {
  data = input.required<Widget>();
}
