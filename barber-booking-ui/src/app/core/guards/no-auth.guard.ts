import { inject }     from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { Store }        from '@ngrx/store';
import { map, take }    from 'rxjs/operators';
import { AuthSelectors } from '@store/auth/selectors/auth.selectors';

export const noAuthGuard: CanActivateFn = () => {
  const store  = inject(Store);
  const router = inject(Router);

  // If the user IS authenticated, redirect them away from login/register.
  // Note: noAuthGuard does NOT need to wait for initialized.
  // If the user just logged out, initialized is already true and
  // isAuthenticated is false — they reach login cleanly.
  // If somehow called before init, selectIsAuthenticated returns false
  // (correct — don't redirect an unknown user away from login).
  return store.select(AuthSelectors.selectIsAuthenticated).pipe(
    take(1),
    map(isAuth =>
      isAuth
        ? router.createUrlTree(['/dashboard'])
        : true
    ),
  );
};