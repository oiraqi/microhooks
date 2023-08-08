package io.microhooks.internal;

import java.util.Random;
import lombok.Getter;

import io.microhooks.common.Event;

public class TransactionalEvent<P> extends Event<P> {

    @Getter
    private long txId;

    private Event<P> event;

    public TransactionalEvent(Event<P> event) {
        this.event = event;
        txId = new Random().nextLong();
    }

    @Override
    public long getTimestamp() {
        return event.getTimestamp();
    }

    @Override
    public P getPayload() {
        return event.getPayload();
    }

    @Override
    public String getLabel() {
        return event.getLabel();
    }
}
