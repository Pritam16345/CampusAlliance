package com.campusalliance.repository;

import com.campusalliance.entity.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    Optional<Bookmark> findByUserIdAndTargetTypeAndTargetId(Long userId, String targetType, Long targetId);
    List<Bookmark> findByUserIdOrderByCreatedAtDesc(Long userId);
    boolean existsByUserIdAndTargetTypeAndTargetId(Long userId, String targetType, Long targetId);
    void deleteByUserIdAndTargetTypeAndTargetId(Long userId, String targetType, Long targetId);
}
