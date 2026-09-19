import { Routes } from '@angular/router';
import { HomePage } from './pages/home/home';
import { ChartDashboardPage } from './pages/chart/chartdashboard.component';

export const routes: Routes = [
  { path: '', component: HomePage },
  { path: 'chart', component: ChartDashboardPage },
];
