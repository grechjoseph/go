package com.jg.gopractical.api.dto;

import com.jg.gopractical.domain.enums.IpState;
import lombok.Getter;

import java.util.UUID;

@Getter
public class ApiIpAddressExtended extends ApiIpAddress {

    private final UUID id;

    public ApiIpAddressExtended(final UUID id,
                                final UUID ipPoolId,
                                final String value,
                                final IpState ipState,
                                final boolean dynamic) {
        super(ipPoolId, value, ipState, dynamic);
        this.id = id;
    }
}
