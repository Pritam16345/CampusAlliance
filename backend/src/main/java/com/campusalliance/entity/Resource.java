package com.campusalliance.entity;

import jakarta.persistence.*;
import lombok.*;


import java.util.ArrayList;
import java.util.List;

/**
 * A logical academic resource (lecture notes, assignment, lab manual, etc.).
 *
 * The Resource is what users search for and see in the UI. The actual
 * uploaded files live in ResourceVersion — re-uploading creates a new
 * version instead of overwriting. Think of it like a Google Doc where
 * each upload creates a new revision.
 */
@Entity
@Table(name = "resources")

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Resource extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String description;

    /**
     * Free-form course tag like "CS301". Not a separate entity because
     * we don't need to manage courses as first-class objects in this scope.
     */
    @Column(nullable = false)
    private String courseName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by", nullable = false)
    private User uploadedBy;

    /**
     * All versions of this resource, newest last.
     * CascadeType.ALL so saving a Resource cascades to its versions.
     * orphanRemoval cleans up version rows if they're removed from the list.
     */
    @OneToMany(mappedBy = "resource", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("versionNumber ASC")
    @Builder.Default
    private List<ResourceVersion> versions = new ArrayList<>();
}
