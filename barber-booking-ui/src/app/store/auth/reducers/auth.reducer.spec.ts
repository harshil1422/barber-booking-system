import { authReducer }    from './auth.reducer';
import { AuthActions }    from '../actions/auth.actions';
import { initialAuthState, AuthUser } from '@core/models/auth.models';

const mockUser: AuthUser = {
  userId: 'uuid-1', username: 'alice',
  roles: ['ROLE_USER'], exp: 9999999999,
};

describe('authReducer', () => {

  it('returns initialAuthState for unknown action', () => {
    const s = authReducer(undefined, { type: '@@INIT' } as any);
    expect(s).toEqual(initialAuthState);
    expect(s.initialized).toBeFalse();
  });

  it('sets loading=true and clears stale error on login', () => {
    const s = authReducer(
      { ...initialAuthState, error: 'old error' },
      AuthActions.login({ request: { username: 'a', password: 'b' } }),
    );
    expect(s.loading).toBeTrue();
    expect(s.error).toBeNull();
  });

  it('stores user and token on loginSuccess', () => {
    const s = authReducer(initialAuthState,
      AuthActions.loginSuccess({ token: 'jwt', user: mockUser }));
    expect(s.user).toEqual(mockUser);
    expect(s.accessToken).toBe('jwt');
    expect(s.loading).toBeFalse();
    expect(s.error).toBeNull();
  });

  it('sets error string on loginFailure', () => {
    const s = authReducer(initialAuthState,
      AuthActions.loginFailure({ error: 'Bad credentials' }));
    expect(s.error).toBe('Bad credentials');
    expect(s.loading).toBeFalse();
  });

  it('resets all state on logoutSuccess but keeps initialized: true', () => {
    const loggedIn  = authReducer(initialAuthState,
      AuthActions.loginSuccess({ token: 'jwt', user: mockUser }));
    const loggedOut = authReducer(loggedIn, AuthActions.logoutSuccess());
    expect(loggedOut.user).toBeNull();
    expect(loggedOut.accessToken).toBeNull();
    expect(loggedOut.initialized).toBeTrue(); // MUST be true
  });

  it('sets initialized=true on checkAuthFailure (no cookie)', () => {
    const s = authReducer(initialAuthState, AuthActions.checkAuthFailure());
    expect(s.initialized).toBeTrue();
    expect(s.user).toBeNull();
  });

  it('sets initialized=true on checkAuthSuccess (cookie valid)', () => {
    const s = authReducer(initialAuthState,
      AuthActions.checkAuthSuccess({ token: 'jwt', user: mockUser }));
    expect(s.initialized).toBeTrue();
    expect(s.user).toEqual(mockUser);
  });

  it('clears error on clearError', () => {
    const s = authReducer(
      { ...initialAuthState, error: 'something went wrong' },
      AuthActions.clearError());
    expect(s.error).toBeNull();
  });

  it('does NOT set loading on refreshTokenSuccess — silent refresh', () => {
    const s = authReducer(initialAuthState,
      AuthActions.refreshTokenSuccess({ token: 'new-jwt', user: mockUser }));
    expect(s.loading).toBeFalse();
    expect(s.accessToken).toBe('new-jwt');
  });
});