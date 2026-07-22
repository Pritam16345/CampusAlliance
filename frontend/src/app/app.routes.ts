import { Routes } from '@angular/router';
import { LoginComponent } from './auth/login/login.component';
import { LayoutComponent } from './layout/layout.component';
import { authGuard } from './auth/auth.guard';
import { roleGuard } from './auth/role.guard';
import { ResourceRepositoryComponent } from './resources/resource-repository/resource-repository.component';
import { LiveNoticesComponent } from './notices/live-notices/live-notices.component';
import { NoticeAnalyticsComponent } from './notices/notice-analytics/notice-analytics.component';
import { SystemHealthComponent } from './health/system-health.component';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  {
    path: '',
    component: LayoutComponent,
    canActivate: [authGuard],
    children: [
      { path: 'resources', component: ResourceRepositoryComponent },
      { path: 'notices', component: LiveNoticesComponent },
      { 
        path: 'notices/:id/analytics', 
        component: NoticeAnalyticsComponent,
        canActivate: [roleGuard],
        data: { roles: ['ADMIN', 'FACULTY'] }
      },
      { 
        path: 'health', 
        component: SystemHealthComponent,
        canActivate: [roleGuard],
        data: { roles: ['ADMIN'] }
      },
      { path: '', redirectTo: 'resources', pathMatch: 'full' }
    ]
  },
  { path: '**', redirectTo: 'login' }
];
