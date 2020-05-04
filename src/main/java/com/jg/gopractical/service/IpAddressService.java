package com.jg.gopractical.service;

import com.jg.gopractical.domain.model.IpAddress;

import java.util.List;
import java.util.UUID;

public interface IpAddressService {

    IpAddress getIpAddressById(final UUID ipAddressId);

    List<IpAddress> reserveDynamicIpAddress(final Integer quantity);

    IpAddress reserveIpAddress(final UUID poolId, final IpAddress ipAddress);

    IpAddress blacklistIpAddress(final IpAddress ipAddress);

    void freeIpAddress(final UUID ipAddressId);

    IpAddress getIpAddressByValue(String ipAddress);
}
