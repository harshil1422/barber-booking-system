import { Routes } from '@angular/router';
import { authGuard } from '@core/guards/auth.guard';
import {BarberListComponent} from './features/barber-shop/Components/barber-list/barber-list.component'
import { BarberSlotsComponent } from './features/barber-shop/Components/barber-slots/barber-slots.component';
import { ShopRegisterComponent } from './features/barber-shop/Components/shop-register/shop-register.component';

export const APP_ROUTES: Routes = [

    {
    path: 'auth',
    loadChildren: () =>
      import('./features/auth/auth.routes')
        .then(r => r.AUTH_ROUTES),
  },
  {
    path: '',
    // canActivate: [authGuard],
    loadComponent: () =>
      import('./shell/shell.component').then(c => c.ShellComponent),
    children: [

       {
    path: 'dashboard',
    loadChildren: () => import('./features/user-dashboard/user-dashboard.routes')
    .then(d=> d.USER_DASHBOARD_ROUTES)
  },

    ]
  },
 

      // {path:'', redirectTo:'barbers',pathMatch :'full'},
      // {path:'barbers',component:ShopRegisterComponent},
      // {path:'barbers/:id/slot',component:BarberSlotsComponent}
];
