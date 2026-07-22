import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface ResourceVersionDto {
  id: number;
  fileName: string;
  contentType: string;
  sizeBytes: number;
  uploadedAt: string;
  uploadedByEmail: string;
  uploadedByName: string;
  versionNumber: number;
}

export interface ResourceDto {
  id: number;
  title: string;
  description: string;
  courseName: string;
  uploadedByEmail: string;
  uploadedByName: string;
  totalVersions: number;
  versions: ResourceVersionDto[];
  createdAt: string;
  updatedAt: string;
}

@Injectable({
  providedIn: 'root'
})
export class ResourceService {
  private apiUrl = 'https://campus-alliance-api.onrender.com/api/resources';

  constructor(private http: HttpClient) {}

  getResources(keyword?: string): Observable<ResourceDto[]> {
    const params: any = {};
    if (keyword) params.keyword = keyword;
    return this.http.get<ResourceDto[]>(this.apiUrl, { params });
  }

  getResource(id: number): Observable<ResourceDto> {
    return this.http.get<ResourceDto>(`${this.apiUrl}/${id}`);
  }

  getVersions(id: number): Observable<ResourceVersionDto[]> {
    return this.http.get<ResourceVersionDto[]>(`${this.apiUrl}/${id}/versions`);
  }

  uploadNewResource(title: string, courseName: string, description: string, file: File): Observable<ResourceDto> {
    const formData = new FormData();
    formData.append('title', title);
    formData.append('courseName', courseName);
    if (description) formData.append('description', description);
    formData.append('file', file);

    return this.http.post<ResourceDto>(this.apiUrl, formData);
  }

  uploadNewVersion(id: number, file: File): Observable<ResourceVersionDto> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<ResourceVersionDto>(`${this.apiUrl}/${id}/versions`, formData);
  }

  downloadResource(id: number, versionId?: number) {
    let url = `${this.apiUrl}/${id}/download`;
    if (versionId) {
      url += `?versionId=${versionId}`;
    }
    // We fetch it as a blob so we can trigger the browser download
    return this.http.get(url, { responseType: 'blob' });
  }
}
