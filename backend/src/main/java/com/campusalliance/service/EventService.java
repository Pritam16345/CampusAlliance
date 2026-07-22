package com.campusalliance.service;

import com.campusalliance.dto.EventDto;
import com.campusalliance.dto.EventRequest;
import com.campusalliance.entity.Event;
import com.campusalliance.entity.User;
import com.campusalliance.exception.ResourceNotFoundException;
import com.campusalliance.repository.EventRepository;
import com.campusalliance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Transactional
    public EventDto createEvent(EventRequest request, String organizerEmail) {
        User organizer = findUser(organizerEmail);

        Event event = Event.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .location(request.getLocation())
                .eventDate(request.getEventDate())
                .organizer(organizer)
                .build();
        eventRepository.save(event);
        return toDto(event);
    }

    @Transactional
    public EventDto updateEvent(Long id, EventRequest request) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event", id));

        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setLocation(request.getLocation());
        event.setEventDate(request.getEventDate());
        event.setVersion(request.getVersion());

        eventRepository.save(event);
        return toDto(event);
    }

    public List<EventDto> getAllEvents() {
        return eventRepository.findAllByOrderByEventDateAsc()
                .stream().map(this::toDto).toList();
    }

    public EventDto getEventById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event", id));
        return toDto(event);
    }

    @Transactional
    public void deleteEvent(Long id) {
        if (!eventRepository.existsById(id)) {
            throw new ResourceNotFoundException("Event", id);
        }
        eventRepository.deleteById(id);
    }

    private User findUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
    }

    private EventDto toDto(Event e) {
        return EventDto.builder()
                .id(e.getId())
                .title(e.getTitle())
                .description(e.getDescription())
                .location(e.getLocation())
                .eventDate(e.getEventDate())
                .organizerName(e.getOrganizer().getFullName())
                .version(e.getVersion())
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .build();
    }
}
