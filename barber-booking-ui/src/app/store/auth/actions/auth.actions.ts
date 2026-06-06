import {createAction,createActionGroup,emptyProps ,props} from '@ngrx/store';
import { AuthUser, LoginRequest, RegisterRequest } from '../../../core/models/auth.models';


// createActionGroup auto-generates typed action creators.
// 'Login' → AuthActions.login(), AuthActions.loginSuccess(), AuthActions.loginFailure()
// In Redux DevTools they appear as [Auth] Login, [Auth] Login Success, etc.


export const AuthActions = createActionGroup({
  source: 'Auth',
  events: {

    // ── Login ──────────────────────────────────────────────────────────
    'Login':         props<{ request: LoginRequest }>(),
    'Login Success': props<{ token: string; user: AuthUser }>(),
    'Login Failure': props<{ error: string }>(),

    // ── Register ───────────────────────────────────────────────────────
    'Register':         props<{ request: RegisterRequest }>(),
    'Register Success': emptyProps(),
    'Register Failure': props<{ error: string }>(),

    // ── Logout ─────────────────────────────────────────────────────────
    'Logout':         emptyProps(),
    'Logout Success': emptyProps(),

    // ── Silent token refresh (called by interceptor + scheduler) ───────
    'Refresh Token':         emptyProps(),
    'Refresh Token Success': props<{ token: string; user: AuthUser }>(),
    'Refresh Token Failure': emptyProps(),

    // ── App-init session check (APP_INITIALIZER) ───────────────────────
    'Check Auth':         emptyProps(),
    'Check Auth Success': props<{ token: string; user: AuthUser }>(),
    'Check Auth Failure': emptyProps(),

    // ── Misc ───────────────────────────────────────────────────────────
    'Clear Error': emptyProps(),
  },
});