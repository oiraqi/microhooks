package io.microhooks.core.internal;

import io.microhooks.core.BrokerType;

public class BrokerNotSupportedException extends RuntimeException {

    public BrokerNotSupportedException(BrokerType brokerType) {
        super(brokerType.name());
    }
}
