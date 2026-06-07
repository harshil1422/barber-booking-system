import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

export function passwordMatchValidator(): ValidatorFn {
  return (group: AbstractControl): ValidationErrors | null => {
    const pw  = group.get('password')?.value;
    const cpw = group.get('confirmPassword')?.value;
    return pw && cpw && pw !== cpw ? { passwordMismatch: true } : null;
  };
}

export function passwordStrengthValidator(): ValidatorFn {
  return (ctrl: AbstractControl): ValidationErrors | null => {
    const v: string = ctrl.value ?? '';
    const errs: ValidationErrors = {};
    if (!/[A-Z]/.test(v))       errs['missingUppercase'] = true;
    if (!/[a-z]/.test(v))       errs['missingLowercase'] = true;
    if (!/\d/.test(v))          errs['missingNumber']    = true;
    if (!/[!@#$%^&*]/.test(v))  errs['missingSpecial']   = true;
    return Object.keys(errs).length ? errs : null;
  };
}
