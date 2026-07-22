import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';

export interface AuthResponse {
  token: string;
  email: string;
  fullName: string;
  role: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'https://campus-alliance-api.onrender.com/api/auth';
  
  private currentUserSubject = new BehaviorSubject<AuthResponse | null>(this.getStoredUser());
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient) {}

  login(credentials: any): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, credentials).pipe(
      tap(response => {
        localStorage.setItem('user', JSON.stringify(response));
        this.currentUserSubject.next(response);
      })
    );
  }

  register(userData: any): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/register`, userData).pipe(
      tap(response => {
        localStorage.setItem('user', JSON.stringify(response));
        this.currentUserSubject.next(response);
      })
    );
  }

  logout() {
    localStorage.removeItem('user');
    this.currentUserSubject.next(null);
  }

  getToken(): string | null {
    const user = this.getStoredUser();
    return user ? user.token : null;
  }

  getRole(): string | null {
    const user = this.getStoredUser();
    return user ? user.role : null;
  }
  
  getUserName(): string | null {
    const user = this.getStoredUser();
    return user ? user.fullName : null;
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  private getStoredUser(): AuthResponse | null {
    const userStr = localStorage.getItem('user');
    if (userStr) {
      return JSON.parse(userStr);
    }
    return null;
  }
}
