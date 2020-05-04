package com.jg.gopractical.service;

import com.jg.gopractical.domain.model.IpAddress;

import java.util.List;
import java.util.UUID;

public interface IpAddressService {

    IpAddress getIpAddressById(final UUID ipAddressId);

    List<IpAddress> getIpAddresses();

    IpAddress reserveIpAddress(final IpAddress ipAddress);

    IpAddress blacklistIpAddress(final UUID ipAddressId);

    IpAddress blacklistIpAddress(final IpAddress ipAddress);

    IpAddress freeIpAddress(final UUID ipAddressId);

}
