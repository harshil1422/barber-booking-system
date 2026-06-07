import { LoginComponent } from "./Component/login/login.component";
import { RegisterComponent } from "./Component/register/register.component";
import { noAuthGuard } from "@core/guards/no-auth.guard";
import {Routes} from '@angular/router' ;




export const AUTH_ROUTES: Routes = [
 {
    path: '',
    canActivate: [noAuthGuard],
    children: [
      { path: 'login',    component: LoginComponent },
      { path: 'register', component: RegisterComponent },
      { path: '',         redirectTo: 'login', pathMatch: 'full' },
    ]
  }
]