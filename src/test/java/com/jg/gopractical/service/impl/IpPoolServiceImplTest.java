package com.jg.gopractical.service.impl;

import com.jg.gopractical.domain.model.IpPool;
import com.jg.gopractical.domain.repository.IpPoolRepository;
import com.jg.gopractical.service.IpPoolService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import static org.mockito.Mockito.verify;

@SpringBootTest
public class IpPoolServiceImplTest {

    @Mock
    private IpPoolRepository ipPoolRepository;

    @InjectMocks
    private IpPoolService ipPoolService;

    final IpPool ipPool = new IpPool();

    @BeforeEach
    public void beforeEach() {
        ipPool.setDescription("This is a sample description.");
        ipPool.setUpperBound("127.0.0.0");
        ipPool.setLowerBound("127.0.0.1");
    }

    @Test
    public void createIpPool_shouldCreatePool() {
        ipPoolService.createIpPool(ipPool);
        verify(ipPoolRepository).save(ipPool);
    }
}
