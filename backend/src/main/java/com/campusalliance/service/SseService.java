package com.campusalliance.service;

import com.campusalliance.dto.NoticeDto;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Manages SSE connections. Clients connect and get an SseEmitter.
 * When a notice is created, we push it to all connected clients.
 *
 * CopyOnWriteArrayList because emitters can be added/removed from
 * different threads (HTTP request threads + timeout callbacks).
 * It's fine for a small number of concurrent connections.
 */
@Service
public class SseService {

    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    public SseEmitter subscribe() {
        // 5 minute timeout — client will auto-reconnect via EventSource
        SseEmitter emitter = new SseEmitter(5 * 60 * 1000L);

        emitters.add(emitter);

        // clean up when connection ends
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError(e -> emitters.remove(emitter));

        return emitter;
    }

    /**
     * Push a new notice to all connected clients.
     * If sending to a specific client fails, we just remove it —
     * the client's EventSource will reconnect automatically.
     */
    public void pushNotice(NoticeDto notice) {
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event()
                        .name("new-notice")
                        .data(notice));
            } catch (IOException e) {
                emitters.remove(emitter);
            }
        }
    }
}
