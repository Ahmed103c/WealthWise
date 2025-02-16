import { bootstrapApplication } from '@angular/platform-browser';
import { AppComponent } from './app/app.component';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { provideRouter } from '@angular/router';
import { routes } from './app/app.routes'; // ✅ Import correct du routeur
import { authInterceptor } from './app/services/auth.interceptor'; // ✅ Import correct de l’intercepteur JWT

bootstrapApplication(AppComponent, {
  providers: [
    provideHttpClient(withInterceptors([authInterceptor])), // ✅ Ajout de l’intercepteur
    provideRouter(routes) // ✅ Ajout du routeur
  ]
}).catch(err => console.error(err));

