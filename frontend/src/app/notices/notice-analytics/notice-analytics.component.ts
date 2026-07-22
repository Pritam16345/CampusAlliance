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
  targetAudience = 200;
  
  // Mock student list for the UI
  mockStudents = [
    { name: 'Alex Mercer', id: 'CS-2021-045', seenAt: 'Oct 15, 10:23 AM', status: 'seen' },
    { name: 'Beatriz Costa', id: 'CS-2021-089', seenAt: 'Oct 15, 09:45 AM', status: 'seen' },
    { name: 'Chloe Davis', id: 'CS-2021-112', seenAt: 'Oct 14, 16:12 PM', status: 'seen' },
    { name: 'Daniel Wright', id: 'CS-2021-156', seenAt: 'Oct 14, 15:30 PM', status: 'seen' },
    { name: 'Elijah Thorne', id: 'CS-2021-177', seenAt: 'Oct 14, 11:05 AM', status: 'seen' }
  ];

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
    return Math.min(Math.round((this.notice.seenCount / this.targetAudience) * 100), 100);
  }
  
  get unseenCount(): number {
    if (!this.notice) return this.targetAudience;
    return Math.max(0, this.targetAudience - this.notice.seenCount);
  }

  // To draw the pure CSS pie chart
  get pieChartStyle() {
    const p = this.seenPercentage;
    return {
      'background': `conic-gradient(#0f172a ${p}%, #e2e8f0 ${p}% 100%)`
    };
  }
}
