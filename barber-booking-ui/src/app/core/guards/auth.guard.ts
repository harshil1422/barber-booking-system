import { inject }     from '@angular/core';
import {
  CanActivateFn,
  ActivatedRouteSnapshot,
  RouterStateSnapshot,
  Router,
} from '@angular/router';
import { Store }       from '@ngrx/store';
import { filter, map, switchMap, take } from 'rxjs/operators';
import { AuthSelectors } from '@store/auth/selectors/auth.selectors';

export const authGuard: CanActivateFn = (
  _route: ActivatedRouteSnapshot,
  state:  RouterStateSnapshot,
) => {
  const store  = inject(Store);
  const router = inject(Router);

  // Step 1: Wait for initialized to become true.
  // On a hard refresh this is false until APP_INITIALIZER's checkAuth
  // effect completes. filter(Boolean) holds the guard here until then.
  return store.select(AuthSelectors.selectInitialized).pipe(
    filter((init): init is true => init === true),
    take(1),

    // Step 2: Once initialized, check if the user is authenticated.
    switchMap(() =>
      store.select(AuthSelectors.selectIsAuthenticated).pipe(take(1)),
    ),

    // Step 3: Return true to allow, or a UrlTree to redirect.
    // router.createUrlTree() is preferred over router.navigate() + return false
    // because it lets Angular handle the redirect atomically in the router cycle.
    map(isAuth => {
      if (isAuth) return true;

      // Encode the blocked URL as returnUrl so we can redirect back after login
      return router.createUrlTree(['/auth/login'], {
        queryParams: { returnUrl: state.url },
      });
    }),
  );
};