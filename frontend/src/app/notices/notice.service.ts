import { Injectable, NgZone } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from '../auth/auth.service';

export interface NoticeDto {
  id: number;
  title: string;
  content: string;
  postedByEmail: string;
  postedByName: string;
  createdAt: string;
  updatedAt: string;
  version: number;
  seenCount: number;
}

@Injectable({
  providedIn: 'root'
})
export class NoticeService {
  private apiUrl = 'http://localhost:8080/api/notices';
  private sseUrl = 'http://localhost:8080/api/sse/notices';

  constructor(private http: HttpClient, private authService: AuthService, private zone: NgZone) {}

  getNotices(): Observable<NoticeDto[]> {
    return this.http.get<NoticeDto[]>(this.apiUrl);
  }

  getNotice(id: number): Observable<NoticeDto> {
    return this.http.get<NoticeDto>(`${this.apiUrl}/${id}`);
  }

  createNotice(title: string, content: string): Observable<NoticeDto> {
    return this.http.post<NoticeDto>(this.apiUrl, { title, content });
  }

  updateNotice(id: number, title: string, content: string, version: number): Observable<NoticeDto> {
    return this.http.put<NoticeDto>(`${this.apiUrl}/${id}`, { title, content, version });
  }

  deleteNotice(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  markAsSeen(id: number): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/${id}/seen`, {});
  }

  listenToLiveNotices(onMessage: (notice: NoticeDto) => void): () => void {
    const eventSource = new EventSource(this.sseUrl);
    
    eventSource.onmessage = (event) => {
      this.zone.run(() => {
        if (event.data) {
          const notice: NoticeDto = JSON.parse(event.data);
          onMessage(notice);
        }
      });
    };
    
    eventSource.onerror = (error) => {
      console.error('SSE Error:', error);
      // EventSource auto-reconnects, but if we need manual logic, we could handle it here.
    };

    // Return an unsubscribe function
    return () => {
      eventSource.close();
    };
  }
}
