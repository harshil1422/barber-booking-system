import { Component, inject, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule }         from '@angular/common';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { LoadingService }       from '@core/services/loading.service';

@Component({
  selector: 'app-loading-bar',
  standalone: true,
  imports: [CommonModule, MatProgressBarModule],
  template: `
    @if (loader.isLoading()) {
      <mat-progress-bar
        mode="indeterminate"
        color="primary"
        class="global-loader"
        aria-label="Loading">
      </mat-progress-bar>
    }
  `,
  styles: [`
    .global-loader {
      position: fixed;
      top: 0; left: 0; right: 0;
      z-index: 9999;
      height: 3px;
    }
  `],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class LoadingBarComponent {
  readonly loader = inject(LoadingService);
}
