package com.jg.gopractical.api.dto;

import lombok.Data;

@Data
public class ApiIpPool {

    private final String description;
    private final String lowerBound;
    private final String upperBound;
}
