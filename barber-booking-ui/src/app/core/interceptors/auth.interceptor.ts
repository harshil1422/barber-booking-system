// Adds the JWT access token to every API request.
// Detects 401 Unauthorized responses.
// Refreshes the access token automatically.
// Retries failed requests with the new token.
// Prevents multiple refresh calls when many requests fail simultaneously.
// Logs the user out if refresh also fails.

import {
  HttpInterceptorFn, HttpErrorResponse,
  HttpRequest, HttpHandlerFn,
} from '@angular/common/http';
import { inject }                          from '@angular/core';
import { BehaviorSubject, Observable, throwError } from 'rxjs';
import { catchError, filter, switchMap, take }     from 'rxjs/operators';
import { AuthService }          from '@core/services/auth.service';
import { TokenStorageService }  from '@core/services/token-storage.service';
import { Store }                from '@ngrx/store';
import { AuthActions }          from '@store/auth/actions/auth.actions';

// Module-level state — shared across all interceptor invocations in this session.
// These live outside the function so parallel requests share the same queue.
let isRefreshing   = false;
const refreshSubject = new BehaviorSubject<string | null>(null);

// These endpoints must never have Authorization or trigger the 401 handler.
// /auth/refresh would cause an infinite loop if it got the retry treatment.
const SKIP_URLS = ['/auth/login', '/auth/refresh', '/auth/register'];

function addBearer(
  req: HttpRequest<unknown>,
  token: string,
): HttpRequest<unknown> {
  return req.clone({
    setHeaders: { Authorization: `Bearer ${token}` },
  });
}

function shouldSkip(url: string): boolean {
  return SKIP_URLS.some(s => url.includes(s));
}

// Called when a 401 is received on a non-auth endpoint.
// Starts a refresh if one is not already in-flight.
// Parallel requests wait on refreshSubject and replay with the new token.
function handle401(
  req: HttpRequest<unknown>,
  next: HttpHandlerFn,
  authSvc: AuthService,
  store: Store,
): Observable<any> {

  if (!isRefreshing) {
    isRefreshing = true;
    refreshSubject.next(null);  // signal: refresh in-flight

    return authSvc.refreshToken().pipe(
      switchMap((accessToken: string) => {
  refreshSubject.next(accessToken);
  return next(addBearer(req, accessToken));
}),
      catchError(err => {
        isRefreshing = false;
        // Refresh failed — clear state and navigate to login via logoutSuccess
        store.dispatch(AuthActions.logoutSuccess());
        return throwError(() => err);
      }),
    );
  }

  // Another refresh is already in-flight — queue this request.
  // filter(Boolean) waits until refreshSubject emits a real token (not null).
  // take(1) unsubscribes after the first value so we don't replay endlessly.
  return refreshSubject.pipe(
    filter((t): t is string => t !== null),
    take(1),
    switchMap(token => next(addBearer(req, token))),
  );
}

// The functional interceptor itself — called for every outgoing HTTP request.
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  if (shouldSkip(req.url)) return next(req);

  const storage = inject(TokenStorageService);
  const authSvc = inject(AuthService);
  const store   = inject(Store);
  const token   = storage.getToken();
  const authReq = token ? addBearer(req, token) : req;

  return next(authReq).pipe(
    catchError((err: HttpErrorResponse) => {
      if (err.status === 401 && !shouldSkip(req.url)) {
        return handle401(req, next, authSvc, store);
      }
      return throwError(() => err);
    }),
  );
};