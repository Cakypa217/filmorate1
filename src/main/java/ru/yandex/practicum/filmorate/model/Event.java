package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.OperationType;

@Data
@NoArgsConstructor
public class Event {
    private long eventId;
    private long timestamp;
    private long userId;
    private EventType eventType;
    private OperationType operation;
    private long entityId;

    public Event(long timestamp, long userId, EventType eventType, OperationType operation, long entityId) {
        this.timestamp = timestamp;
        this.userId = userId;
        this.eventType = eventType;
        this.operation = operation;
        this.entityId = entityId;
    }
}
