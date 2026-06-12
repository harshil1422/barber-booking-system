import { Injectable, inject }          from '@angular/core';
import { Router }                       from '@angular/router';
import { HttpErrorResponse }            from '@angular/common/http';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { of }                           from 'rxjs';
import { catchError, exhaustMap, map, tap } from 'rxjs/operators';
import { AuthService }                 from '@core/services/auth.service';
import { TokenStorageService }         from '@core/services/token-storage.service';
import { AuthActions }                 from '../actions/auth.actions';

@Injectable()
export class AuthEffects {

  // Angular 19: inject() in field declarations — no constructor params needed
  readonly #actions$ = inject(Actions);
  readonly #auth     = inject(AuthService);
  readonly #storage  = inject(TokenStorageService);
  readonly #router   = inject(Router);

  // ── Login ──────────────────────────────────────────────────────────────
  // exhaustMap: if user clicks login twice, the second dispatch is ignored
  // until the first HTTP call resolves. Prevents duplicate login requests.
  login$ = createEffect(() =>
    this.#actions$.pipe(
      ofType(AuthActions.login),
      exhaustMap(({ request }) =>
        this.#auth.login(request).pipe(
          map(res => AuthActions.loginSuccess({
            token: res.accessToken,
            // tap() in AuthService already called storage.set() so toAuthUser() works here
            user:  this.#storage.toAuthUser()!,
          })),
          catchError((e: HttpErrorResponse) =>
            of(AuthActions.loginFailure({
              error: e.error?.message ?? 'Login failed',
            }))
          ),
        ),
      ),
    ),
  );

  // ── Login success: navigate to returnUrl ───────────────────────────────
  // dispatch: false — this effect produces no actions, only navigates.
  // history.state.returnUrl is set by AuthGuard when redirecting to /auth/login.
  loginSuccess$ = createEffect(() =>
    this.#actions$.pipe(
      ofType(AuthActions.loginSuccess),
      tap(() => {
        const returnUrl = history.state?.returnUrl ?? '/dashboard';
        this.#router.navigateByUrl(returnUrl);
      }),
    ),
    { dispatch: false },
  );

  // ── Register ───────────────────────────────────────────────────────────
  register$ = createEffect(() =>
    this.#actions$.pipe(
      ofType(AuthActions.register),
      exhaustMap(({ request }) =>
        this.#auth.register(request).pipe(
          map(() => AuthActions.registerSuccess()),
          catchError((e: HttpErrorResponse) =>
            of(AuthActions.registerFailure({
              error: e.error?.message ?? 'Registration failed',
            }))
          ),
        ),
      ),
    ),
  );

  // ── Register success: go to login with ?registered=true banner ─────────
  registerSuccess$ = createEffect(() =>
    this.#actions$.pipe(
      ofType(AuthActions.registerSuccess),
      tap(() =>
        this.#router.navigate(['/auth/login'], {
          queryParams: { registered: 'true' },
        })
      ),
    ),
    { dispatch: false },
  );

  // ── Logout ─────────────────────────────────────────────────────────────
  // ALWAYS dispatches logoutSuccess — even if the server returns 500.
  // Never leave a user stuck in a logged-in state due to a server error.
 logout$ = createEffect(() =>
    this.#actions$.pipe(
      ofType(AuthActions.logout),
      tap(() => {
        this.#auth.logout();
      })
    ),
    { dispatch: false }
  );
  // ── Logout success: navigate to login ──────────────────────────────────
  // Also fires when AuthInterceptor dispatches logoutSuccess() after
  // a failed token refresh — ensures user always lands on login page.
  logoutSuccess$ = createEffect(() =>
    this.#actions$.pipe(
      ofType(AuthActions.logoutSuccess),
      tap(() => this.#router.navigate(['/auth/login'])),
    ),
    { dispatch: false },
  );

  // ── Silent token refresh ───────────────────────────────────────────────
  // Dispatched by the proactive refresh scheduler (runs before expiry).
  // No loading flag — background operation, must not show spinner.
  refreshToken$ = createEffect(() =>
    this.#actions$.pipe(
      ofType(AuthActions.refreshToken),
      exhaustMap(() =>
        this.#auth.refreshToken().pipe(
          map(accessToken => AuthActions.refreshTokenSuccess({
            token: accessToken,
            user:  this.#storage.toAuthUser()!,
          })),
          catchError(() => of(AuthActions.refreshTokenFailure())),
        ),
      ),
    ),
  );

  // ── App-init: restore session from HttpOnly cookie ─────────────────────
  // Dispatched by APP_INITIALIZER before first route resolves.
  // Calls refreshToken — if the cookie is valid, Spring Boot returns a new
  // access token. Both success and failure set initialized: true in reducer,
  // which unblocks all route guards that were waiting with filter(Boolean).
  checkAuth$ = createEffect(() =>
    this.#actions$.pipe(
      ofType(AuthActions.checkAuth),
      exhaustMap(() =>
        this.#auth.refreshToken().pipe(
          map(accessToken => AuthActions.checkAuthSuccess({
            token: accessToken,
            user:  this.#storage.toAuthUser()!,
          })),
          catchError(() => of(AuthActions.checkAuthFailure())),
        ),
      ),
    ),
  );

  checkAuthFailureRedirect$ = createEffect(
  () =>
    this.#actions$.pipe(
      ofType(AuthActions.checkAuthFailure),
      tap(() => {
        this.#router.navigate(['/auth/login']);
      })
    ),
  { dispatch: false }
);
  
}