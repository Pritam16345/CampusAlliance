import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { Subscription, timer } from 'rxjs';
import { switchMap, catchError } from 'rxjs/operators';
import { of } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface AuditLogItem {
  id: number;
  action: string;
  performedBy: string;
  details: string;
  performedAt: string;
}

@Component({
  selector: 'app-audit-logs',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './audit-logs.component.html',
  styleUrls: ['./audit-logs.component.css']
})
export class AuditLogsComponent implements OnInit, OnDestroy {
  logs: AuditLogItem[] = [];
  searchQuery: string = '';
  selectedActionFilter: string = 'ALL';
  isLoading: boolean = false;
  lastUpdated: Date = new Date();
  private pollSub?: Subscription;

  private apiUrl = environment.apiUrl + '/api/admin/audit-logs';

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.startPolling();
  }

  ngOnDestroy(): void {
    if (this.pollSub) {
      this.pollSub.unsubscribe();
    }
  }

  startPolling(): void {
    // Refresh audit logs every 8 seconds in the background
    this.pollSub = timer(0, 8000).pipe(
      switchMap(() => this.http.get<AuditLogItem[]>(this.apiUrl).pipe(
        catchError(() => of([]))
      ))
    ).subscribe(data => {
      if (data && data.length > 0) {
        this.logs = data;
        this.lastUpdated = new Date();
      }
    });
  }

  refreshNow(): void {
    this.isLoading = true;
    this.http.get<AuditLogItem[]>(this.apiUrl).subscribe({
      next: (data) => {
        this.logs = data;
        this.lastUpdated = new Date();
        this.isLoading = false;
      },
      error: () => {
        this.isLoading = false;
      }
    });
  }

  get filteredLogs(): AuditLogItem[] {
    return this.logs.filter(log => {
      const user = (log.performedBy || '').toLowerCase();
      const action = (log.action || '').toLowerCase();
      const details = (log.details || '').toLowerCase();
      const query = this.searchQuery.trim().toLowerCase();

      const matchesSearch = !query || user.includes(query) || action.includes(query) || details.includes(query);
      const matchesFilter = this.selectedActionFilter === 'ALL' ||
        (this.selectedActionFilter === 'AUTH' && (action.includes('login') || action.includes('registered') || action.includes('auth'))) ||
        (this.selectedActionFilter === 'NOTICES' && action.includes('notice')) ||
        (this.selectedActionFilter === 'RESOURCES' && action.includes('resource')) ||
        (this.selectedActionFilter === 'SECURITY' && (action.includes('suspend') || action.includes('activate') || action.includes('delete')));

      return matchesSearch && matchesFilter;
    });
  }

  setActionFilter(filter: string): void {
    this.selectedActionFilter = filter;
  }

  getActionLabel(action: string): string {
    if (!action) return 'Action';
    return action.replace(/_/g, ' ');
  }

  getBadgeClass(action: string): string {
    const a = (action || '').toUpperCase();
    if (a.includes('DELETE') || a.includes('SUSPEND')) return 'badge-danger';
    if (a.includes('CREATE') || a.includes('UPLOAD') || a.includes('REGISTER') || a.includes('ACTIVATE')) return 'badge-success';
    if (a.includes('LOGIN') || a.includes('AUTH')) return 'badge-info';
    return 'badge-gray';
  }
}
