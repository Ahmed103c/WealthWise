import { Routes } from '@angular/router';
import { LoginComponent } from './pages/login/login.component';
import { DashboardComponent } from './pages/dashboard/dashboard.component';
import { RegisterComponent } from './pages/register/register.component';
import { InfopersoComponent } from './pages/infoperso/infoperso.component';
import { MainLayoutComponent } from './pages/main-layout/main-layout.component';
import { HomeComponent } from './pages/home/home.component';
import { BudgetComponent } from './pages/budget/budget.component';
import { TransactionComponent } from './pages/transaction/transaction.component';

export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'login', component: LoginComponent }, // ✅ Vérifier que la route existe
  { path: 'register', component: RegisterComponent },
  { path: 'transactions', component: TransactionComponent },
  {
    path: 'main',
    component: MainLayoutComponent,
    children: [
      { path: 'dashboard', component: DashboardComponent },
      { path: 'infoperso', component: InfopersoComponent },
      { path: 'budget', component: BudgetComponent }
    ]
  },
];

