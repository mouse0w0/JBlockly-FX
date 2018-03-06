package team.unstudio.jblockly.event;

import javafx.beans.NamedArg;
import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;

public class BlockEvent extends Event{
	
    public static final EventType<BlockEvent> ANY =
            new EventType<BlockEvent> (Event.ANY, "BLOCK");

    public BlockEvent(final @NamedArg("eventType") EventType<? extends BlockEvent> eventType) {
        super(eventType);
    }

    public BlockEvent(final @NamedArg("source") Object source,
                      final @NamedArg("target") EventTarget target,
                      final @NamedArg("eventType") EventType<? extends BlockEvent> eventType) {
        super(source, target, eventType);
    }

    @Override
    public EventType<? extends BlockEvent> getEventType() {
        return (EventType<? extends BlockEvent>) super.getEventType();
    }

}
