package com.campusalliance.service;

import com.campusalliance.dto.ResourceDto;
import com.campusalliance.dto.ResourceVersionDto;
import com.campusalliance.entity.Resource;
import com.campusalliance.entity.ResourceVersion;
import com.campusalliance.entity.User;
import com.campusalliance.exception.ResourceNotFoundException;
import com.campusalliance.repository.ResourceRepository;
import com.campusalliance.repository.ResourceVersionRepository;
import com.campusalliance.repository.UserRepository;
import com.campusalliance.repository.ResourceRatingRepository;
import com.campusalliance.entity.ResourceRating;
import com.campusalliance.dto.ResourceRatingDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ResourceService {

    private final ResourceRepository resourceRepository;
    private final ResourceVersionRepository versionRepository;
    private final UserRepository userRepository;
    private final ResourceRatingRepository resourceRatingRepository;
    private final AuditLogService auditLogService;

    /**
     * Create a new resource with its first file version.
     */
    @Transactional
    public ResourceDto createResource(String title, String description,
                                      String courseName, MultipartFile file,
                                      String uploaderEmail) throws IOException {
        User uploader = findUserByEmail(uploaderEmail);

        Resource resource = Resource.builder()
                .title(title)
                .description(description)
                .courseName(courseName)
                .uploadedBy(uploader)
                .build();
        resourceRepository.save(resource);

        // create version 1
        createVersion(resource, file, uploader, 1);

        auditLogService.log("RESOURCE_UPLOADED", uploaderEmail, "Title: " + title);

        return toDto(resource);
    }

    /**
     * Upload a new version of an existing resource.
     * Bumps version number, keeps old versions intact.
     */
    @Transactional
    public ResourceVersionDto uploadNewVersion(Long resourceId, MultipartFile file,
                                               String uploaderEmail) throws IOException {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource", resourceId));
        User uploader = findUserByEmail(uploaderEmail);

        // figure out the next version number
        int nextVersion = versionRepository.findTopByResourceIdOrderByVersionNumberDesc(resourceId)
                .map(v -> v.getVersionNumber() + 1)
                .orElse(1);

        ResourceVersion version = createVersion(resource, file, uploader, nextVersion);
        return toVersionDto(version);
    }

    public List<ResourceDto> getAllResources() {
        // @SQLRestriction automatically filters out deleted resources
        return resourceRepository.findAll().stream().map(this::toDto).toList();
    }

    public List<ResourceDto> searchResources(String keyword) {
        return resourceRepository.searchByKeyword(keyword).stream().map(this::toDto).toList();
    }

    public ResourceDto getResourceById(Long id) {
        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource", id));
        return toDto(resource);
    }

    public List<ResourceVersionDto> getVersions(Long resourceId) {
        // make sure the resource exists first
        if (!resourceRepository.existsById(resourceId)) {
            throw new ResourceNotFoundException("Resource", resourceId);
        }
        return versionRepository.findByResourceIdOrderByVersionNumberAsc(resourceId)
                .stream().map(this::toVersionDto).toList();
    }

    /**
     * Returns the actual file bytes for download.
     * If versionId is null, returns the latest version.
     */
    @Transactional(readOnly = true)
    public ResourceVersion getFileForDownload(Long resourceId, Long versionId) {
        if (versionId != null) {
            return versionRepository.findById(versionId)
                    .orElseThrow(() -> new ResourceNotFoundException("ResourceVersion", versionId));
        }
        // latest version
        return versionRepository.findTopByResourceIdOrderByVersionNumberDesc(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource", resourceId));
    }

    @Transactional
    public void deleteResource(Long id) {
        if (!resourceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Resource", id);
        }
        resourceRepository.deleteById(id);
    }

    @Transactional
    public void rateResource(Long resourceId, int rating, String userEmail) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource", resourceId));
        User user = findUserByEmail(userEmail);

        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        ResourceRating resourceRating = resourceRatingRepository.findByResourceIdAndUserId(resourceId, user.getId())
                .orElse(ResourceRating.builder()
                        .resource(resource)
                        .user(user)
                        .build());

        resourceRating.setRating(rating);
        resourceRatingRepository.save(resourceRating);
    }

    public List<ResourceRatingDto> getResourceRatings(Long resourceId) {
        if (!resourceRepository.existsById(resourceId)) {
            throw new ResourceNotFoundException("Resource", resourceId);
        }
        return resourceRatingRepository.findByResourceId(resourceId).stream().map(r -> ResourceRatingDto.builder()
                .id(r.getId())
                .resourceId(r.getResource().getId())
                .userId(r.getUser().getId())
                .userName(r.getUser().getFullName())
                .rating(r.getRating())
                .createdAt(r.getCreatedAt())
                .build()).toList();
    }

    // --- helper methods ---

    private ResourceVersion createVersion(Resource resource, MultipartFile file,
                                          User uploader, int versionNumber) throws IOException {
        ResourceVersion version = ResourceVersion.builder()
                .resource(resource)
                .versionNumber(versionNumber)
                .fileName(file.getOriginalFilename())
                .contentType(file.getContentType())
                .fileSize(file.getSize())
                .fileData(file.getBytes())
                .uploadedBy(uploader)
                .build();
        return versionRepository.save(version);
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
    }

    private ResourceDto toDto(Resource r) {
        Double avgRating = resourceRatingRepository.getAverageRating(r.getId());
        Long count = resourceRatingRepository.countByResourceId(r.getId());
        return ResourceDto.builder()
                .id(r.getId())
                .title(r.getTitle())
                .description(r.getDescription())
                .courseName(r.getCourseName())
                .uploadedByName(r.getUploadedBy().getFullName())
                .totalVersions(r.getVersions().size())
                .createdAt(r.getCreatedAt())
                .updatedAt(r.getUpdatedAt())
                .averageRating(avgRating != null ? avgRating : 0.0)
                .ratingCount(count)
                .build();
    }

    private ResourceVersionDto toVersionDto(ResourceVersion v) {
        return ResourceVersionDto.builder()
                .id(v.getId())
                .versionNumber(v.getVersionNumber())
                .fileName(v.getFileName())
                .contentType(v.getContentType())
                .fileSize(v.getFileSize())
                .uploadedByName(v.getUploadedBy().getFullName())
                .createdAt(v.getCreatedAt())
                .build();
    }
}
