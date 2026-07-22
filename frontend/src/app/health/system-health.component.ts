import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SystemHealthService, ActuatorHealth } from './system-health.service';

@Component({
  selector: 'app-system-health',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './system-health.component.html',
  styleUrls: ['./system-health.component.css']
})
export class SystemHealthComponent implements OnInit {
  health: ActuatorHealth | null = null;
  
  // Hardcoded uptime since actuator /health doesn't include it by default
  // and we didn't expose /metrics/process.uptime for simplicity.
  uptimeString = '14d 06h 22m';

  mockAuditLogs = [
    { timestamp: '2023-10-27 14:32:01', action: 'SOFT DELETED', user: 'admin.jdoe@alliance.edu', description: 'Admin soft-deleted Notice #402', ip: '192.168.1.105', type: 'danger' },
    { timestamp: '2023-10-27 14:15:22', action: 'UPDATED', user: 'system_service', description: 'System Health: DB Connection UP', ip: 'localhost', type: 'info' },
    { timestamp: '2023-10-27 11:05:45', action: 'CREATED', user: 'prof.smith@alliance.edu', description: "Created Resource Folder 'Fall 2023 Syllabus'", ip: '10.0.0.55', type: 'success' },
    { timestamp: '2023-10-27 09:30:12', action: 'WARNING', user: 'system_auth', description: "Failed login attempt for user 'admin.jdoe'", ip: '45.22.19.102', type: 'warning' },
    { timestamp: '2023-10-26 16:45:00', action: 'SOFT DELETED', user: 'admin.mlee@alliance.edu', description: 'Admin soft-deleted User Profile #8992', ip: '192.168.1.110', type: 'danger' }
  ];

  constructor(private healthService: SystemHealthService) {}

  ngOnInit(): void {
    this.healthService.getHealth().subscribe({
      next: (data) => this.health = data,
      error: (err) => console.error('Failed to load actuator health', err)
    });
  }

  get dbStatus(): string {
    return this.health?.components?.db?.status || 'UNKNOWN';
  }

  get storageStatus(): string {
    return this.health?.components?.diskSpace?.status || 'UNKNOWN';
  }
}
