package com.campusalliance.repository;

import com.campusalliance.entity.ResourceRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ResourceRatingRepository extends JpaRepository<ResourceRating, Long> {
    Optional<ResourceRating> findByResourceIdAndUserId(Long resourceId, Long userId);
    List<ResourceRating> findByResourceId(Long resourceId);
    
    @Query("SELECT AVG(r.rating) FROM ResourceRating r WHERE r.resource.id = :resourceId")
    Double getAverageRating(@Param("resourceId") Long resourceId);
    
    long countByResourceId(Long resourceId);
}
