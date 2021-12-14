package io.microhooks.eda.providers;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class BrokerNotSupportedException extends RuntimeException {
    private final String brokerType;
}
