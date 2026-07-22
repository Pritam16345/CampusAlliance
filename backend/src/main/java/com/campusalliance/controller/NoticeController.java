package com.campusalliance.controller;

import com.campusalliance.dto.NoticeDto;
import com.campusalliance.dto.NoticeRequest;
import com.campusalliance.service.NoticeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notices")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @PostMapping
    @PreAuthorize("hasAnyRole('FACULTY', 'ADMIN')")
    public ResponseEntity<NoticeDto> create(@Valid @RequestBody NoticeRequest request,
                                            Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(noticeService.createNotice(request, auth.getName()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('FACULTY', 'ADMIN')")
    public ResponseEntity<NoticeDto> update(@PathVariable Long id,
                                            @Valid @RequestBody NoticeRequest request) {
        return ResponseEntity.ok(noticeService.updateNotice(id, request));
    }

    @GetMapping
    public ResponseEntity<List<NoticeDto>> getAll() {
        return ResponseEntity.ok(noticeService.getAllNotices());
    }

    @GetMapping("/{id}")
    public ResponseEntity<NoticeDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(noticeService.getNoticeById(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        noticeService.deleteNotice(id);
        return ResponseEntity.noContent().build();
    }

    // any logged-in user can mark a notice as seen
    @PostMapping("/{id}/seen")
    public ResponseEntity<Void> markAsSeen(@PathVariable Long id, Authentication auth) {
        noticeService.markAsSeen(id, auth.getName());
        return ResponseEntity.ok().build();
    }
}
