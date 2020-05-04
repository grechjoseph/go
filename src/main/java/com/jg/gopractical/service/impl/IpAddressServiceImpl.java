package com.jg.gopractical.service.impl;

import com.jg.gopractical.domain.enums.IpState;
import com.jg.gopractical.domain.model.IpAddress;
import com.jg.gopractical.domain.repository.IpAddressRepository;
import com.jg.gopractical.service.IpAddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class IpAddressServiceImpl implements IpAddressService {

    private final IpAddressRepository ipAddressRepository;

    @Override
    public IpAddress getIpAddressById(final UUID ipAddressId) {
        return ipAddressRepository.findById(ipAddressId).orElseThrow(() -> new RuntimeException("IP Address not found."));
    }

    @Override
    public List<IpAddress> getIpAddresses() {
        return ipAddressRepository.findAll();
    }

    @Override
    public IpAddress reserveIpAddress(final IpAddress ipAddress) {

        return null;
    }

    @Override
    public IpAddress blacklistIpAddress(final UUID ipAddressId) {
        return null;
    }

    @Override
    public IpAddress blacklistIpAddress(final IpAddress ipAddress) {
        return null;
    }

    @Override
    public IpAddress freeIpAddress(final UUID ipAddressId) {
        return null;
    }

    private IpAddress transitState(final String ipAddress, final IpState newState) {
        final IpAddress ipAddress = getIpAddressById(ipAddressId);
        validateStateTransition(ipAddress, newState);
        ipAddress.setResourceState(newState);
        return ipAddressRepository.save(ipAddress);
    }

    private void validateStateTransition(final IpAddress ipAddress, final IpState newState) {
        if(!ipAddress.getResourceState().getNextSteps().contains(newState)) {
            throw new IllegalArgumentException("Invalid State transition.");
        }
    }

}
