import { Component } from '@angular/core';
import {Router, RouterLinkActive,RouterModule} from '@angular/router';
import { CommonModule } from '@angular/common';


@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss'],
  imports: [CommonModule, RouterLinkActive,RouterModule],
  standalone: true
})
export class DashboardComponent{}

