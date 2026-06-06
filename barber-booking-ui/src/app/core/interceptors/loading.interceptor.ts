import { HttpInterceptorFn } from '@angular/common/http';
import { inject }          from '@angular/core';
import { finalize }        from 'rxjs/operators';
import { LoadingService }  from '@core/services/loading.service';

export const loadingInterceptor: HttpInterceptorFn = (req, next) => {

  // Requests can opt out of the loading bar by setting this header.
  // The header is stripped before forwarding — server never sees it.
  // Example: this.http.get('/api/suggestions', {
  //   headers: { 'X-Skip-Loading': 'true' }
  // })
  if (req.headers.has('X-Skip-Loading')) {
    return next(req.clone({
      headers: req.headers.delete('X-Skip-Loading'),
    }));
  }

  const loader = inject(LoadingService);
  loader.show();

  // finalize() runs on complete AND on error — loader.hide() always fires.
  // Without this a failed request would leave the bar visible forever.
  return next(req).pipe(
    finalize(() => loader.hide()),
  );
};