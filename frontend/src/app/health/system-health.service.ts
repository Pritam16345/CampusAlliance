import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface ActuatorHealth {
  status: string;
  components?: {
    db?: {
      status: string;
      details?: {
        database: string;
        validationQuery: string;
      }
    };
    diskSpace?: {
      status: string;
      details?: {
        total: number;
        free: number;
        threshold: number;
        exists: boolean;
      }
    };
    ping?: {
      status: string;
    }
  }
}

@Injectable({
  providedIn: 'root'
})
export class SystemHealthService {
  private actuatorUrl = 'http://localhost:8080/actuator/health';

  constructor(private http: HttpClient) {}

  getHealth(): Observable<ActuatorHealth> {
    return this.http.get<ActuatorHealth>(this.actuatorUrl);
  }
}
