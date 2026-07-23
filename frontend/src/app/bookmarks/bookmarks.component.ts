import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { BookmarkService } from './bookmark.service';

@Component({
  selector: 'app-bookmarks',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './bookmarks.component.html',
  styleUrls: ['./bookmarks.component.css']
})
export class BookmarksComponent implements OnInit {
  bookmarks: any[] = [];

  constructor(
    private bookmarkService: BookmarkService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadBookmarks();
  }

  loadBookmarks() {
    this.bookmarkService.getBookmarks().subscribe({
      next: (data) => this.bookmarks = data,
      error: (err) => console.error('Error loading bookmarks', err)
    });
  }

  unbookmark(bookmarkId: number, targetType: string, targetId: number, event: Event) {
    event.stopPropagation();
    this.bookmarkService.toggleBookmark(targetType, targetId).subscribe({
      next: () => this.loadBookmarks(),
      error: (err) => console.error('Error unbookmarking', err)
    });
  }

  navigateToItem(bookmark: any) {
    if (bookmark.targetType === 'NOTICE') {
      this.router.navigate(['/live-notices']); // Or specific notice if we had route
    } else if (bookmark.targetType === 'RESOURCE') {
      this.router.navigate(['/resources']);
    }
  }
}
