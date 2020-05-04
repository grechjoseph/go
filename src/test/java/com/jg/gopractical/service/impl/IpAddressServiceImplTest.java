package com.jg.gopractical.service.impl;

import com.jg.gopractical.domain.repository.IpAddressRepository;
import com.jg.gopractical.service.IpAddressService;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class IpAddressServiceImplTest {

    @Mock
    private IpAddressRepository ipAddressRepository;

    @InjectMocks
    private IpAddressService ipAddressService;

    @BeforeEach
    public void beforeEach() {

    }
}
