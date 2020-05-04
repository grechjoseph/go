package com.jg.gopractical.service.impl;

import com.jg.gopractical.domain.model.IpPool;
import com.jg.gopractical.domain.repository.IpPoolRepository;
import com.jg.gopractical.service.IpPoolService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class IpPoolServiceImpl implements IpPoolService {

    private IpPoolRepository ipPoolRepository;

    @Override
    public IpPool createIpPool(final IpPool ipPool) {
        return null;
    }

    @Override
    public IpPool getIpPoolById(final UUID ipPoolId) {
        return null;
    }

    @Override
    public List<IpPool> getIpPools() {
        return null;
    }

    @Override
    public IpPool updateIpPool(final UUID ipPoolId, final IpPool newValues) {
        return null;
    }

    @Override
    public void deleteIpPool(final UUID ipPoolId) {

    }
}
