import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class BookmarkService {
  private apiUrl = 'https://campus-alliance-api.onrender.com/api/bookmarks';
  constructor(private http: HttpClient) {}
  
  toggleBookmark(targetType: string, targetId: number): Observable<{bookmarked: boolean}> { 
    return this.http.post<{bookmarked: boolean}>(this.apiUrl, {targetType, targetId}); 
  }
  
  getBookmarks(): Observable<any[]> { 
    return this.http.get<any[]>(this.apiUrl); 
  }
  
  checkBookmark(targetType: string, targetId: number): Observable<{bookmarked: boolean}> { 
    return this.http.get<{bookmarked: boolean}>(`${this.apiUrl}/check?targetType=${targetType}&targetId=${targetId}`); 
  }
}
