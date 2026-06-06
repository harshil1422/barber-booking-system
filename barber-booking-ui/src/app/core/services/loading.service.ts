import { Injectable, signal, computed } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class LoadingService {

  // Angular 19 signals — no BehaviorSubject, no RxJS needed in the service.
  // #count tracks concurrent in-flight requests.
  // isLoading is a computed signal: true when count > 0.
  readonly #count    = signal(0);
  readonly isLoading = computed(() => this.#count() > 0);

  /** Called by loadingInterceptor before the request is sent */
  show(): void {
    this.#count.update(n => n + 1);
  }

  /** Called by finalize() after the request completes or errors */
  hide(): void {
    // Math.max(0, n - 1) prevents the counter going negative if hide()
    // is called more times than show() (e.g. from cached responses).
    this.#count.update(n => Math.max(0, n - 1));
  }
}