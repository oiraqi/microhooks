package io.microhooks.eda.providers;


public class BrokerNotSupportedException extends RuntimeException {

    public BrokerNotSupportedException(String brokerType) {
        super(brokerType);
    }
}
