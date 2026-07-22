package com.campusalliance.service;

import com.campusalliance.dto.NoticeDto;
import com.campusalliance.dto.NoticeRequest;
import com.campusalliance.entity.Notice;
import com.campusalliance.entity.NoticeSeenBy;
import com.campusalliance.entity.User;
import com.campusalliance.exception.ResourceNotFoundException;
import com.campusalliance.repository.NoticeRepository;
import com.campusalliance.repository.NoticeSeenByRepository;
import com.campusalliance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final NoticeSeenByRepository seenByRepository;
    private final UserRepository userRepository;
    private final SseService sseService;

    @Transactional
    public NoticeDto createNotice(NoticeRequest request, String posterEmail) {
        User poster = findUser(posterEmail);

        Notice notice = Notice.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .postedBy(poster)
                .build();
        noticeRepository.save(notice);
        NoticeDto dto = toDto(notice);

        // push to all connected SSE clients
        sseService.pushNotice(dto);

        return dto;
    }

    /**
     * Update a notice. The version from the request is set on the entity
     * so Hibernate can check it against the DB version. If they don't match,
     * it throws OptimisticLockException — our global handler returns 409.
     */
    @Transactional
    public NoticeDto updateNotice(Long id, NoticeRequest request) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notice", id));

        notice.setTitle(request.getTitle());
        notice.setContent(request.getContent());
        // set the version from the client so JPA can detect conflicts
        notice.setVersion(request.getVersion());

        noticeRepository.save(notice);
        return toDto(notice);
    }

    public List<NoticeDto> getAllNotices() {
        return noticeRepository.findAllByOrderByCreatedAtDesc()
                .stream().map(this::toDto).toList();
    }

    public NoticeDto getNoticeById(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notice", id));
        return toDto(notice);
    }

    @Transactional
    public void deleteNotice(Long id) {
        if (!noticeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Notice", id);
        }
        noticeRepository.deleteById(id);
    }

    /**
     * Mark a notice as seen by the current user.
     * If they already saw it, do nothing (idempotent).
     */
    @Transactional
    public void markAsSeen(Long noticeId, String userEmail) {
        if (!noticeRepository.existsById(noticeId)) {
            throw new ResourceNotFoundException("Notice", noticeId);
        }

        User user = findUser(userEmail);

        // already seen? skip
        if (seenByRepository.existsByNoticeIdAndUserId(noticeId, user.getId())) {
            return;
        }

        Notice notice = noticeRepository.findById(noticeId).orElseThrow();
        NoticeSeenBy seen = NoticeSeenBy.builder()
                .notice(notice)
                .user(user)
                .build();
        seenByRepository.save(seen);
    }

    private User findUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
    }

    private NoticeDto toDto(Notice n) {
        long seenCount = seenByRepository.countByNoticeId(n.getId());
        return NoticeDto.builder()
                .id(n.getId())
                .title(n.getTitle())
                .content(n.getContent())
                .postedByName(n.getPostedBy().getFullName())
                .version(n.getVersion())
                .seenCount(seenCount)
                .createdAt(n.getCreatedAt())
                .updatedAt(n.getUpdatedAt())
                .build();
    }
}
