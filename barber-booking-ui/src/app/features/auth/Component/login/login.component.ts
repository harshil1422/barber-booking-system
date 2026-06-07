import { Component, OnInit, OnDestroy, ChangeDetectionStrategy, inject } from '@angular/core';
import { CommonModule }          from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { RouterLink, ActivatedRoute }  from '@angular/router';
import { Store }                 from '@ngrx/store';
import { BehaviorSubject, Subject }               from 'rxjs';
import { takeUntil }             from 'rxjs/operators';
import { MatCardModule }         from '@angular/material/card';
import { MatFormFieldModule }    from '@angular/material/form-field';
import { MatInputModule }        from '@angular/material/input';
import { MatButtonModule }       from '@angular/material/button';
import { MatIconModule }         from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { AuthActions }           from '@store/auth/actions/auth.actions';
import { AuthSelectors }         from '@store/auth/selectors/auth.selectors';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    CommonModule, ReactiveFormsModule, RouterLink,
    MatCardModule, MatFormFieldModule, MatInputModule,
    MatButtonModule, MatIconModule, MatProgressSpinnerModule,
  ],
  templateUrl: './login.component.html',
  styleUrls:   ['./login.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class LoginComponent implements OnInit, OnDestroy {

  readonly #fb      = inject(FormBuilder);
  readonly #store   = inject(Store);
  readonly #route   = inject(ActivatedRoute);
  readonly #destroy = new Subject<void>();

  form!: FormGroup;
  hidePassword = true;

  // Angular 19: use selectSignal for signal-based store reads
  private isLoadingSubject = new BehaviorSubject<boolean>(false);
  isLoading$ = this.isLoadingSubject.asObservable();
  readonly isLoading = this.#store.selectSignal(AuthSelectors.selectIsLoading);
  private errorSubject = new BehaviorSubject<string | null>(null);
  error$ = this.errorSubject.asObservable();
  readonly error     = this.#store.selectSignal(AuthSelectors.selectError);

  readonly isRegistered = this.#route.snapshot.queryParams['registered'] === 'true';

  ngOnInit(): void {
    this.form = this.#fb.group({
      username: ['', [Validators.required, Validators.minLength(3)]],
      password: ['', [Validators.required, Validators.minLength(8)]],
    });

    // Clear stale API error as soon as user starts typing
    this.form.valueChanges
      .pipe(takeUntil(this.#destroy))
      .subscribe(() => this.#store.dispatch(AuthActions.clearError()));
  }

  ngOnDestroy(): void {
    this.#destroy.next();
    this.#destroy.complete();
  }

  submit(): void {
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }

    this.#store.dispatch(AuthActions.login({
      request: {
        username: this.form.value.username.trim(),
        password: this.form.value.password,
      },
    }));
  }

  get username() { return this.form.get('username')!; }
  get password() { return this.form.get('password')!; }
}
