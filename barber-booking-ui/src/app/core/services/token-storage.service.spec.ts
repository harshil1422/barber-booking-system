/// <reference types="jasmine" />
import { TestBed }            from '@angular/core/testing';
import { TokenStorageService } from './token-storage.service';

// A real JWT signed with HS256 (expires far in the future — safe for tests)
const MOCK_JWT =
  'eyJhbGciOiJIUzI1NiJ9' +
  '.eyJzdWIiOiJhbGljZSIsInVzZXJJZCI6InV1aWQtMSIsInJvbGVzIjpbIlJPTEVfVVNFUiJdLCJleHAiOjk5OTk5OTk5OTl9' +
  '.signature';

describe('TokenStorageService', () => {
  let svc: TokenStorageService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    svc = TestBed.inject(TokenStorageService);
  });

  it('starts with no token', () => {
    expect(svc.getToken).toBeNull();
    expect(svc.exists()).toBeFalse();
  });

  it('stores and retrieves a token', () => {
    svc.setToken('my-token');
    expect(svc.getToken()).toBe('my-token');
  });

  it('clears the token', () => {
    svc.setToken('my-token');
    svc.clear();
    expect(svc.getToken()).toBeNull();
  });

  it('projects JWT claims to AuthUser correctly', () => {
    svc.setToken(MOCK_JWT);
    const user = svc.toAuthUser();
    expect(user?.username).toBe('alice');
    expect(user?.userId).toBe('uuid-1');
    expect(user?.roles).toContain('ROLE_USER');
  });

  it('returns null toAuthUser when no token set', () => {
    expect(svc.toAuthUser()).toBeNull();
  });

  it('hasRole returns true for matching role', () => {
    svc.setToken(MOCK_JWT);
    expect(svc.hasRole('ROLE_USER')).toBeTrue();
    expect(svc.hasRole('ROLE_ADMIN')).toBeFalse();
  });

  it('isExpired returns true for invalid token', () => {
    svc.setToken('not-a-jwt');
    expect(svc.isExpired()).toBeTrue();
  });
});