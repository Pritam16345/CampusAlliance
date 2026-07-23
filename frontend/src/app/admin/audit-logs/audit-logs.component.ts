import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-audit-logs',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './audit-logs.component.html',
  styleUrls: ['./audit-logs.component.css']
})
export class AuditLogsComponent implements OnInit {
  logs: any[] = [];
  private apiUrl = 'https://campus-alliance-api.onrender.com/api/admin/audit-logs';

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.http.get<any[]>(this.apiUrl).subscribe(data => {
      this.logs = data;
    });
  }

  getBadgeClass(actionType: string): string {
    const t = actionType?.toLowerCase() || '';
    if (t.includes('user')) return 'badge-green';
    if (t.includes('notice')) return 'badge-blue';
    if (t.includes('resource')) return 'badge-purple';
    return 'badge-gray';
  }
}
