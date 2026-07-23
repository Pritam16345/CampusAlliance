package com.campusalliance.repository;

import com.campusalliance.entity.NoticeComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoticeCommentRepository extends JpaRepository<NoticeComment, Long> {
    List<NoticeComment> findByNoticeIdOrderByCreatedAtAsc(Long noticeId);
    long countByNoticeId(Long noticeId);
}
