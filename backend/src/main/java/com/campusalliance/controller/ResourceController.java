package com.campusalliance.controller;

import com.campusalliance.dto.ResourceDto;
import com.campusalliance.dto.ResourceVersionDto;
import com.campusalliance.service.ResourceService;
import com.campusalliance.dto.ResourceRatingDto;
import com.campusalliance.entity.ResourceVersion;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/resources")
@RequiredArgsConstructor
public class ResourceController {

    private final ResourceService resourceService;

    // only faculty and admins can upload
    @PostMapping
    @PreAuthorize("hasAnyRole('FACULTY', 'ADMIN')")
    public ResponseEntity<ResourceDto> createResource(
            @RequestParam String title,
            @RequestParam(required = false) String description,
            @RequestParam String courseName,
            @RequestParam("file") MultipartFile file,
            Authentication auth) throws IOException {

        ResourceDto dto = resourceService.createResource(
                title, description, courseName, file, auth.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    // upload a new version of an existing resource
    @PostMapping("/{id}/versions")
    @PreAuthorize("hasAnyRole('FACULTY', 'ADMIN')")
    public ResponseEntity<ResourceVersionDto> uploadVersion(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file,
            Authentication auth) throws IOException {

        ResourceVersionDto dto = resourceService.uploadNewVersion(id, file, auth.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    // list or search — any authenticated user
    @GetMapping
    public ResponseEntity<List<ResourceDto>> getResources(
            @RequestParam(required = false) String keyword) {

        List<ResourceDto> resources = (keyword != null && !keyword.isBlank())
                ? resourceService.searchResources(keyword)
                : resourceService.getAllResources();
        return ResponseEntity.ok(resources);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResourceDto> getResource(@PathVariable Long id) {
        return ResponseEntity.ok(resourceService.getResourceById(id));
    }

    @GetMapping("/{id}/versions")
    public ResponseEntity<List<ResourceVersionDto>> getVersions(@PathVariable Long id) {
        return ResponseEntity.ok(resourceService.getVersions(id));
    }

    // download latest version, or a specific version
    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> download(
            @PathVariable Long id,
            @RequestParam(required = false) Long versionId) {

        ResourceVersion version = resourceService.getFileForDownload(id, versionId);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(version.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + version.getFileName() + "\"")
                .body(version.getFileData());
    }

    // soft delete — only admins
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteResource(@PathVariable Long id) {
        resourceService.deleteResource(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/rate")
    public ResponseEntity<Void> rateResource(
            @PathVariable Long id,
            @RequestParam int rating,
            Authentication auth) {
        resourceService.rateResource(id, rating, auth.getName());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/ratings")
    public ResponseEntity<List<ResourceRatingDto>> getResourceRatings(@PathVariable Long id) {
        return ResponseEntity.ok(resourceService.getResourceRatings(id));
    }
}
