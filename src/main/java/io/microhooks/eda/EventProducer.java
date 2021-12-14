package io.microhooks.eda;

public abstract class EventProducer<T, U> {

    public void publish(T key, U payload, String label, String[] streams) {        
        Event<T, U> event = new Event<>(key, payload, label);
        for (int i = 0; i < streams.length; i++) {
            publish(key, event, "${appName}#" + streams[i]);
        }
    }

    public void publish(U payload, String label, String[] streams) {
        publish(null, payload, label, streams);
    }

    public void publish(T key, U payload, String[] streams) {
        publish(key, payload, null, streams);
    }

    public void publish(U payload, String[] streams) {
        publish(null, payload, null, streams);
    }

    protected abstract void publish(T key, Event<T, U> event, String stream);

}
