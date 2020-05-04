package com.jg.gopractical.service;

import com.jg.gopractical.domain.model.IpAddress;

import java.util.List;
import java.util.UUID;

public interface IpAddressService {

    IpAddress getIpAddressById(final UUID ipAddressId);

    List<IpAddress> reserveDynamicIpAddress(final Integer quantity);

    IpAddress reserveIpAddress(final UUID poolId, final IpAddress ipAddress);

    IpAddress blacklistIpAddress(final UUID poolId, final String ipAddress);

    void freeIpAddress(final UUID poolId, final String ipAddress);

    IpAddress getIpAddressByValue(String ipAddress);
}
