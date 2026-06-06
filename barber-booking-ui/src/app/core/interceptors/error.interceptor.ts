import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject }      from '@angular/core';
import { throwError }  from 'rxjs';
import { catchError }  from 'rxjs/operators';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ApiError }   from '@core/models/api.models';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const snackBar = inject(MatSnackBar);

  return next(req).pipe(
    catchError((err: HttpErrorResponse) => {

      // Parse Spring Boot ApiError shape from GlobalExceptionHandler.
      // Falls back to a generic message if the body is not the expected shape.
      const apiError = err.error as ApiError;
      const message  = apiError?.message ?? defaultMessage(err.status);

      // 401 is handled silently by authInterceptor — never show a snackbar for it.
      if (err.status !== 401) {
        snackBar.open(message, 'Dismiss', {
          duration:   5000,
          panelClass: ['snack-error'],
        });
      }

      // Always re-throw — callers (effects) still need to handle the error.
      return throwError(() => err);
    }),
  );
};

function defaultMessage(status: number): string {
  const map: Record<number, string> = {
    0:   'No connection — check your network',
    400: 'Invalid request',
    403: 'You do not have permission to do this',
    404: 'Resource not found',
    409: 'Conflict — resource already exists',
    500: 'Server error — please try again later',
  };
  return map[status] ?? 'An unexpected error occurred';
}