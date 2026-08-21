import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ResourceService, ResourceDto, ResourceVersionDto } from '../resource.service';
import { BookmarkService } from '../../bookmarks/bookmark.service';
import { AuthService } from '../../auth/auth.service';

@Component({
  selector: 'app-resource-repository',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './resource-repository.component.html',
  styleUrls: ['./resource-repository.component.css']
})
export class ResourceRepositoryComponent implements OnInit {
  resources: ResourceDto[] = [];
  bookmarkedMap: { [id: number]: boolean } = {};
  searchQuery: string = '';
  userRole: string = '';
  
  // Version History Panel state
  selectedResource: ResourceDto | null = null;
  isVersionPanelOpen = false;

  // New Resource Upload state
  isUploadModalOpen = false;
  uploadData = { title: '', courseName: '', description: '', file: null as File | null };

  constructor(
    private resourceService: ResourceService,
    private bookmarkService: BookmarkService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.userRole = this.authService.getRole() || '';
    this.loadResources();
  }

  deleteResource(r: ResourceDto) {
    if (!confirm(`Are you sure you want to permanently delete "${r.title}"?`)) {
      return;
    }
    this.resourceService.deleteResource(r.id).subscribe({
      next: () => {
        this.loadResources();
      },
      error: (err) => alert(err.error?.message || 'Failed to delete resource.')
    });
  }

  loadResources() {
    this.resourceService.getResources(this.searchQuery).subscribe({
      next: (data) => {
        this.resources = data;
        this.resources.forEach(r => this.checkBookmarkStatus(r.id));
      },
      error: (err) => console.error('Error loading resources', err)
    });
  }

  checkBookmarkStatus(id: number) {
    this.bookmarkService.checkBookmark('RESOURCE', id).subscribe(res => {
      this.bookmarkedMap[id] = res.bookmarked;
    });
  }

  toggleBookmark(resource: ResourceDto) {
    this.bookmarkService.toggleBookmark('RESOURCE', resource.id).subscribe(res => {
      this.bookmarkedMap[resource.id] = res.bookmarked;
    });
  }

  onSearch() {
    this.loadResources();
  }

  openVersionHistory(resource: ResourceDto) {
    this.selectedResource = resource;
    this.isVersionPanelOpen = true;
  }

  closeVersionHistory() {
    this.isVersionPanelOpen = false;
    this.selectedResource = null;
  }

  downloadVersion(resourceId: number, versionId?: number) {
    this.resourceService.downloadResource(resourceId, versionId).subscribe(blob => {
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      
      let fileName = 'download';
      if (this.selectedResource && this.selectedResource.versions) {
          if (versionId) {
              const v = this.selectedResource.versions.find(v => v.id === versionId);
              if (v) fileName = v.fileName;
          } else {
              const latest = this.getLatestVersion(this.selectedResource);
              if (latest) fileName = latest.fileName;
          }
      }
      
      a.download = fileName;
      document.body.appendChild(a);
      a.click();
      window.URL.revokeObjectURL(url);
      document.body.removeChild(a);
    });
  }

  // Upload new resource
  openUploadModal() {
    this.isUploadModalOpen = true;
  }
  
  closeUploadModal() {
    this.isUploadModalOpen = false;
    this.uploadData = { title: '', courseName: '', description: '', file: null };
  }

  onFileSelected(event: any) {
    if (event.target.files.length > 0) {
      this.uploadData.file = event.target.files[0];
    }
  }

  submitUpload() {
    if (!this.uploadData.file || !this.uploadData.title || !this.uploadData.courseName) return;
    
    this.resourceService.uploadNewResource(
      this.uploadData.title,
      this.uploadData.courseName,
      this.uploadData.description,
      this.uploadData.file
    ).subscribe({
      next: () => {
        this.closeUploadModal();
        this.loadResources(); // Refresh the list
      },
      error: (err) => alert('Failed to upload')
    });
  }

  // Helper for UI
  getFileExtension(fileName: string | undefined): string {
    if (!fileName) return 'FILE';
    const parts = fileName.split('.');
    return parts.length > 1 ? parts[parts.length - 1].toUpperCase() : 'FILE';
  }
  
  getLatestVersion(resource: ResourceDto): ResourceVersionDto | undefined {
    if (!resource.versions || resource.versions.length === 0) return undefined;
    // Assuming backend returns them in order, or we take the highest versionNumber
    return resource.versions.reduce((prev, current) => (prev.versionNumber > current.versionNumber) ? prev : current);
  }

  rateResource(resource: ResourceDto, rating: number) {
    this.resourceService.rateResource(resource.id, rating).subscribe({
      next: () => {
        // Optimistically update or reload
        this.loadResources();
      },
      error: (err) => console.error('Failed to rate resource', err)
    });
  }
}
