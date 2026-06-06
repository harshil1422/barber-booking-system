import { LoginComponent } from "./Component/login/login.component";
import { RegisterComponent } from "./Component/register/register.component";
import { NoAuthGuard } from "./guards/no-auth.guard";




export const AUTH_ROUTES = [
 {
    path: '',
    canActivate: [NoAuthGuard],
    children: [
      { path: 'login',    component: LoginComponent },
      { path: 'register', component: RegisterComponent },
      { path: '',         redirectTo: 'login', pathMatch: 'full' },
    ]
  }
]