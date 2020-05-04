package com.jg.gopractical.service.impl;

import com.jg.gopractical.domain.enums.IpState;
import com.jg.gopractical.domain.exception.BaseException;
import com.jg.gopractical.domain.model.IpAddress;
import com.jg.gopractical.domain.model.IpPool;
import com.jg.gopractical.domain.repository.IpAddressRepository;
import com.jg.gopractical.domain.repository.IpPoolRepository;
import com.jg.gopractical.service.IpAddressService;
import com.jg.gopractical.utils.IPUtils;
import inet.ipaddr.ipv4.IPv4Address;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.jg.gopractical.domain.enums.IpState.BLACKLISTED;
import static com.jg.gopractical.domain.enums.IpState.RESERVED;
import static com.jg.gopractical.domain.exception.ErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class IpAddressServiceImpl implements IpAddressService {

    private final IpAddressRepository ipAddressRepository;
    private final IpPoolRepository ipPoolRepository;

    @Override
    public IpAddress getIpAddressById(final UUID ipAddressId) {
        log.info("Retrieving IP Address by ID '{}'.", ipAddressId);
        return ipAddressRepository.findById(ipAddressId).orElseThrow(() -> new RuntimeException("IP Address not found."));
    }

    @Override
    public List<IpAddress> reserveDynamicIpAddress(final Integer quantity) {
        log.info("Reserving {} Dynamic IPs.", quantity);
        final IpPool dynamicIpPool = ipPoolRepository.findBySupportDynamicIsTrue().orElseThrow(() -> new RuntimeException("Pool not found."));

        if(dynamicIpPool.getAvailableCapacity() < quantity) {
            log.warn("Dynamic Pool has only {} remaining resources.", dynamicIpPool.getAvailableCapacity());
            throw new BaseException(IP_POOL_EXHAUSTED);
        }

        final List<IpAddress> generatedAddresses = new ArrayList<>();
        int last = IPUtils.getIpNumbers(dynamicIpPool.getLowerBound());

        log.debug("Generating Dynamic IPS...");
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
        log.info("Reserving IP '{}' from Pool with ID '{}'.", ipAddress.getValue(), poolId);
        ipAddress.setResourceState(RESERVED);
        final IpPool ipPool = ipPoolRepository.findById(poolId).orElseThrow(() -> new BaseException(IP_POOL_NOT_FOUND));

        if(ipPool.getAvailableCapacity() < 1) {
            log.warn("IP Pool with ID '{}' has no resources left.", poolId);
            throw new BaseException(IP_POOL_EXHAUSTED);
        }

        if(!ipAddressInPoolRange(ipPool, ipAddress.getValue())) {
            log.warn("IP '{}' is not in range of IP Pool with Lower Bound '{}', and Upper Bound '{}'.", ipAddress.getValue(), ipPool.getLowerBound(), ipPool.getUpperBound());
            throw new BaseException(IP_ADDRESS_NOT_IN_RANGE);
        }

        final Optional<IpAddress> ipAddressByValue = ipAddressRepository.findByValue(ipAddress.getValue());
        if (ipAddressByValue.isPresent()) {
            if(ipAddressByValue.get().getResourceState().equals(BLACKLISTED)){
                log.warn("IP Address is Blacklisted.");
                throw new BaseException(IP_ADDRESS_BLACKLISTED);
            }

            log.warn("IP Address is already Reserved.");
            throw new BaseException(IP_ADDRESS_NOT_AVAILABLE);
        }

        ipAddress.setIpPool(ipPool);
        log.debug("Persisting IP Address '{}' for IP Pool '{}'.", ipAddress.getValue(), poolId);
        return ipAddressRepository.save(ipAddress);
    }

    @Override
    public IpAddress blacklistIpAddress(final UUID poolId, final String ipAddress) {
        log.info("Blacklisting IP '{}' for IP Pool with ID '{}'.", ipAddress, poolId);
        Optional<IpAddress> optionalIpAddress = ipAddressRepository.findByValue(ipAddress);

        optionalIpAddress.ifPresent(foundIpAddress -> {
            log.warn("IP Address is not Free.");
            throw new BaseException(foundIpAddress.getResourceState().equals(RESERVED) ? IP_ADDRESS_IN_USE : IP_ADDRESS_BLACKLISTED);
        });

        final IpPool ipPool = ipPoolRepository.findById(poolId).orElseThrow(() -> new BaseException(IP_POOL_NOT_FOUND));

        if(!ipAddressInPoolRange(ipPool, ipAddress)) {
            log.warn("IP Address is not in Range ");
            throw new BaseException(IP_ADDRESS_NOT_IN_RANGE);
        }

        final IpAddress ipAddressToBlacklist = new IpAddress();
        ipAddressToBlacklist.setValue(ipAddress);
        ipAddressToBlacklist.setIpPool(ipPool);

        return transitState(ipAddressToBlacklist, BLACKLISTED);
    }

    @Override
    public void freeIpAddress(final UUID poolId, final String ipAddress) {
        final IpPool ipPool = ipPoolRepository.findById(poolId).orElseThrow(() -> new BaseException(IP_POOL_NOT_FOUND));

        if(!ipAddressInPoolRange(ipPool, ipAddress)) {
            log.warn("IP '{}' is not in range of IP Pool with Lower Bound '{}', and Upper Bound '{}'.", ipAddress, ipPool.getLowerBound(), ipPool.getUpperBound());
            throw new BaseException(IP_ADDRESS_NOT_IN_RANGE);
        }

        final IpAddress ipAddressToFree = getIpAddressByValue(ipAddress);
        ipAddressRepository.delete(ipAddressToFree);
    }

    @Override
    public IpAddress getIpAddressByValue(final String ipAddress) {
        log.debug("Retrieving IP Address object by value '{}'.", ipAddress);
        return ipAddressRepository.findByValue(ipAddress).orElseThrow(() -> new BaseException(IP_ADDRESS_NOT_FOUND));
    }

    private IpAddress transitState(final IpAddress ipAddress, final IpState newState) {
        validateStateTransition(ipAddress, newState);
        ipAddress.setResourceState(newState);
        return ipAddressRepository.save(ipAddress);
    }

    private void validateStateTransition(final IpAddress ipAddress, final IpState newState) {
        if(!ipAddress.getResourceState().getNextSteps().contains(newState)) {
            log.warn("Cannot transit from {} to {}.", ipAddress.getResourceState(), newState);
            throw new BaseException(IP_ADDRESS_INVALID_STATE_TRANSITION);
        }
    }

    private boolean ipAddressInPoolRange(final IpPool ipPool, final String value) {
        log.debug("Checking if '{}' is in range of IP Pool with ID '{}'", value, ipPool.getId());
        final IPv4Address iPv4Address = IPUtils.fromString(value);
        final IPv4Address lowerBound = IPUtils.fromString(ipPool.getLowerBound());
        final IPv4Address upperBound = IPUtils.fromString(ipPool.getUpperBound());

        return IPUtils.isInRange(iPv4Address, lowerBound, upperBound);
    }

}
