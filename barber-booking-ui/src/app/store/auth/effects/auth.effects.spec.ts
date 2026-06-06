import { TestBed }              from '@angular/core/testing';
import { RouterTestingModule }  from '@angular/router/testing';
import { provideMockActions }   from '@ngrx/effects/testing';
import { Observable, of, throwError } from 'rxjs';
import { Action }               from '@ngrx/store';
import { AuthEffects }          from './auth.effects';
import { AuthService }          from '@core/services/auth.service';
import { TokenStorageService }  from '@core/services/token-storage.service';
import { AuthActions }          from '../actions/auth.actions';
import { AuthUser }             from '@core/models/auth.models';

const mockUser: AuthUser = {
  userId: 'uuid-1', username: 'alice',
  roles: ['ROLE_USER'], exp: 9999999999,
};

describe('AuthEffects', () => {
  let effects:      AuthEffects;
  let actions$:     Observable<Action>;
  let authService:  jasmine.SpyObj<AuthService>;
  let tokenStorage: jasmine.SpyObj<TokenStorageService>;

  beforeEach(() => {
    authService  = jasmine.createSpyObj('AuthService', [
      'login', 'logout', 'refreshToken', 'register',
    ]);
    tokenStorage = jasmine.createSpyObj('TokenStorageService', [
      'toAuthUser', 'set', 'clear', 'get',
    ]);
    tokenStorage.toAuthUser.and.returnValue(mockUser);

    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [
        AuthEffects,
        provideMockActions(() => actions$),
        { provide: AuthService,         useValue: authService },
        { provide: TokenStorageService,  useValue: tokenStorage },
      ],
    });
    effects = TestBed.inject(AuthEffects);
  });

  // ── login$ ─────────────────────────────────────────────────────────────

  it('login$ dispatches loginSuccess on valid credentials', done => {
    authService.login.and.returnValue(of({ accessToken: 'jwt' }));
    actions$ = of(AuthActions.login({
      request: { username: 'alice', password: 'pass' },
    }));

    effects.login$.subscribe(action => {
      expect(action).toEqual(AuthActions.loginSuccess({
        token: 'jwt', user: mockUser,
      }));
      done();
    });
  });

  it('login$ dispatches loginFailure with Spring Boot message', done => {
    authService.login.and.returnValue(
      throwError(() => ({ error: { message: 'Invalid credentials' } }))
    );
    actions$ = of(AuthActions.login({
      request: { username: 'x', password: 'y' },
    }));

    effects.login$.subscribe(action => {
      expect(action).toEqual(
        AuthActions.loginFailure({ error: 'Invalid credentials' })
      );
      done();
    });
  });

  // ── register$ ──────────────────────────────────────────────────────────

  it('register$ dispatches registerSuccess', done => {
    authService.register.and.returnValue(of(undefined as any));
    actions$ = of(AuthActions.register({
      request: { username: 'bob', email: 'b@b.com', password: 'pass' },
    }));

    effects.register$.subscribe(action => {
      expect(action).toEqual(AuthActions.registerSuccess());
      done();
    });
  });

  // ── logout$ ────────────────────────────────────────────────────────────

  it('logout$ dispatches logoutSuccess on HTTP success', done => {
    authService.logout.and.returnValue(of(undefined as any));
    actions$ = of(AuthActions.logout());

    effects.logout$.subscribe(action => {
      expect(action).toEqual(AuthActions.logoutSuccess());
      done();
    });
  });

  it('logout$ still dispatches logoutSuccess on HTTP 500 error', done => {
    authService.logout.and.returnValue(
      throwError(() => ({ status: 500 }))
    );
    actions$ = of(AuthActions.logout());

    effects.logout$.subscribe(action => {
      expect(action).toEqual(AuthActions.logoutSuccess());
      done();
    });
  });

  // ── checkAuth$ ─────────────────────────────────────────────────────────

  it('checkAuth$ dispatches checkAuthSuccess when cookie is valid', done => {
    authService.refreshToken.and.returnValue(of({ accessToken: 'new-jwt' }));
    actions$ = of(AuthActions.checkAuth());

    effects.checkAuth$.subscribe(action => {
      expect(action).toEqual(AuthActions.checkAuthSuccess({
        token: 'new-jwt', user: mockUser,
      }));
      done();
    });
  });

  it('checkAuth$ dispatches checkAuthFailure when no session exists', done => {
    authService.refreshToken.and.returnValue(
      throwError(() => ({ status: 401 }))
    );
    actions$ = of(AuthActions.checkAuth());

    effects.checkAuth$.subscribe(action => {
      expect(action).toEqual(AuthActions.checkAuthFailure());
      done();
    });
  });
});