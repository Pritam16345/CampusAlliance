import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ResourceService, ResourceDto, ResourceVersionDto } from '../resource.service';
import { BookmarkService } from '../../bookmarks/bookmark.service';

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
  
  // Version History Panel state
  selectedResource: ResourceDto | null = null;
  isVersionPanelOpen = false;

  // New Resource Upload state
  isUploadModalOpen = false;
  uploadData = { title: '', courseName: '', description: '', file: null as File | null };

  constructor(
    private resourceService: ResourceService,
    private bookmarkService: BookmarkService
  ) {}

  ngOnInit(): void {
    this.loadResources();
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
      // We don't have the filename here easily from the blob response unless we parse content-disposition header.
      // So we'll just use a fallback name or the user's browser will figure it out if we just open it.
      a.download = this.selectedResource?.title || 'download';
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
