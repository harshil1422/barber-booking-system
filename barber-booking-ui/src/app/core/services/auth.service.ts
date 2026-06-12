// src/app/core/services/auth.service.ts
import { Injectable, computed, signal } from '@angular/core';
import { HttpClient }  from '@angular/common/http';
import { Router }      from '@angular/router';
import { Observable, tap, map } from 'rxjs';
import { jwtDecode }   from 'jwt-decode';

import { environment } from '@env/environment';
import { AuthResponse, JwtPayload, LoginRequest, UserProfile, UserRole,RegisterRequest } from '@core/models/auth.models';

const ACCESS_TOKEN_KEY  = 'nl_access_token';
const REFRESH_TOKEN_KEY = 'nl_refresh_token';

@Injectable({ providedIn: 'root' })
export class AuthService {

  private readonly api = `${environment.apiGatewayUrl}/auth`;

  // ── Angular Signals ──────────────────────────────────────
  private _user   = signal<UserProfile | null>(null);
  private _loaded = signal(false);

  readonly user$     = this._user.asReadonly();
  readonly isLoggedIn = computed(() => !!this._user());
  readonly roles      = computed(() => this._user()?.roles ?? []);
  readonly isBarber   = computed(() => this.roles().includes('BARBER'));
  readonly hasShop    = computed(() => !!this._user()?.shopId);

  constructor(private http: HttpClient, private router: Router) {
    this.rehydrate();
  }

  // ── Login (password or OTP) ──────────────────────────────
  login(payload: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.api}/login`, payload).pipe(
      tap(res => this.storeTokens(res))
    );
  }

  // ── OTP flow ─────────────────────────────────────────────
  requestOtp(phone: string): Observable<void> {
    return this.http.post<void>(`${this.api}/otp/request`, { phone });
  }

  verifyOtp(phone: string, otp: string): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.api}/otp/verify`, { phone, otp }).pipe(
      tap(res => this.storeTokens(res))
    );
  }

  // ── Refresh token ─────────────────────────────────────────
  refreshToken(): Observable<string> {
    const refreshToken = localStorage.getItem(REFRESH_TOKEN_KEY);
    return this.http
      .post<AuthResponse>(`${this.api}/refresh`, { refreshToken })
      .pipe(
        tap(res => this.storeTokens(res)),
        map(res => res.accessToken)
      );
  }

  // ── Register ───────────────────────────────────────────────
  register(request:RegisterRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.api}/register`, request).pipe(
      tap(res => this.storeTokens(res))
    );
  }

  // ── Logout ───────────────────────────────────────────────
  logout(): void {
    const token = this.getAccessToken();
    if (token) {
      this.http.post(`${this.api}/logout`, {}).subscribe();
    }
    localStorage.removeItem(ACCESS_TOKEN_KEY);
    localStorage.removeItem(REFRESH_TOKEN_KEY);
    this._user.set(null);
    this.router.navigate(['/auth/login']);
  }

  // ── Helpers ──────────────────────────────────────────────
  getAccessToken(): string | null {
    return localStorage.getItem(ACCESS_TOKEN_KEY);
  }

  hasRole(role: UserRole): boolean {
    return this.roles().includes(role);
  }

  private storeTokens(res: AuthResponse): void {
    localStorage.setItem(ACCESS_TOKEN_KEY, res.accessToken);
    localStorage.setItem(REFRESH_TOKEN_KEY, res.refreshToken);
    this._user.set(res.user);
    this._loaded.set(true);
  }

  private rehydrate(): void {
    const token = this.getAccessToken();
    if (!token) { this._loaded.set(true); return; }

    try {
      const payload = jwtDecode<JwtPayload>(token);
      if (payload.exp * 1000 < Date.now()) {
        this.logout();
        return;
      }
      // Re-fetch user profile to get latest roles/shopId
      this.http.get<UserProfile>(`${environment.apiGatewayUrl}/users/me`).subscribe({
        next:  user => { this._user.set(user); this._loaded.set(true); },
        error: ()   => { this.logout(); }
      });
    } catch {
      this.logout();
    }
  }
}
