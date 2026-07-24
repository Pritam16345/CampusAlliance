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
