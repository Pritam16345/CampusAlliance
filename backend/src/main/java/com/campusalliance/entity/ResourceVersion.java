package com.campusalliance.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * One uploaded file version for a Resource.
 *
 * Each re-upload bumps versionNumber and creates a new row here,
 * leaving old versions intact for download. This gives users a
 * full revision history.
 *
 * File storage: we store the file as byte[] (@Lob) in the database.
 * For a production system with large files you'd use S3 or a filesystem
 * and store the path here, but for a demo this keeps the setup
 * self-contained — no external storage to configure.
 *
 * The @Lob with FetchType.LAZY means listing resources won't load
 * every file into memory. The byte[] is only fetched during download.
 */
@Entity
@Table(name = "resource_versions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResourceVersion extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resource_id", nullable = false)
    private Resource resource;

    @Column(nullable = false)
    private Integer versionNumber;

    @Column(nullable = false)
    private String fileName; // original filename like "lecture-week3.pdf"

    @Column(nullable = false)
    private String contentType; // MIME type for correct Content-Type header on download

    @Column(nullable = false)
    private Long fileSize; // in bytes — useful for showing "2.4 MB" in the UI

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(nullable = false)
    private byte[] fileData;

    // Who uploaded this specific version (might differ from original uploader)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by", nullable = false)
    private User uploadedBy;
}
