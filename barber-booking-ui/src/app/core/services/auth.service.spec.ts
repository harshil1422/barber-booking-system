import { TestBed }                             from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient }                   from '@angular/common/http';
import { AuthService }                        from './auth.service';
import { TokenStorageService }               from './token-storage.service';

describe('AuthService', () => {
  let svc: AuthService;
  let http: HttpTestingController;
  let storage: TokenStorageService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    svc     = TestBed.inject(AuthService);
    http    = TestBed.inject(HttpTestingController);
    storage = TestBed.inject(TokenStorageService);
  });

  afterEach(() => http.verify());

  it('login() POSTs credentials and stores the token', () => {
    svc.login({ username: 'alice', password: 'pass' }).subscribe();
    const req = http.expectOne(r => r.url.includes('/login'));

    expect(req.request.method).toBe('POST');
    expect(req.request.withCredentials).toBeTrue();

    req.flush({ accessToken: 'jwt-abc' });
    expect(storage.getToken()).toBe('jwt-abc');
  });

  it('register() POSTs without withCredentials', () => {
    svc.register({ username: 'bob', email: 'b@b.com', password: 'pass' }).subscribe();
    const req = http.expectOne(r => r.url.includes('/register'));

    expect(req.request.withCredentials).toBeFalse();
    req.flush(null, { status: 201, statusText: 'Created' });
  });

  it('refreshToken() uses withCredentials and stores new token', () => {
    svc.refreshToken().subscribe();
    const req = http.expectOne(r => r.url.includes('/refresh'));

    expect(req.request.withCredentials).toBeTrue();
    req.flush({ accessToken: 'new-jwt' });
    expect(storage.getToken()).toBe('new-jwt');
  });

  it('logout() clears token even on server error', () => {
    storage.setToken('existing-token');
    svc.logout().subscribe({ error: () => {} });
    const req = http.expectOne(r => r.url.includes('/logout'));

    req.flush(null, { status: 500, statusText: 'Error' });
    // Note: tap fires before error — token IS cleared
    expect(storage.getToken()).toBeNull();
  });
});