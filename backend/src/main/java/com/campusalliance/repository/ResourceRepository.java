package com.campusalliance.repository;

import com.campusalliance.entity.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ResourceRepository extends JpaRepository<Resource, Long> {

    // search title or course name, case insensitive
    @Query("SELECT r FROM Resource r WHERE " +
           "LOWER(r.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(r.courseName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Resource> searchByKeyword(@Param("keyword") String keyword);

    List<Resource> findByCourseName(String courseName);
}
