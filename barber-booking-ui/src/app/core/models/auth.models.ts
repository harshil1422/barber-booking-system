// ── HTTP request / response DTOs ─────────────────────────────────────────
// These match the Java records in your Spring Boot AuthController exactly.

export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  email:    string;
  password: string;
}

export interface TokenResponse {
  accessToken: string;  // refresh token arrives as HttpOnly cookie, not in body
}

// ── Raw JWT payload ───────────────────────────────────────────────────────
// These are the exact claims your Spring Boot JwtTokenProvider embeds.
// sub = username, userId = UUID, roles = ['ROLE_USER','ROLE_ADMIN']

export interface JwtPayload {
  sub:    string;    // username (JWT standard claim)
  userId: string;    // UUID from Spring Boot
  roles:  string[];  // ['ROLE_USER', 'ROLE_ADMIN']
  iat:    number;    // issued at (seconds since epoch)
  exp:    number;    // expires at (seconds since epoch)
  iss:    string;    // issuer — e.g. "your-app"
}

// ── App-level user model ──────────────────────────────────────────────────
// A simplified projection of JwtPayload used throughout UI and selectors.
// Never put the raw JwtPayload into the store — always project to AuthUser.

export interface AuthUser {
  userId:   string;
  username: string;
  roles:    string[];
  exp:      number;  // kept so components can show session expiry if needed
}

// ── NgRx feature state ────────────────────────────────────────────────────
// This is exactly what sits in the Redux store under the 'auth' key.
// initialized = true after APP_INITIALIZER checkAuth attempt completes.

export interface AuthState {
  user:        AuthUser | null;
  accessToken: string   | null;
  loading:     boolean;
  error:       string   | null;
  initialized: boolean;
}

export const initialAuthState: AuthState = {
  user:        null,
  accessToken: null,
  loading:     false,
  error:       null,
  initialized: false,
};

// ── Role constants ────────────────────────────────────────────────────────
// Must match Spring Boot UserRole enum values exactly.
// Use these in route guards: data: { roles: [UserRole.Admin] }

export enum UserRole {
  User      = 'ROLE_USER',
  Admin     = 'ROLE_ADMIN',
  Moderator = 'ROLE_MODERATOR',
}