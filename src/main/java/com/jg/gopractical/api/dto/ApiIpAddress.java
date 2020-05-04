package com.jg.gopractical.api.dto;

import com.jg.gopractical.domain.enums.IpState;
import lombok.Data;

import java.util.UUID;

@Data
public class ApiIpAddress {

    private final UUID id;
    private final UUID ipPoolId;
    private final String value;
    private final IpState resourceState;
    private Boolean dynamic;
    private Integer quantity;

}
