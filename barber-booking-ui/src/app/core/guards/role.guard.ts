import { inject }     from '@angular/core';
import {
  CanActivateFn,
  ActivatedRouteSnapshot,
  Router,
} from '@angular/router';
import { Store }        from '@ngrx/store';
import { map, take }    from 'rxjs/operators';
import { AuthSelectors } from '@store/auth/selectors/auth.selectors';

export const roleGuard: CanActivateFn = (route: ActivatedRouteSnapshot) => {
  const store  = inject(Store);
  const router = inject(Router);

  // Required roles are defined in route data:
  // { path: 'admin', canActivate: [authGuard, roleGuard],
  //   data: { roles: ['ROLE_ADMIN'] } }
  // Always use with authGuard first — roleGuard assumes user is authenticated.
  const required: string[] = route.data['roles'] ?? [];

  return store.select(AuthSelectors.selectUserRoles).pipe(
    take(1),
    map(userRoles => {
      // some() — user needs ANY ONE of the required roles (OR logic)
      // Change to every() if you need ALL roles (AND logic)
      const hasRole = required.some(r => userRoles.includes(r));

      if (hasRole) return true;

      // Authenticated but not authorised — show forbidden page, not login
      return router.createUrlTree(['/forbidden']);
    }),
  );
};