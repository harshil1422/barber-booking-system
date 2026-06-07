import { Component, ChangeDetectionStrategy, inject } from '@angular/core';
import { RouterOutlet, RouterLink } from '@angular/router';
import { Store }                   from '@ngrx/store';
import { MatToolbarModule }        from '@angular/material/toolbar';
import { MatButtonModule }         from '@angular/material/button';
import { MatIconModule }           from '@angular/material/icon';
import { MatMenuModule }           from '@angular/material/menu';
import { MatDividerModule }        from '@angular/material/divider';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { AuthSelectors }           from '@store/auth/selectors/auth.selectors';
import { AuthActions }             from '@store/auth/actions/auth.actions';
import { LoadingBarComponent } from '@shared/components/loading/loading.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    RouterOutlet, RouterLink,
    MatToolbarModule, MatButtonModule, MatIconModule,
    MatMenuModule, MatDividerModule, MatProgressSpinnerModule,
    LoadingBarComponent,
   
  ],
  templateUrl: './app.component.html',
  styleUrl:    './app.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AppComponent {

  readonly #store = inject(Store);

  // Angular 19 selectSignal — returns a native Signal, no async pipe needed.
  // OnPush change detection works because signals notify the framework directly.
  readonly initialized     = this.#store.selectSignal(AuthSelectors.selectInitialized);
  readonly isAuthenticated = this.#store.selectSignal(AuthSelectors.selectIsAuthenticated);
  readonly currentUser     = this.#store.selectSignal(AuthSelectors.selectUser);

  logout(): void {
    this.#store.dispatch(AuthActions.logout());
  }
}