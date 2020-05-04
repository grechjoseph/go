package com.jg.gopractical.api.dto;

import lombok.Getter;

import java.util.UUID;

@Getter
public class ApiIpPoolExtended extends ApiIpPool {

    private final UUID id;
    private final Integer totalCapacity;
    private final Integer usedCapacity;


    public ApiIpPoolExtended(final UUID id,
                             final Integer totalCapacity,
                             final Integer usedCapacity,
                             final String description,
                             final String lowerBound,
                             final String upperBound) {
        super(description, lowerBound, upperBound);
        this.id = id;
        this.totalCapacity = totalCapacity;
        this.usedCapacity = usedCapacity;
    }
}
