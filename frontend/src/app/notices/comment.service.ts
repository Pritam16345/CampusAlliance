import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({ providedIn: 'root' })
export class CommentService {
  private baseUrl = 'https://campus-alliance-api.onrender.com/api/notices';
  constructor(private http: HttpClient) {}
  getComments(noticeId: number) { return this.http.get<any[]>(`${this.baseUrl}/${noticeId}/comments`); }
  addComment(noticeId: number, content: string) { return this.http.post<any>(`${this.baseUrl}/${noticeId}/comments`, {content}); }
  deleteComment(noticeId: number, commentId: number) { return this.http.delete(`${this.baseUrl}/${noticeId}/comments/${commentId}`); }
}
