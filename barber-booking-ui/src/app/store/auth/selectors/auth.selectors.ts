import { createFeatureSelector,createSelector } from '@ngrx/store';
import { AuthState } from '../../../core/models/auth.models';



// AUTH_FEATURE_KEY must match exactly what you pass to provideStore() in app.config.ts.
// A mismatch gives silent undefined selectors — no error message.
export const AUTH_FEATURE_KEY = 'auth';

// Root selector — reads the entire 'auth' slice from the store
const selectAuthState = createFeatureSelector<AuthState>(AUTH_FEATURE_KEY);

// All selectors are memoized — recompute only when their input changes.
// Components use: store.selectSignal(AuthSelectors.selectXxx)
// which returns a native Angular Signal — no async pipe needed.

export const AuthSelectors = {

  // ── Raw state slices ───────────────────────────────────────────────
  selectUser:        createSelector(selectAuthState, s => s.user),
  selectAccessToken: createSelector(selectAuthState, s => s.accessToken),
  selectIsLoading:   createSelector(selectAuthState, s => s.loading),
  selectError:       createSelector(selectAuthState, s => s.error),
  selectInitialized: createSelector(selectAuthState, s => s.initialized),

  // ── Derived — computed from raw state ──────────────────────────────

  // Used by AuthGuard, NoAuthGuard, and app shell @if
  selectIsAuthenticated: createSelector(
    selectAuthState, s => !!s.user && !!s.accessToken,
  ),

  // Convenience projections — toolbar, profile, bookings header
  selectUsername:  createSelector(selectAuthState, s => s.user?.username ?? null),
  selectUserId:    createSelector(selectAuthState, s => s.user?.userId   ?? null),
  selectUserRoles: createSelector(selectAuthState, s => s.user?.roles    ?? []),

  // Role-specific — RoleGuard and admin-only UI elements
  selectIsAdmin: createSelector(
    selectAuthState, s => s.user?.roles.includes('ROLE_ADMIN') ?? false,
  ),
  selectIsUser: createSelector(
    selectAuthState, s => s.user?.roles.includes('ROLE_USER') ?? false,
  ),
  selectIsBarber: createSelector(
    selectAuthState, s=> s.user?.roles.includes('ROLE_BARBER') ?? false,
  ),
  selectActiveRole: createSelector(
    selectAuthState, s => s.activeRole
  ),
  selectHasShop: createSelector(
    selectAuthState, s => 1
  )
};