package com.campusalliance.entity;

/**
 * The three roles in Campus Alliance.
 *
 * Enum instead of a DB table because these roles are fixed business rules
 * baked into @PreAuthorize checks. They won't change at runtime, so a
 * separate table would just add a join for no benefit.
 */
public enum Role {
    STUDENT,
    FACULTY,
    ADMIN
}
