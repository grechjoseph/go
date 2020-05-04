package com.jg.gopractical.service.impl;

import com.jg.gopractical.domain.exception.BaseException;
import com.jg.gopractical.domain.model.IpAddress;
import com.jg.gopractical.domain.model.IpPool;
import com.jg.gopractical.domain.repository.IpAddressRepository;
import com.jg.gopractical.domain.repository.IpPoolRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.stubbing.Answer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.jg.gopractical.domain.enums.IpState.BLACKLISTED;
import static com.jg.gopractical.domain.enums.IpState.RESERVED;
import static com.jg.gopractical.domain.exception.ErrorCode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
public class IpAddressServiceImplTest {

    private static int QUANTITY = 10;
    private static String LOWER_BOUND = "0.0.0.0";
    private static String UPPER_BOUND = "0.0.0." + QUANTITY;
    private static IpAddress IP_ADDRESS_OBJ = new IpAddress();
    private static UUID POOL_ID = UUID.randomUUID();

    @Mock
    private IpPoolRepository mockIpPoolRepository;

    @Mock
    private IpAddressRepository mockIpAddressRepository;

    @InjectMocks
    private IpAddressServiceImpl ipAddressService;

    @Mock
    private IpPool mockIpPool;

    @BeforeEach
    public void beforeEach() {
        IP_ADDRESS_OBJ.setValue(LOWER_BOUND);
        when(mockIpPool.getUpperBound()).thenReturn(UPPER_BOUND);
        when(mockIpPool.getLowerBound()).thenReturn(LOWER_BOUND);
        when(mockIpPool.getId()).thenReturn(POOL_ID);
        when(mockIpPoolRepository.findBySupportDynamicIsTrue()).thenReturn(Optional.of(mockIpPool));
        when(mockIpPoolRepository.findById(any(UUID.class))).thenReturn(Optional.of(mockIpPool));
        when(mockIpAddressRepository.saveAll(any(List.class))).thenAnswer((Answer) invocation -> invocation.getArguments()[0]);
        when(mockIpAddressRepository.save(any(IpAddress.class))).thenAnswer((Answer) invocation -> invocation.getArguments()[0]);
        when(mockIpPool.getAvailableCapacity()).thenReturn(QUANTITY);
    }

    @Test
    public void reserveDyanmicIps_quantityAvailable_shouldCreateIPs() {
        final List<IpAddress> ipAddresses = ipAddressService.reserveDynamicIpAddress(QUANTITY);
        verify(mockIpAddressRepository).saveAll(ipAddresses);
        assertThat(ipAddresses.size()).isEqualTo(QUANTITY);
        ipAddresses.forEach(ipAddress -> assertThat(ipAddress.getResourceState()).isEqualTo(RESERVED));
    }

    @Test
    public void reserveDynamicIps_quantityNotAvailable_shouldThrowExhaustedException() {
        when(mockIpPool.getAvailableCapacity()).thenReturn(QUANTITY - 1);
        Assertions.assertThatThrownBy(() -> ipAddressService.reserveDynamicIpAddress(QUANTITY))
                .isEqualTo(new BaseException(IP_POOL_EXHAUSTED));
        verify(mockIpAddressRepository, never()).saveAll(any());
    }

    @Test
    public void reserveDynamicIps_middleIpTaken_shouldCreateRemainingIPs() {
        when(mockIpAddressRepository.findByValue(LOWER_BOUND)).thenReturn(Optional.of(new IpAddress()));
        final List<IpAddress> ipAddresses = ipAddressService.reserveDynamicIpAddress(QUANTITY);
        verify(mockIpAddressRepository).saveAll(ipAddresses);
        assertThat(ipAddresses).noneMatch(ipAddress -> ipAddress.getValue().equals(LOWER_BOUND));
        assertThat(ipAddresses.size()).isEqualTo(QUANTITY);
        ipAddresses.forEach(ipAddress -> assertThat(ipAddress.getResourceState()).isEqualTo(RESERVED));
    }

    @Test
    public void reserveStaticIp_validObject_shouldCreateStaticIpInReservedState() {
        when(mockIpPool.getAvailableCapacity()).thenReturn(1);
        final IpAddress ipAddress = ipAddressService.reserveIpAddress(UUID.randomUUID(), IP_ADDRESS_OBJ);
        assertThat(ipAddress.getResourceState()).isEqualTo(RESERVED);
        verify(mockIpAddressRepository).save(IP_ADDRESS_OBJ);
    }

    @Test
    public void reserveStaticIp_quantityNotAvailable_shouldThrowPoolExhaustedException() {
        when(mockIpPool.getAvailableCapacity()).thenReturn(0);
        assertThatThrownBy(() -> ipAddressService.reserveIpAddress(UUID.randomUUID(), IP_ADDRESS_OBJ))
                .isEqualTo(new BaseException(IP_POOL_EXHAUSTED));
        verify(mockIpAddressRepository, never()).save(any(IpAddress.class));
    }

    @Test
    public void reserveStaticIp_ipNotInRange_shouldThrowNotInRangeException() {
        IP_ADDRESS_OBJ.setValue("1.1.1.1");
        assertThatThrownBy(() -> ipAddressService.reserveIpAddress(UUID.randomUUID(), IP_ADDRESS_OBJ))
                .isEqualTo(new BaseException(IP_ADDRESS_NOT_IN_RANGE));
        verify(mockIpAddressRepository, never()).save(any(IpAddress.class));
    }

