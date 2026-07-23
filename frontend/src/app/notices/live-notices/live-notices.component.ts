import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { NoticeService, NoticeDto } from '../notice.service';
import { AuthService } from '../../auth/auth.service';
import { BookmarkService } from '../../bookmarks/bookmark.service';
import { CommentService } from '../comment.service';

@Component({
  selector: 'app-live-notices',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './live-notices.component.html',
  styleUrls: ['./live-notices.component.css']
})
export class LiveNoticesComponent implements OnInit, OnDestroy {
  notices: NoticeDto[] = [];
  private unsubscribeSse?: () => void;
  
  userRole: string = '';

  // New Notice Modal
  isModalOpen = false;
  newNotice = { title: '', content: '', targetAudience: 'All Students' };
  
  // Bookmarks
  bookmarkedMap: { [id: number]: boolean } = {};
  
  // Comments
  expandedComments: { [id: number]: boolean } = {};
  commentsMap: { [id: number]: any[] } = {};
  newCommentText: { [id: number]: string } = {};

  constructor(
    private noticeService: NoticeService, 
    private authService: AuthService,
    private bookmarkService: BookmarkService,
    private commentService: CommentService,
    private router: Router
  ) {}

  ngOnInit() {
    this.userRole = this.authService.getRole() || '';
    
    // Load initial notices
    this.noticeService.getNotices().subscribe(data => {
      // Sort newest first
      this.notices = data.sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime());
      this.notices.forEach(n => this.checkBookmarkStatus(n.id));
    });

    // Start SSE stream for live updates
    this.unsubscribeSse = this.noticeService.listenToLiveNotices((newNotice) => {
      // Check if it already exists (e.g., if we just created it)
      const index = this.notices.findIndex(n => n.id === newNotice.id);
      if (index !== -1) {
        this.notices[index] = newNotice;
      } else {
        this.notices.unshift(newNotice); // prepend
      }
    });
  }

  ngOnDestroy() {
    if (this.unsubscribeSse) {
      this.unsubscribeSse();
    }
  }

  openModal() {
    this.isModalOpen = true;
  }
  
  closeModal() {
    this.isModalOpen = false;
    this.newNotice = { title: '', content: '', targetAudience: 'All Students' };
  }

  submitNotice() {
    if (!this.newNotice.title || !this.newNotice.content) return;
    
    this.noticeService.createNotice(this.newNotice.title, this.newNotice.content, this.newNotice.targetAudience).subscribe({
      next: (res) => {
        this.closeModal();
      },
      error: () => alert('Failed to create notice')
    });
  }

  checkBookmarkStatus(id: number) {
    this.bookmarkService.checkBookmark('NOTICE', id).subscribe(res => {
      this.bookmarkedMap[id] = res.bookmarked;
    });
  }

  toggleBookmark(notice: NoticeDto) {
    this.bookmarkService.toggleBookmark('NOTICE', notice.id).subscribe(res => {
      this.bookmarkedMap[notice.id] = res.bookmarked;
    });
  }

  toggleComments(noticeId: number) {
    if (this.expandedComments[noticeId]) {
      this.expandedComments[noticeId] = false;
    } else {
      this.expandedComments[noticeId] = true;
      this.loadComments(noticeId);
    }
  }

  loadComments(noticeId: number) {
    this.commentService.getComments(noticeId).subscribe(data => {
      this.commentsMap[noticeId] = data;
    });
  }

  addComment(noticeId: number) {
    const content = this.newCommentText[noticeId];
    if (!content || !content.trim()) return;
    this.commentService.addComment(noticeId, content).subscribe(() => {
      this.newCommentText[noticeId] = '';
      this.loadComments(noticeId);
    });
  }

  deleteComment(noticeId: number, commentId: number) {
    this.commentService.deleteComment(noticeId, commentId).subscribe(() => {
      this.loadComments(noticeId);
    });
  }

  markAsSeen(id: number) {
    this.noticeService.markAsSeen(id).subscribe();
  }

  goToAnalytics(id: number) {
    // Navigate to the analytics page for this notice
    this.router.navigate(['/notices', id, 'analytics']);
  }

  // Helpers for UI mapping
  getBadgeClass(title: string): string {
    const t = title.toLowerCase();
    if (t.includes('exam') || t.includes('important')) return 'badge-important';
    if (t.includes('maintenance') || t.includes('info')) return 'badge-info';
    if (t.includes('lecture') || t.includes('event')) return 'badge-event';
    return 'badge-admin'; // default
  }

  getBadgeLabel(title: string): string {
    const t = title.toLowerCase();
    if (t.includes('exam') || t.includes('important')) return 'Important';
    if (t.includes('maintenance') || t.includes('info')) return 'Info';
    if (t.includes('lecture') || t.includes('event')) return 'Event';
    return 'Admin'; // default
  }

  getSeenPercentage(seenCount: number): number {
    // mock target audience of 200
    const target = 200;
    return Math.min(Math.round((seenCount / target) * 100), 100);
  }

  getTimeAgo(dateString: string): string {
    const date = new Date(dateString);
    const now = new Date();
    const diffMs = now.getTime() - date.getTime();
    
    if (diffMs < 60000) return 'Just now';
    
    const diffHours = Math.floor(diffMs / (1000 * 60 * 60));
    if (diffHours < 1) {
      return Math.floor(diffMs / 60000) + ' mins ago';
    } else if (diffHours < 24) {
      return diffHours + ' hours ago';
    } else if (diffHours < 48) {
      return 'Yesterday';
    } else {
      return date.toLocaleDateString('en-US', { month: 'short', day: 'numeric' });
    }
  }
}
