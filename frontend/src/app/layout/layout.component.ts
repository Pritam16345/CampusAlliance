import { Component } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../auth/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-layout',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive, CommonModule],
  templateUrl: './layout.component.html',
  styleUrls: ['./layout.component.css']
})
export class LayoutComponent {
  userRole = '';
  
  constructor(public authService: AuthService, private router: Router) {
    this.userRole = this.authService.getRole() || '';
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
