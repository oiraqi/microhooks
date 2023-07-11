package io.microhooks.core.internal;


public class BrokerNotSupportedException extends RuntimeException {

    public BrokerNotSupportedException(String brokerType) {
        super(brokerType);
    }
}
