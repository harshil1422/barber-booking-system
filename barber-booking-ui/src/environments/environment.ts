export const environment = {
  production: false,
  apiUrl: '/api',
  appName: 'Barber Booking',
  tokenKey: 'bb_access_token',
  refreshInterval: 60000,

};

// Base URL comes from environment — '/api' in dev (proxied), real URL in prod
      //  readonly #base = `${environment.apiUrl}/v1/auth`;
