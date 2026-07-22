package com.campusalliance.repository;

import com.campusalliance.entity.ResourceVersion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ResourceVersionRepository extends JpaRepository<ResourceVersion, Long> {

    List<ResourceVersion> findByResourceIdOrderByVersionNumberAsc(Long resourceId);

    // gives us the latest version so we can increment for the next upload
    Optional<ResourceVersion> findTopByResourceIdOrderByVersionNumberDesc(Long resourceId);
}
