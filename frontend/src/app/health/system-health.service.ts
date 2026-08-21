import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

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
  private actuatorUrl = environment.apiUrl + '/actuator/health';

  constructor(private http: HttpClient) {}

  getHealth(): Observable<ActuatorHealth> {
    return this.http.get<ActuatorHealth>(this.actuatorUrl);
  }
}
