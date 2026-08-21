import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../../auth/auth.service';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-user-management',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './user-management.component.html',
  styleUrls: ['./user-management.component.css']
})
export class UserManagementComponent implements OnInit {
  users: any[] = [];
  stats = { students: 0, faculty: 0, admins: 0 };
  searchQuery: string = '';
  selectedRoleFilter: string = 'ALL';
  selectedStatusFilter: string = 'ALL';
  currentUserEmail: string | null = '';
  errorMessage: string = '';
  successMessage: string = '';

  private apiUrl = environment.apiUrl + '/api/admin/users';

  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.currentUserEmail = this.authService.getUserEmail();
    this.loadUsers();
  }

  loadUsers() {
    this.http.get<any[]>(this.apiUrl).subscribe({
      next: (data) => {
        this.users = data;
        this.calculateStats();
      },
      error: (err) => {
        this.errorMessage = 'Failed to load users.';
      }
    });
  }

  get filteredUsers(): any[] {
    return this.users.filter(u => {
      const name = (u.fullName || u.name || '').toLowerCase();
      const email = (u.email || '').toLowerCase();
      const search = this.searchQuery.trim().toLowerCase();

      const matchesSearch = !search || name.includes(search) || email.includes(search);
      const matchesRole = this.selectedRoleFilter === 'ALL' || u.role === this.selectedRoleFilter;
      const matchesStatus = this.selectedStatusFilter === 'ALL' ||
        (this.selectedStatusFilter === 'ACTIVE' && u.active) ||
        (this.selectedStatusFilter === 'SUSPENDED' && !u.active);

      return matchesSearch && matchesRole && matchesStatus;
    });
  }

  setRoleFilter(role: string) {
    this.selectedRoleFilter = role;
  }

  setStatusFilter(status: string) {
    this.selectedStatusFilter = status;
  }

  calculateStats() {
    this.stats = { students: 0, faculty: 0, admins: 0 };
    this.users.forEach(u => {
      if (u.role === 'STUDENT') this.stats.students++;
      else if (u.role === 'FACULTY') this.stats.faculty++;
      else if (u.role === 'ADMIN') this.stats.admins++;
    });
  }

  toggleStatus(user: any) {
    if (user.email === this.currentUserEmail && user.active) {
      alert('You cannot suspend your own active administrator account.');
      return;
    }

    const action = user.active ? 'suspend' : 'activate';
    if (!confirm(`Are you sure you want to ${action} ${user.fullName || user.email}?`)) {
      return;
    }

    this.http.put(`${this.apiUrl}/${user.id}/toggle-status`, {}).subscribe({
      next: () => {
        this.loadUsers();
      },
      error: (err) => {
        alert(err.error?.message || `Failed to ${action} user.`);
      }
    });
  }
}
