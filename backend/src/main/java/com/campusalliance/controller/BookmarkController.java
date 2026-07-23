package com.campusalliance.controller;

import com.campusalliance.dto.BookmarkDto;
import com.campusalliance.service.BookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bookmarks")
@RequiredArgsConstructor
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @PostMapping
    public ResponseEntity<Map<String, Boolean>> toggleBookmark(
            @RequestBody Map<String, Object> request,
            Authentication auth) {
        String targetType = (String) request.get("targetType");
        Long targetId = Long.valueOf(request.get("targetId").toString());
        boolean bookmarked = bookmarkService.toggleBookmark(targetType, targetId, auth.getName());
        Map<String, Boolean> response = new HashMap<>();
        response.put("bookmarked", bookmarked);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<BookmarkDto>> getUserBookmarks(Authentication auth) {
        return ResponseEntity.ok(bookmarkService.getUserBookmarks(auth.getName()));
    }

    @GetMapping("/check")
    public ResponseEntity<Map<String, Boolean>> checkBookmark(
            @RequestParam String targetType,
            @RequestParam Long targetId,
            Authentication auth) {
        boolean bookmarked = bookmarkService.isBookmarked(targetType, targetId, auth.getName());
        Map<String, Boolean> response = new HashMap<>();
        response.put("bookmarked", bookmarked);
        return ResponseEntity.ok(response);
    }
}
