package com.campusalliance.service;

import com.campusalliance.dto.NoticeCommentDto;
import com.campusalliance.entity.Notice;
import com.campusalliance.entity.NoticeComment;
import com.campusalliance.entity.User;
import com.campusalliance.exception.ResourceNotFoundException;
import com.campusalliance.repository.NoticeCommentRepository;
import com.campusalliance.repository.NoticeRepository;
import com.campusalliance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticeCommentService {

    private final NoticeCommentRepository noticeCommentRepository;
    private final NoticeRepository noticeRepository;
    private final UserRepository userRepository;

    @Transactional
    public NoticeCommentDto addComment(Long noticeId, String content, String userEmail) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new ResourceNotFoundException("Notice", noticeId));
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        NoticeComment comment = NoticeComment.builder()
                .notice(notice)
                .user(user)
                .content(content)
                .build();
        noticeCommentRepository.save(comment);

        return toDto(comment);
    }

    public List<NoticeCommentDto> getComments(Long noticeId) {
        if (!noticeRepository.existsById(noticeId)) {
            throw new ResourceNotFoundException("Notice", noticeId);
        }
        return noticeCommentRepository.findByNoticeIdOrderByCreatedAtAsc(noticeId)
                .stream().map(this::toDto).toList();
    }

    @Transactional
    public void deleteComment(Long commentId) {
        if (!noticeCommentRepository.existsById(commentId)) {
            throw new ResourceNotFoundException("NoticeComment", commentId);
        }
        noticeCommentRepository.deleteById(commentId);
    }

    private NoticeCommentDto toDto(NoticeComment c) {
        return NoticeCommentDto.builder()
                .id(c.getId())
                .noticeId(c.getNotice().getId())
                .userId(c.getUser().getId())
                .userName(c.getUser().getFullName())
                .userRole(c.getUser().getRole().name())
                .content(c.getContent())
                .createdAt(c.getCreatedAt())
                .build();
    }
}
