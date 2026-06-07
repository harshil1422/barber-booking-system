import { Component, ChangeDetectionStrategy } from '@angular/core';
import { RouterLink }    from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule }   from '@angular/material/icon';

@Component({
  selector: 'app-forbidden',
  standalone: true,
  imports: [RouterLink, MatButtonModule, MatIconModule],
  template: `
    <div class="forbidden">
      <mat-icon class="icon">lock</mat-icon>
      <h1>Access denied</h1>
      <p>You do not have permission to view this page.</p>
      <a mat-raised-button color="primary" routerLink="/dashboard">Go to dashboard</a>
    </div>
  `,
  styles: [`
    .forbidden {
      display: flex; flex-direction: column; align-items: center;
      justify-content: center; min-height: 80vh; gap: 12px; text-align: center;
    }
    .icon { font-size: 64px; width: 64px; height: 64px; color: #c62828; }
    h1 { font-size: 1.5rem; font-weight: 500; margin: 0; }
    p  { color: rgba(0,0,0,.6); margin: 0; }
  `],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ForbiddenComponent {}
