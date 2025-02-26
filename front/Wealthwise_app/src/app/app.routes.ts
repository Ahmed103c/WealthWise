import { Routes } from '@angular/router';
import { LoginComponent } from './pages/login/login.component';
import { DashboardComponent } from './pages/dashboard/dashboard.component';
import { RegisterComponent } from './pages/register/register.component';
import { InfopersoComponent } from './pages/infoperso/infoperso.component';
import { MainLayoutComponent } from './pages/main-layout/main-layout.component';
import { HomeComponent } from './pages/home/home.component';
import { BudgetComponent } from './pages/budget/budget.component';
import { TransactionComponent } from './pages/transaction/transaction.component';
import { CompteConjointComponent } from './pages/compteconjoint/compteconjoint.component';

export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  {
    path: 'main',
    component: MainLayoutComponent,
    children: [
      { path: 'dashboard', component: DashboardComponent },
      { path: 'infoperso', component: InfopersoComponent },
      { path: 'app-budget', component: BudgetComponent },
      { path: 'app-transaction', component: TransactionComponent },
      // Ajout de la route pour la page compte conjoint
      { path: 'compte-conjoint', component: CompteConjointComponent }
    ]
  },
];
