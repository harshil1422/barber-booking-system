// src/app/shell/shell.component.ts
import {
  Component, OnInit, ChangeDetectionStrategy,
  inject, computed, signal
} from '@angular/core';
import { Router, RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { CommonModule }      from '@angular/common';
import { Store }             from '@ngrx/store';

// Angular Material
import { MatToolbarModule }       from '@angular/material/toolbar';
import { MatSidenavModule }       from '@angular/material/sidenav';
import { MatListModule }          from '@angular/material/list';
import { MatIconModule }          from '@angular/material/icon';
import { MatButtonModule }        from '@angular/material/button';
import { MatBadgeModule }         from '@angular/material/badge';
import { MatMenuModule }          from '@angular/material/menu';
import { MatDividerModule }       from '@angular/material/divider';
import { MatButtonToggleModule }  from '@angular/material/button-toggle';
import { MatTooltipModule }       from '@angular/material/tooltip';
import { MatFormFieldModule }     from '@angular/material/form-field';
import { MatInputModule }         from '@angular/material/input';
import {AuthSelectors }from '@store/auth/selectors/auth.selectors';
import { AuthService }            from '@core/services/auth.service';
import { NotificationService }    from '@core/services/notification.service';
import { UiActions }              from '@store/ui/ui.reducer';
import { UserRole }               from '@core/models/auth.models';


interface NavItem {
  label:    string;
  icon:     string;
  route:    string;
  badge?:   number;
  roles?:   UserRole[];        // if set, only show for these roles
}

@Component({
  selector:        'nl-shell',
  standalone:      true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    CommonModule, RouterOutlet, RouterLink, RouterLinkActive,
    MatToolbarModule, MatSidenavModule, MatListModule, MatIconModule,
    MatButtonModule, MatBadgeModule, MatMenuModule, MatDividerModule,
    MatButtonToggleModule, MatTooltipModule, MatFormFieldModule, MatInputModule
  ],
  templateUrl: './shell.component.html',
  styleUrls:   ['./shell.component.scss']
})
export class ShellComponent implements OnInit {

  private store            = inject(Store);
  protected authService    = inject(AuthService);
  protected notifService   = inject(NotificationService);
  private router           = inject(Router);

  // Signals from store
  protected user     = this.store.selectSignal(AuthSelectors.selectUser);
  protected isBarber = this.store.selectSignal(AuthSelectors.selectIsBarber);
  protected hasShop  = this.store.selectSignal(AuthSelectors.selectHasShop);

  // Local UI signals
  protected sidenavOpen = signal(true);
  protected activeRole  = signal<UserRole>('USER');

  // Notification unread count from SSE service
  protected unreadCount = this.notifService.unreadCount;

  // User-facing nav items
  protected userNavItems: NavItem[] = [
    { label: 'Dashboard',       icon: 'dashboard',      route: '/dashboard' },
    { label: 'Nearby Shops',    icon: 'location_on',    route: '/discover',    badge: 8 },
    { label: 'Book a Slot',     icon: 'calendar_month', route: '/discover' },
    { label: 'My Appointments', icon: 'schedule',       route: '/my-bookings' }
  ];

  protected exploreNavItems: NavItem[] = [
    { label: 'Shop Gallery', icon: 'photo_library', route: '/media' },
    { label: 'Reviews',      icon: 'rate_review',   route: '/reviews', badge: 3 }
  ];

  protected accountNavItems: NavItem[] = [
    { label: 'Register Shop', icon: 'storefront', route: '/dashboard' },
    { label: 'Settings',      icon: 'settings',   route: '/dashboard' }
  ];

  // Computed initials for avatar
  protected initials = computed(() => {
    const username = this.user()?.username ?? 'User';
    return username.split(' ').map(n => n[0]).join('').slice(0, 2).toUpperCase();
  });

  ngOnInit(): void {
    this.notifService.connect();
  }

  protected toggleSidenav(): void {
    this.sidenavOpen.update(v => !v);
  }

  protected switchRole(role: UserRole): void {
    this.activeRole.set(role);
    this.store.dispatch(UiActions.setActiveRole({ role }));
    if (role === 'BARBER' && this.hasShop()) {
      this.router.navigate(['/barber']);
    } else if (role === 'BARBER') {
      this.router.navigate(['/dashboard']);  // shopGuard handles messaging
    } else {
      this.router.navigate(['/dashboard']);
    }
  }

  protected logout(): void {
    this.store.dispatch({ type: '[Auth] Logout' });
  }
}
