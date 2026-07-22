package com.campusalliance.service;

import com.campusalliance.dto.ResourceDto;
import com.campusalliance.entity.Resource;
import com.campusalliance.entity.ResourceVersion;
import com.campusalliance.entity.User;
import com.campusalliance.exception.ResourceNotFoundException;
import com.campusalliance.repository.ResourceRepository;
import com.campusalliance.repository.ResourceVersionRepository;
import com.campusalliance.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResourceServiceTest {

    @Mock private ResourceRepository resourceRepository;
    @Mock private ResourceVersionRepository versionRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks
    private ResourceService resourceService;

    @Test
    void deleteResource_Success() {
        // Arrange
        Long resourceId = 1L;
        when(resourceRepository.existsById(resourceId)).thenReturn(true);

        // Act
        resourceService.deleteResource(resourceId);

        // Assert
        verify(resourceRepository, times(1)).deleteById(resourceId);
    }

    @Test
    void deleteResource_NotFound_ThrowsException() {
        // Arrange
        Long resourceId = 99L;
        when(resourceRepository.existsById(resourceId)).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, 
            () -> resourceService.deleteResource(resourceId));
            
        verify(resourceRepository, never()).deleteById(any());
    }

    @Test
    void getResourceById_Success() {
        // Arrange
        User uploader = User.builder().fullName("Dr. Smith").build();
        Resource resource = Resource.builder()
                .id(1L)
                .title("Syllabus")
                .uploadedBy(uploader)
                .build();
                
        // we need at least one version in the list since toDto calls r.getVersions().size()
        resource.getVersions().add(new ResourceVersion());
        
        when(resourceRepository.findById(1L)).thenReturn(Optional.of(resource));

        // Act
        ResourceDto dto = resourceService.getResourceById(1L);

        // Assert
        assertNotNull(dto);
        assertEquals("Syllabus", dto.getTitle());
        assertEquals("Dr. Smith", dto.getUploadedByName());
        assertEquals(1, dto.getTotalVersions());
    }
}
