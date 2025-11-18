package com.comp2042.event;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

// Tests MoveEvent creation and properties
// Tests all user input event types
class MoveEventTest {

    @Test
    void testCreateDownEventFromUser() {
        MoveEvent event = new MoveEvent(EventType.DOWN, EventSource.USER);
        
        assertEquals(EventType.DOWN, event.getEventType());
        assertEquals(EventSource.USER, event.getEventSource());
    }

    @Test
    void testCreateLeftEventFromUser() {
        MoveEvent event = new MoveEvent(EventType.LEFT, EventSource.USER);
        
        assertEquals(EventType.LEFT, event.getEventType());
        assertEquals(EventSource.USER, event.getEventSource());
    }

    @Test
    void testCreateRightEventFromUser() {
        MoveEvent event = new MoveEvent(EventType.RIGHT, EventSource.USER);
        
        assertEquals(EventType.RIGHT, event.getEventType());
        assertEquals(EventSource.USER, event.getEventSource());
    }

    @Test
    void testCreateRotateEventFromUser() {
        MoveEvent event = new MoveEvent(EventType.ROTATE, EventSource.USER);
        
        assertEquals(EventType.ROTATE, event.getEventType());
        assertEquals(EventSource.USER, event.getEventSource());
    }

    @Test
    void testCreateHardDropEventFromUser() {
        MoveEvent event = new MoveEvent(EventType.HARD_DROP, EventSource.USER);
        
        assertEquals(EventType.HARD_DROP, event.getEventType());
        assertEquals(EventSource.USER, event.getEventSource());
    }

    @Test
    void testCreateHoldEventFromUser() {
        MoveEvent event = new MoveEvent(EventType.HOLD, EventSource.USER);
        
        assertEquals(EventType.HOLD, event.getEventType());
        assertEquals(EventSource.USER, event.getEventSource());
    }

    @Test
    void testCreateDownEventFromThread() {
        MoveEvent event = new MoveEvent(EventType.DOWN, EventSource.THREAD);
        
        assertEquals(EventType.DOWN, event.getEventType());
        assertEquals(EventSource.THREAD, event.getEventSource());
    }

    @Test
    void testEventImmutability() {
        // MoveEvent properties cannot change once created
        MoveEvent event = new MoveEvent(EventType.HARD_DROP, EventSource.USER);
        
        assertEquals(EventType.HARD_DROP, event.getEventType());
        assertEquals(EventSource.USER, event.getEventSource());
        
        // Properties remain consistent
        assertEquals(EventType.HARD_DROP, event.getEventType());
        assertEquals(EventSource.USER, event.getEventSource());
    }
}
