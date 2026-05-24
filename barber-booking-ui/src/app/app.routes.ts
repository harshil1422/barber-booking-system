import { Routes } from '@angular/router';
import {BarberListComponent} from './features/barber-shop/Components/barber-list/barber-list.component'
import { BarberSlotsComponent } from './features/barber-shop/Components/barber-slots/barber-slots.component';
import { ShopRegisterComponent } from './features/barber-shop/Components/shop-register/shop-register.component';

export const routes: Routes = [
    {path:'', redirectTo:'barbers',pathMatch :'full'},
      {path:'barbers',component:ShopRegisterComponent},
      {path:'barbers/:id/slot',component:BarberSlotsComponent}
];
