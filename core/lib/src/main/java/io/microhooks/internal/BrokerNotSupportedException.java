package io.microhooks.internal;


public class BrokerNotSupportedException extends RuntimeException {

    public BrokerNotSupportedException(String brokerType) {
        super(brokerType);
    }
}
