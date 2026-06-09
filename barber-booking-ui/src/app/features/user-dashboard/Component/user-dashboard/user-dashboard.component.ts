import { ChangeDetectionStrategy, Component } from '@angular/core';
import { NavbarComponent } from '@shared/components/navbar/navbar/navbar.component';

@Component({
  selector: 'app-user-dashboard',
  imports: [NavbarComponent],
  templateUrl: './user-dashboard.component.html',
  styleUrl: './user-dashboard.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class UserDashboardComponent {

}
