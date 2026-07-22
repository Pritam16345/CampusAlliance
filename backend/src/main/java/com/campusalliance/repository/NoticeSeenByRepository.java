package com.campusalliance.repository;

import com.campusalliance.entity.NoticeSeenBy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeSeenByRepository extends JpaRepository<NoticeSeenBy, Long> {

    boolean existsByNoticeIdAndUserId(Long noticeId, Long userId);

    long countByNoticeId(Long noticeId);
}
