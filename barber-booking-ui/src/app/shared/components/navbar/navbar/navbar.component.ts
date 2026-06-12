import { ChangeDetectionStrategy,Component,inject,computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatMenuModule } from '@angular/material/menu';
import { MatIconModule } from '@angular/material/icon';
import { MatBadgeModule } from '@angular/material/badge';
import { Store } from '@ngrx/store';
import {  AuthSelectors } from '@store/auth/selectors/auth.selectors';
import { AuthActions,  } from '@store/auth/actions/auth.actions';
import { DashboardActions } from '@store/dashboard/dashboard.action';
import { MatDivider } from '@angular/material/divider';

 

@Component({
  selector: 'nl-navbar',
  imports: [
    CommonModule,
    RouterModule,
    MatToolbarModule,
    MatButtonModule,
    MatMenuModule,
    MatIconModule,
    MatBadgeModule,
    MatDivider
  ],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class NavbarComponent {

    #store = inject(Store);
  private router = inject(Router);
 
  user       = this.#store.selectSignal(AuthSelectors.selectUser);
  activeRole = this.#store.selectSignal(AuthSelectors.selectActiveRole);
  isBarber  = this.#store.selectSignal(AuthSelectors.selectIsBarber);
  initials  = computed(() => this.user()?.username?.charAt(0).toUpperCase() ?? 'U');
 
  switchToRole(role: 'USER' | 'BARBER'): void {
    this.#store.dispatch(DashboardActions.switchRole({ role }));
    this.router.navigate([role === 'BARBER' ? '/barber' : '/dashboard']);
  }
 
  onLogout(): void {
    this.#store.dispatch(AuthActions.logout());
    // this.router.navigate(['/login']);
  }

}
