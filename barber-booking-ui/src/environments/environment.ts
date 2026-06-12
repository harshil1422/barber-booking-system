export const environment = {
  production: false,
  apiUrl: '/api',
  appName:       'New Look',
  tokenKey: 'bb_access_token',
  refreshInterval: 60000,
  apiGatewayUrl: 'http://localhost:9090/api/v1',
  sseUrl:        'http://localhost:8086/api/v1/notifications/stream',
  googleMapsKey: 'YOUR_GOOGLE_MAPS_API_KEY',
  razorpayKey:   'rzp_test_XXXXXXXXXXXXXXXX',
  version:       '1.0.0-dev'

};

// Base URL comes from environment — '/api' in dev (proxied), real URL in prod
      //  readonly #base = `${environment.apiUrl}/v1/auth`;
