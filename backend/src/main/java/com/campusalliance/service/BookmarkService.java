package com.campusalliance.service;

import com.campusalliance.dto.BookmarkDto;
import com.campusalliance.entity.Bookmark;
import com.campusalliance.entity.User;
import com.campusalliance.repository.BookmarkRepository;
import com.campusalliance.repository.NoticeRepository;
import com.campusalliance.repository.ResourceRepository;
import com.campusalliance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final UserRepository userRepository;
    private final NoticeRepository noticeRepository;
    private final ResourceRepository resourceRepository;

    @Transactional
    public boolean toggleBookmark(String targetType, Long targetId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Optional<Bookmark> existing = bookmarkRepository.findByUserIdAndTargetTypeAndTargetId(user.getId(), targetType, targetId);
        
        if (existing.isPresent()) {
            bookmarkRepository.delete(existing.get());
            return false;
        } else {
            Bookmark bookmark = Bookmark.builder()
                    .user(user)
                    .targetType(targetType)
                    .targetId(targetId)
                    .build();
            bookmarkRepository.save(bookmark);
            return true;
        }
    }

    public List<BookmarkDto> getUserBookmarks(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        
        return bookmarkRepository.findByUserIdOrderByCreatedAtDesc(user.getId()).stream().map(b -> {
            String title = "Unknown";
            if ("NOTICE".equals(b.getTargetType())) {
                title = noticeRepository.findById(b.getTargetId()).map(n -> n.getTitle()).orElse("Deleted Notice");
            } else if ("RESOURCE".equals(b.getTargetType())) {
                title = resourceRepository.findById(b.getTargetId()).map(r -> r.getTitle()).orElse("Deleted Resource");
            }
            return BookmarkDto.builder()
                    .id(b.getId())
                    .targetType(b.getTargetType())
                    .targetId(b.getTargetId())
                    .title(title)
                    .createdAt(b.getCreatedAt())
                    .build();
        }).toList();
    }

    public boolean isBookmarked(String targetType, Long targetId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return bookmarkRepository.existsByUserIdAndTargetTypeAndTargetId(user.getId(), targetType, targetId);
    }
}
