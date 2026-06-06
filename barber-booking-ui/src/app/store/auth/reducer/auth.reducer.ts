import { createReducer, on } from '@ngrx/store';
import { AuthState, initialAuthState } from '@core/models/auth.models';
import { AuthActions } from '../actions/auth.actions';

// Pure function: (currentState, action) => newState
// NEVER mutate state directly — always spread: { ...s, field: value }
// NgRx runtimeChecks.strictStateImmutability throws if you mutate.

export const authReducer = createReducer(
  initialAuthState,

  // ── Login ──────────────────────────────────────────────────────────
  on(AuthActions.login,
    s => ({ ...s, loading: true, error: null })),

  on(AuthActions.loginSuccess,
    (s, { token, user }) => ({
      ...s, loading: false, accessToken: token, user, error: null,
    })),

  on(AuthActions.loginFailure,
    (s, { error }) => ({ ...s, loading: false, error })),

  // ── Register ───────────────────────────────────────────────────────
  on(AuthActions.register,
    s => ({ ...s, loading: true, error: null })),

  on(AuthActions.registerSuccess,
    s => ({ ...s, loading: false })),

  on(AuthActions.registerFailure,
    (s, { error }) => ({ ...s, loading: false, error })),

  // ── Logout ─────────────────────────────────────────────────────────
  // CRITICAL: Reset ALL state but override initialized: true.
  // If initialized went back to false, every guard would re-run
  // checkAuth after logout, creating a redirect loop.
  on(AuthActions.logout,
    s => ({ ...s, loading: true })),

  on(AuthActions.logoutSuccess,
    () => ({ ...initialAuthState, initialized: true })),

  // ── Silent refresh — NO loading flag, no spinner ────────────────────
  on(AuthActions.refreshTokenSuccess,
    (s, { token, user }) => ({
      ...s, accessToken: token, user, initialized: true,
    })),

  on(AuthActions.refreshTokenFailure,
    () => ({ ...initialAuthState, initialized: true })),

  // ── App-init — both outcomes MUST set initialized: true ─────────────
  on(AuthActions.checkAuth,
    s => ({ ...s, loading: true })),

  on(AuthActions.checkAuthSuccess,
    (s, { token, user }) => ({
      ...s, loading: false, accessToken: token, user, initialized: true,
    })),

  on(AuthActions.checkAuthFailure,
    s => ({ ...s, loading: false, initialized: true })),

  // ── Misc ───────────────────────────────────────────────────────────
  on(AuthActions.clearError,
    s => ({ ...s, error: null })),
);