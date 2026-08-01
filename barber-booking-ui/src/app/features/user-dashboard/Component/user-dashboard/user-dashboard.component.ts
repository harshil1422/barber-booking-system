import { ChangeDetectionStrategy, Component } from '@angular/core';
import { NavbarComponent } from '@core/layout/navbar/navbar.component';

@Component({
  selector: 'app-user-dashboard',
  imports: [NavbarComponent],
  templateUrl: './user-dashboard.component.html',
  styleUrl: './user-dashboard.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class UserDashboardComponent {

}
