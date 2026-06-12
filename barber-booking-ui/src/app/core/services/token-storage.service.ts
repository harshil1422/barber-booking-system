import { Injectable } from '@angular/core';
import {jwtDecode} from 'jwt-decode';
import { AuthUser, JwtPayload } from '../models/auth.models';

@Injectable({
  providedIn: 'root'
})
export class TokenStorageService {
 
   // Private class field — inaccessible outside this service.
  // This is the ONLY place the access token is stored in memory.
  // making private filed token
  #token: string|null = null;

  // ── Token CRUD ────────────────────────────────────────────────────────

  /** Called by AuthService.login() and AuthService.refreshToken() */

  setToken(token: string): void {
    this.#token = token;
  }

  /** Called by AuthInterceptor to attach Bearer header */
  getToken(): string|null{
    return this.#token;
  }

  /** Called on logout and refresh failure */
  clear(): void {
    this.#token =null;
  }

  /** True if a non-expired token is present */
  exists(): boolean {
    return !!this.#token && !this.isExpired();
  }

   // ── JWT decoding ──────────────────────────────────────────────────────

  /** Decode JWT without verifying signature (browser cannot verify) */
  decode(): JwtPayload | null {
    if (!this.#token) return null;
    try   { return jwtDecode<JwtPayload>(this.#token); }
    catch { return null; }
  }

  /** Project JwtPayload → AuthUser (the simplified model used in the store) */
  toAuthUser(): AuthUser | null {
    const p = this.decode();
    if (!p) return null;
    return {
      userId:   p.userId,
      username: p.sub,     // JWT 'sub' → AuthUser 'username'
      roles:    p.roles ?? [],
      exp:      p.exp,
      phone:    "1234567890" // Placeholder, as phone is not in JWT but may be needed in UI
    };
  }

 // ── Expiry ────────────────────────────────────────────────────────────

  /** True if token is missing or within 10 seconds of expiry */
  isExpired(): boolean {
    const p = this.decode();
    if (!p?.exp) return true;
    // 10-second buffer prevents race: token valid here but expired at server
    return (p.exp * 1000) < (Date.now() + 10_000);
  }

  /** Expiry as milliseconds since epoch — used to schedule proactive refresh */
  expiresAtMs(): number {
    return (this.decode()?.exp ?? 0) * 1000;
  }

  // ── Role helpers ──────────────────────────────────────────────────────

  /** Check single role — used in components for conditional rendering */
  hasRole(role: string): boolean {
    return this.toAuthUser()?.roles.includes(role) ?? false;
  }

  /** Check any of multiple roles — used in guards */
  hasAnyRole(...roles: string[]): boolean {
    const user = this.toAuthUser();
    return roles.some(r => user?.roles.includes(r) ?? false);
  }
}

