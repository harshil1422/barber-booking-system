import { Injectable,inject } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {TokenStorageService} from './token-storage.service';
import { environment }          from '@env/environment';
import { Observable }           from 'rxjs';
import { tap }                  from 'rxjs/operators';
import { LoginRequest, TokenResponse,RegisterRequest } from '../models/auth.models';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

    // Angular 19: inject() instead of constructor params
   readonly #http = inject(HttpClient);
   readonly #storage = inject(TokenStorageService);

     // Base URL comes from environment — '/api' in dev (proxied), real URL in prod
       readonly #base = `${environment.apiUrl}/v1/auth`;

      // ── Login ──────────────────────────────────────────────────────────────
  // withCredentials: true → browser sends & receives the HttpOnly cookie.
  // tap() stores the token immediately so the interceptor has it before
  // any subsequent request fires (e.g. fetching user profile after login).

    login(req: LoginRequest): Observable<TokenResponse> {
    return this.#http
      .post<TokenResponse>(`${this.#base}/login`, req, {
        withCredentials: true,
      })
      .pipe(tap(r => this.#storage.setToken(r.accessToken)));
  }

  // ── Register ───────────────────────────────────────────────────────────
  // No withCredentials — no tokens issued at registration.
  // Returns void because Spring Boot responds with 201 Created (no body).
  register(req: RegisterRequest): Observable<void> {
    return this.#http
      .post<void>(`${this.#base}/register`, req);
  }

  // ── Silent refresh ─────────────────────────────────────────────────────
  // Called by:
  //   1. AuthInterceptor when a 401 is received
  //   2. APP_INITIALIZER on app startup (checkAuth effect)
  // The HttpOnly cookie is sent automatically by the browser.
  // Spring Boot rotates the cookie and returns a new access token.
  refreshToken(): Observable<TokenResponse> {
    return this.#http
      .post<TokenResponse>(`${this.#base}/refresh`, {}, {
        withCredentials: true,
      })
      .pipe(tap(r => this.#storage.setToken(r.accessToken)));
  }

  // ── Logout ─────────────────────────────────────────────────────────────
  // withCredentials: true sends the HttpOnly cookie so Spring Boot can:
  //   1. Find the refresh token hash in PostgreSQL
  //   2. Mark it revoked (theft detection still works)
  //   3. Clear the Set-Cookie header in the response
  // tap() clears the in-memory token immediately regardless of server response.
  logout(): Observable<void> {
    return this.#http
      .post<void>(`${this.#base}/logout`, {}, {
        withCredentials: true,
      })
      .pipe(tap(() => this.#storage.clear()));
  }

}
