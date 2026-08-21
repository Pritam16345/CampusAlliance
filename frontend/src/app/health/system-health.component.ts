import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Subscription, timer, of } from 'rxjs';
import { switchMap, catchError } from 'rxjs/operators';
import { SystemHealthService, ActuatorHealth } from './system-health.service';

@Component({
  selector: 'app-system-health',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './system-health.component.html',
  styleUrls: ['./system-health.component.css']
})
export class SystemHealthComponent implements OnInit, OnDestroy {
  health: ActuatorHealth | null = null;
  lastChecked: Date = new Date();
  isLoading: boolean = false;
  isLivePolling: boolean = true;
  private pollSub?: Subscription;

  constructor(private healthService: SystemHealthService) {}

  ngOnInit(): void {
    this.startLiveMonitoring();
  }

  ngOnDestroy(): void {
    this.stopLiveMonitoring();
  }

  startLiveMonitoring(): void {
    // Polls every 5 seconds continuously in the background
    this.pollSub = timer(0, 5000)
      .pipe(
        switchMap(() => {
          return this.healthService.getHealth().pipe(
            catchError(err => {
              // If backend or DB is down / unreachable, reflect DOWN immediately
              return of({
                status: 'DOWN',
                components: {
                  db: { status: 'DOWN' },
                  diskSpace: { status: 'DOWN' }
                }
              } as ActuatorHealth);
            })
          );
        })
      )
      .subscribe((data) => {
        this.health = data;
        this.lastChecked = new Date();
      });
  }

  stopLiveMonitoring(): void {
    if (this.pollSub) {
      this.pollSub.unsubscribe();
    }
  }

  refreshNow(): void {
    this.isLoading = true;
    this.healthService.getHealth().pipe(
      catchError(err => of({
        status: 'DOWN',
        components: {
          db: { status: 'DOWN' },
          diskSpace: { status: 'DOWN' }
        }
      } as ActuatorHealth))
    ).subscribe((data) => {
      this.health = data;
      this.lastChecked = new Date();
      this.isLoading = false;
    });
  }

  get dbStatus(): string {
    return this.health?.components?.db?.status || 'UNKNOWN';
  }

  get storageStatus(): string {
    return this.health?.components?.diskSpace?.status || 'UNKNOWN';
  }

  get dbType(): string {
    return this.health?.components?.db?.details?.database || 'PostgreSQL';
  }

  get diskFreeGb(): string {
    const free = this.health?.components?.diskSpace?.details?.free;
    return free ? (free / (1024 * 1024 * 1024)).toFixed(2) + ' GB' : 'Available';
  }
}
