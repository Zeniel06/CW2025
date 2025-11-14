package com.comp2042.event;

/**
 * Immutable data class representing a movement or action event in the game.
 * Encapsulates the type of event and its source.
 */
public final class MoveEvent {
    private final EventType eventType;
    private final EventSource eventSource;

    /**
     * Constructs a new MoveEvent with the specified type and source.
     * 
     * @param eventType the type of event or action
     * @param eventSource the source of the event (user or thread)
     */
    public MoveEvent(EventType eventType, EventSource eventSource) {
        this.eventType = eventType;
        this.eventSource = eventSource;
    }

    /**
     * Gets the type of the event.
     * 
     * @return the event type
     */
    public EventType getEventType() {
        return eventType;
    }

    /**
     * Gets the source of the event.
     * 
     * @return the event source
     */
    public EventSource getEventSource() {
        return eventSource;
    }
}

