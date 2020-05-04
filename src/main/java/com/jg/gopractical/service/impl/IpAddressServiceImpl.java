package com.jg.gopractical.service.impl;

import com.jg.gopractical.domain.enums.IpState;
import com.jg.gopractical.domain.model.IpAddress;
import com.jg.gopractical.domain.model.IpPool;
import com.jg.gopractical.domain.repository.IpAddressRepository;
import com.jg.gopractical.domain.repository.IpPoolRepository;
import com.jg.gopractical.service.IpAddressService;
import com.jg.gopractical.utils.IPUtils;
import inet.ipaddr.ipv4.IPv4Address;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.jg.gopractical.domain.enums.IpState.BLACKLISTED;
import static com.jg.gopractical.domain.enums.IpState.RESERVED;

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
    public List<IpAddress> reserveDynamicIpAddress(final Integer quantity) {
        final IpPool dynamicIpPool = ipPoolRepository.findBySupportDynamicIsTrue().orElseThrow(() -> new RuntimeException("Pool not found."));

        if(dynamicIpPool.getAvailableCapacity() < quantity) {
            throw new IllegalArgumentException("Insufficient addresses available.");
        }

        final List<IpAddress> generatedAddresses = new ArrayList<>();
        Integer last = IPUtils.getIpNumbers(dynamicIpPool.getLowerBound());

        for (int i = 0; i < quantity; i++) {
            final int upper = IPUtils.getIpNumbers(dynamicIpPool.getUpperBound());

            for(int j = last; j <= upper; j++) {
                final IPv4Address candidateAddress = new IPv4Address(j);

                final int finalJ = j;
                if (ipAddressRepository.findByValue(candidateAddress.toString()).isEmpty()
                        && generatedAddresses.stream().noneMatch(ip -> IPUtils.getIpNumbers(ip.getValue()) == finalJ)) {
                    final IpAddress ipAddress = new IpAddress();
                    ipAddress.setResourceState(RESERVED);
                    ipAddress.setIpPool(dynamicIpPool);
                    ipAddress.setValue(candidateAddress.toString());
                    generatedAddresses.add(ipAddress);
                    last = j;
                    break;
                }
            }
        }

        return ipAddressRepository.saveAll(generatedAddresses);
    }

    @Override
    public IpAddress reserveIpAddress(final UUID poolId, final IpAddress ipAddress) {
        ipAddress.setResourceState(RESERVED);
        final IpPool ipPool = ipPoolRepository.findById(poolId).orElseThrow(() -> new RuntimeException("Pool not found."));

        final IPv4Address iPv4Address = IPUtils.fromString(ipAddress.getValue());
        final IPv4Address lowerBound = IPUtils.fromString(ipPool.getLowerBound());
        final IPv4Address upperBound = IPUtils.fromString(ipPool.getUpperBound());

        if (!IPUtils.isInRange(iPv4Address, lowerBound, upperBound)) {
            throw new IllegalArgumentException("IP provided is not in range.");
        }

        if (ipAddressRepository.findByValue(ipAddress.getValue()).isPresent()) {
            throw new IllegalArgumentException("IP is not available");
        }

        ipAddress.setIpPool(ipPool);
        return ipAddressRepository.save(ipAddress);
    }

    @Override
    public IpAddress blacklistIpAddress(final IpAddress ipAddress) {
        return transitState(ipAddressRepository.save(ipAddress), BLACKLISTED);
    }

    @Override
    public void freeIpAddress(final UUID ipAddressId) {
        final IpAddress ipAddress = getIpAddressById(ipAddressId);
        ipAddressRepository.delete(ipAddress);
    }

    @Override
    public IpAddress getIpAddressByValue(final String ipAddress) {
        return ipAddressRepository.findByValue(ipAddress).orElseThrow(() -> new IllegalArgumentException("IP not found."));
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