    @Test
    public void reserveStaticIp_ipAlreadyReserved_shouldThrowNotAvailableException() {
        when(mockIpAddressRepository.findByValue(any(String.class))).thenReturn(Optional.of(IP_ADDRESS_OBJ));
        assertThatThrownBy(() -> ipAddressService.reserveIpAddress(UUID.randomUUID(), IP_ADDRESS_OBJ))
                .isEqualTo(new BaseException(IP_ADDRESS_NOT_AVAILABLE));
        verify(mockIpAddressRepository, never()).save(any(IpAddress.class));
    }

    @Test
    public void reserveStaticIp_ipBlacklisted_shouldThrowBlacklistedException() {
        final IpAddress ipAddressObj = new IpAddress();
        ipAddressObj.setResourceState(BLACKLISTED);
        when(mockIpAddressRepository.findByValue(any(String.class))).thenReturn(Optional.of(ipAddressObj));
        assertThatThrownBy(() -> ipAddressService.reserveIpAddress(UUID.randomUUID(), IP_ADDRESS_OBJ))
                .isEqualTo(new BaseException(IP_ADDRESS_BLACKLISTED));
        verify(mockIpAddressRepository, never()).save(any(IpAddress.class));
    }

    @Test
    public void blacklistIpAddress_noExist_shouldCreateBlacklistedIp() {
        final IpAddress ipAddress = ipAddressService.blacklistIpAddress(mockIpPool.getId(), IP_ADDRESS_OBJ.getValue());
        verify(mockIpAddressRepository).save(any(IpAddress.class));
        assertThat(ipAddress.getResourceState()).isEqualTo(BLACKLISTED);
    }

    @Test
    public void blacklistIpAddress_ipReserved_shouldThrowIpInUseException() {
        final IpAddress ipAddressObj = new IpAddress();
        ipAddressObj.setResourceState(RESERVED);
        when(mockIpAddressRepository.findByValue(any(String.class))).thenReturn(Optional.of(ipAddressObj));
        assertThatThrownBy(() -> ipAddressService.blacklistIpAddress(mockIpPool.getId(), IP_ADDRESS_OBJ.getValue()))
                .isEqualTo(new BaseException(IP_ADDRESS_IN_USE));
        verify(mockIpAddressRepository, never()).save(any(IpAddress.class));
    }

    @Test
    public void blacklistIpAddress_ipAlreadyBlacklisted_shouldThrowIpIsBlacklistedException() {
        final IpAddress ipAddressObj = new IpAddress();
        ipAddressObj.setResourceState(BLACKLISTED);
        when(mockIpAddressRepository.findByValue(any(String.class))).thenReturn(Optional.of(ipAddressObj));
        assertThatThrownBy(() -> ipAddressService.blacklistIpAddress(mockIpPool.getId(), IP_ADDRESS_OBJ.getValue()))
                .isEqualTo(new BaseException(IP_ADDRESS_BLACKLISTED));
        verify(mockIpAddressRepository, never()).save(any(IpAddress.class));
    }

    @Test
    public void freeIpAddress_isReserved_shouldDeleteIpAddress() {
        when(mockIpAddressRepository.findByValue(any(String.class))).thenReturn(Optional.of(IP_ADDRESS_OBJ));
        ipAddressService.freeIpAddress(mockIpPool.getId(), IP_ADDRESS_OBJ.getValue());
        verify(mockIpAddressRepository).delete(IP_ADDRESS_OBJ);
    }

    @Test
    public void freeIpAddress_isBlacklisted_shouldDeleteIpAddress() {
        IP_ADDRESS_OBJ.setResourceState(BLACKLISTED);
        when(mockIpAddressRepository.findByValue(any(String.class))).thenReturn(Optional.of(IP_ADDRESS_OBJ));
        ipAddressService.freeIpAddress(mockIpPool.getId(), IP_ADDRESS_OBJ.getValue());
        verify(mockIpAddressRepository).delete(IP_ADDRESS_OBJ);
    }

    @Test
    public void freeIpAddress_notUsed_shouldThrowNotFoundException() {
        when(mockIpAddressRepository.findByValue(any(String.class))).thenReturn(Optional.empty());
        verify(mockIpAddressRepository, never()).delete(any(IpAddress.class));
    }

    @Test
    public void getIpAddressByValue_exists_shouldReturnObject() {
        when(mockIpAddressRepository.findByValue(any(String.class))).thenReturn(Optional.of(IP_ADDRESS_OBJ));
        final IpAddress ipAddressByValue = ipAddressService.getIpAddressByValue(IP_ADDRESS_OBJ.getValue());
        assertThat(ipAddressByValue).isEqualTo(IP_ADDRESS_OBJ);
    }

    @Test
    public void getIpAddressByValue_noExist_shouldThrowNotfFundException() {
        when(mockIpAddressRepository.findByValue(any(String.class))).thenReturn(Optional.empty());
        assertThatThrownBy(() -> ipAddressService.getIpAddressByValue(IP_ADDRESS_OBJ.getValue()))
                .isEqualTo(new BaseException(IP_ADDRESS_NOT_FOUND));
    }

}
