import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../auth.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  authForm: FormGroup;
  selectedRole: string = 'STUDENT'; // default tab
  errorMessage: string = '';
  isLoading: boolean = false;
  isRegisterMode: boolean = false;

  roles = [
    { id: 'STUDENT', label: 'Student' },
    { id: 'FACULTY', label: 'Faculty' }
  ];

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.authForm = this.fb.group({
      fullName: [''],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  setRole(roleId: string) {
    this.selectedRole = roleId;
    this.errorMessage = '';
  }

  toggleMode() {
    this.isRegisterMode = !this.isRegisterMode;
    this.errorMessage = '';
    if (this.isRegisterMode) {
      this.authForm.get('fullName')?.setValidators([Validators.required]);
    } else {
      this.authForm.get('fullName')?.clearValidators();
    }
    this.authForm.get('fullName')?.updateValueAndValidity();
  }

  onSubmit() {
    if (this.authForm.invalid) return;

    this.isLoading = true;
    this.errorMessage = '';

    const formData = { ...this.authForm.value, role: this.selectedRole };

    if (this.isRegisterMode) {
      this.authService.register(formData).subscribe({
        next: (res) => {
          this.router.navigate(['/resources']);
        },
        error: (err) => {
          this.isLoading = false;
          if (err.status === 0 || err.status >= 500) {
            this.errorMessage = 'Cannot connect to server. Please try again in a moment.';
          } else {
            this.errorMessage = err.error?.message || 'Registration failed. Please check your details.';
          }
        }
      });
    } else {
      const loginData = { email: formData.email, password: formData.password };
      this.authService.login(loginData).subscribe({
        next: (res) => {
          this.router.navigate(['/resources']);
        },
        error: (err) => {
          this.isLoading = false;
          if (err.status === 0 || err.status >= 500) {
            this.errorMessage = 'Cannot connect to server. Please try again in a moment.';
          } else {
            this.errorMessage = err.error?.message || 'Invalid credentials. Please verify your email and password.';
          }
        }
      });
    }
  }
}
