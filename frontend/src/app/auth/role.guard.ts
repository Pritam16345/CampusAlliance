import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from './auth.service';

export const roleGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  const expectedRoles = route.data['roles'] as Array<string>;
  const userRole = authService.getRole();

  if (!userRole || !expectedRoles.includes(userRole)) {
    router.navigate(['/resources']); // Redirect to a safe page
    return false;
  }
  
  return true;
};
