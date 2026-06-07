import { ApplicationConfig, APP_INITIALIZER } from '@angular/core';
import {
  provideRouter,
  withComponentInputBinding,
  withViewTransitions,
  withRouterConfig,
} from '@angular/router';
import {
  provideHttpClient,
  withFetch,
  withInterceptors,
} from '@angular/common/http';
import { provideAnimationsAsync }   from '@angular/platform-browser/animations/async';
import { provideStore }             from '@ngrx/store';
import { provideEffects }           from '@ngrx/effects';
import { provideRouterStore }       from '@ngrx/router-store';
import { provideStoreDevtools }     from '@ngrx/store-devtools';
import { Store }                   from '@ngrx/store';

import { APP_ROUTES }              from './app.routes';
import { authInterceptor }         from '@core/interceptors/auth.interceptor';
import { errorInterceptor }        from '@core/interceptors/error.interceptor';
import { loadingInterceptor }      from '@core/interceptors/loading.interceptor';
import { authReducer }             from '@store/auth/reducers/auth.reducer';
import { AuthEffects }             from '@store/auth/effects/auth.effects';
import { AUTH_FEATURE_KEY }        from '@store/auth/selectors/auth.selectors';
import { AuthActions }             from '@store/auth/actions/auth.actions';
import { environment }              from '@env/environment';

// ── APP_INITIALIZER factory ────────────────────────────────────────────────
// Returns a void function — Angular calls it before the first render.
// Dispatches checkAuth which triggers the checkAuth$ effect,
// which calls POST /auth/refresh and either restores the session or sets
// initialized: true so guards unblock. The dispatch is fire-and-forget —
// the initialized flag in the store is what gates the rest of the app.
function checkAuthFactory(store: Store) {
  return () => store.dispatch(AuthActions.checkAuth());
}

export const appConfig: ApplicationConfig = {
  providers: [

    // ── Router ─────────────────────────────────────────────────────────────
    // withComponentInputBinding — route params bind to @Input() signals
    // withViewTransitions      — native browser page transition animations
    // withRouterConfig         — paramsInheritanceStrategy lets child routes
    //                            inherit params from parent routes
    provideRouter(
      APP_ROUTES,
      withComponentInputBinding(),
      withViewTransitions(),
      withRouterConfig({ paramsInheritanceStrategy: 'always' }),
    ),

    // ── HTTP client ────────────────────────────────────────────────────────
    // withFetch()        — use Fetch API instead of XHR (Angular 19 default)
    // withInterceptors() — functional interceptors in execution order:
    //   1. authInterceptor    adds Bearer, handles 401 retry queue
    //   2. errorInterceptor   shows snackbar for non-401 errors
    //   3. loadingInterceptor shows global progress bar
    provideHttpClient(
      withFetch(),
      withInterceptors([
        authInterceptor,
        errorInterceptor,
        loadingInterceptor,
      ]),
    ),

    // ── Animations — deferred, not synchronous ─────────────────────────────
    provideAnimationsAsync(),

    // ── NgRx store — root store with auth feature slice ────────────────────
    // AUTH_FEATURE_KEY is imported from auth.selectors.ts — same constant,
    // never a raw string, so they cannot get out of sync.
    provideStore(
      { [AUTH_FEATURE_KEY]: authReducer },
      {
        runtimeChecks: {
          strictStateImmutability:     true,   // throws if state is mutated
          strictActionImmutability:    true,   // throws if action props mutated
          strictStateSerializability:  false,  // allow Date objects in state
          strictActionSerializability: false,  // allow non-serializable payloads
        },
      },
    ),

    // ── NgRx effects — must come after provideStore() ──────────────────────
    provideEffects([AuthEffects]),

    // ── Router store — syncs router state into NgRx ─────────────────────── 
    provideRouterStore(),

    // ── Redux DevTools — logOnly in production ─────────────────────────────
    provideStoreDevtools({
      maxAge:    25,
      logOnly:   environment.production,
      autoPause: true,
      trace:     !environment.production,  // call stack per action in dev
    }),

    // ── Session restore — runs before first route renders ──────────────────
    {
      provide:    APP_INITIALIZER,
      useFactory: checkAuthFactory,
      deps:       [Store],
      multi:      true,
    },

  ],
};