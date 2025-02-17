import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-root',
  standalone: true,
  template: `<router-outlet></router-outlet>`,
  imports: [RouterOutlet] // ❌ Supprime LoginComponent et RegisterComponent ici
})
export class AppComponent {
  title(title: any) {
    throw new Error('Method not implemented.');
  }
}
