package com.campusalliance.controller;

import com.campusalliance.dto.NoticeCommentDto;
import com.campusalliance.service.NoticeCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notices/{noticeId}/comments")
@RequiredArgsConstructor
public class NoticeCommentController {

    private final NoticeCommentService noticeCommentService;

    @PostMapping
    public ResponseEntity<NoticeCommentDto> addComment(
            @PathVariable Long noticeId,
            @RequestBody Map<String, String> body,
            Authentication auth) {
        String content = body.get("content");
        NoticeCommentDto dto = noticeCommentService.addComment(noticeId, content, auth.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @GetMapping
    public ResponseEntity<List<NoticeCommentDto>> getComments(@PathVariable Long noticeId) {
        return ResponseEntity.ok(noticeCommentService.getComments(noticeId));
    }

    @DeleteMapping("/{commentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long noticeId,
            @PathVariable Long commentId) {
        noticeCommentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }
}
