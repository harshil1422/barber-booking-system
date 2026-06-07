import { Component, OnInit, OnDestroy, ChangeDetectionStrategy, inject } from '@angular/core';
import { CommonModule }         from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { RouterLink }           from '@angular/router';
import { Store }                from '@ngrx/store';
import { Subject }              from 'rxjs';
import { takeUntil }            from 'rxjs/operators';
import { MatCardModule }        from '@angular/material/card';
import { MatFormFieldModule }   from '@angular/material/form-field';
import { MatInputModule }       from '@angular/material/input';
import { MatButtonModule }      from '@angular/material/button';
import { MatIconModule }        from '@angular/material/icon';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { AuthActions }          from '@store/auth/actions/auth.actions';
import { AuthSelectors }        from '@store/auth/selectors/auth.selectors';
import { passwordMatchValidator, passwordStrengthValidator } from '@shared/validators/password.validators';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [
    CommonModule, ReactiveFormsModule, RouterLink,
    MatCardModule, MatFormFieldModule, MatInputModule,
    MatButtonModule, MatIconModule, MatProgressBarModule, MatProgressSpinnerModule,
  ],
  templateUrl: './register.component.html',
  styleUrls:   ['./register.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RegisterComponent implements OnInit, OnDestroy {

  readonly #fb      = inject(FormBuilder);
  readonly #store   = inject(Store);
  readonly #destroy = new Subject<void>();

  form!: FormGroup;
  hidePassword        = true;
  hideConfirmPassword = true;

  readonly isLoading = this.#store.selectSignal(AuthSelectors.selectIsLoading);
  readonly error     = this.#store.selectSignal(AuthSelectors.selectError);

  ngOnInit(): void {
    this.form = this.#fb.group(
      {
        username:        ['', [Validators.required, Validators.minLength(3), Validators.maxLength(50)]],
        email:           ['', [Validators.required, Validators.email]],
        password:        ['', [Validators.required, Validators.minLength(8), passwordStrengthValidator()]],
        confirmPassword: ['', Validators.required],
      },
      { validators: passwordMatchValidator() },
    );

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
    this.#store.dispatch(AuthActions.register({
      request: {
        username: this.form.value.username.trim(),
        email:    this.form.value.email.trim().toLowerCase(),
        password: this.form.value.password,
      },
    }));
  }

  strengthScore(): number {
    const v: string = this.password.value ?? '';
    return [/[A-Z]/, /[a-z]/, /\d/, /[!@#$%^&*]/, /.{12}/].filter(r => r.test(v)).length;
  }

  strengthLabel(): string {
    const s = this.strengthScore();
    return s <= 1 ? 'Weak' : s <= 3 ? 'Fair' : s === 4 ? 'Strong' : 'Very strong';
  }

  strengthColor(): string {
    const s = this.strengthScore();
    return s <= 1 ? 'warn' : s <= 3 ? 'accent' : 'primary';
  }

  get username()        { return this.form.get('username')!; }
  get email()           { return this.form.get('email')!; }
  get password()        { return this.form.get('password')!; }
  get confirmPassword() { return this.form.get('confirmPassword')!; }
}
