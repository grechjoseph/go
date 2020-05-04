package com.jg.gopractical.service.impl;

import com.jg.gopractical.domain.enums.IpState;
import com.jg.gopractical.domain.model.IpAddress;
import com.jg.gopractical.domain.repository.IpAddressRepository;
import com.jg.gopractical.domain.repository.IpPoolRepository;
import com.jg.gopractical.service.IpAddressService;
import com.jg.gopractical.service.IpPoolService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static com.jg.gopractical.domain.enums.IpState.*;

@Service
@RequiredArgsConstructor
public class IpAddressServiceImpl implements IpAddressService {

    private final IpAddressRepository ipAddressRepository;
    private final IpPoolRepository ipPoolRepository;

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
        if(ipAddress.isDynamic()) {
            ipAddress.setIpPool(ipPoolRepository.findById());
        }


        return ipAddressRepository.save(ipAddress);
    }

    @Override
    public IpAddress blacklistIpAddress(final UUID ipAddressId) {
        final IpAddress ipAddress = getIpAddressById(ipAddressId);
        return transitState(ipAddress, BLACKLISTED);
    }

    @Override
    public IpAddress blacklistIpAddress(final IpAddress ipAddress) {
        return transitState(ipAddress, BLACKLISTED);
    }

    @Override
    public void freeIpAddress(final UUID ipAddressId) {
        final IpAddress ipAddress = getIpAddressById(ipAddressId);
        ipAddressRepository.delete(ipAddress);
    }

    private IpAddress transitState(final IpAddress ipAddress, final IpState newState) {
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
