import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-user-management',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './user-management.component.html',
  styleUrls: ['./user-management.component.css']
})
export class UserManagementComponent implements OnInit {
  users: any[] = [];
  stats = { students: 0, faculty: 0, admins: 0 };
  private apiUrl = 'https://campus-alliance-api.onrender.com/api/admin/users';

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers() {
    this.http.get<any[]>(this.apiUrl).subscribe(data => {
      this.users = data;
      this.calculateStats();
    });
  }

  calculateStats() {
    this.stats = { students: 0, faculty: 0, admins: 0 };
    this.users.forEach(u => {
      if (u.role === 'STUDENT') this.stats.students++;
      else if (u.role === 'FACULTY') this.stats.faculty++;
      else if (u.role === 'ADMIN') this.stats.admins++;
    });
  }

  toggleStatus(id: number) {
    this.http.put(`${this.apiUrl}/${id}/toggle-status`, {}).subscribe(() => {
      this.loadUsers();
    });
  }
}
