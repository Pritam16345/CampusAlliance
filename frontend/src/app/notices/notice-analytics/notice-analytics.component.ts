import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { NoticeService, NoticeDto } from '../notice.service';

@Component({
  selector: 'app-notice-analytics',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './notice-analytics.component.html',
  styleUrls: ['./notice-analytics.component.css']
})
export class NoticeAnalyticsComponent implements OnInit {
  notice: NoticeDto | null = null;

  constructor(
    private route: ActivatedRoute,
    private noticeService: NoticeService
  ) {}

  ngOnInit() {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.noticeService.getNotice(+idParam).subscribe({
        next: (data) => this.notice = data,
        error: (err) => console.error(err)
      });
    }
  }

  get seenPercentage(): number {
    if (!this.notice) return 0;
    const estimatedAudience = 500;
    return Math.min(Math.round((this.notice.seenCount / estimatedAudience) * 100), 100);
  }
  
  get unseenCount(): number {
    if (!this.notice) return 0;
    const estimatedAudience = 500;
    return Math.max(0, estimatedAudience - this.notice.seenCount);
  }

  // To draw the pure CSS pie chart
  get pieChartStyle() {
    const p = this.seenPercentage;
    return {
      'background': `conic-gradient(#0f172a ${p}%, #e2e8f0 ${p}% 100%)`
    };
  }
}
