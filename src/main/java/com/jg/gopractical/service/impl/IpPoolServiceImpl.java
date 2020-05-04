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
        return ipPoolRepository.save(ipPool);
    }

    @Override
    public IpPool getIpPoolById(final UUID ipPoolId) {
        return ipPoolRepository.findById(ipPoolId).orElseThrow(() -> new RuntimeException("IP Pool not found."));
    }

    @Override
    public List<IpPool> getIpPools() {
        return ipPoolRepository.findAll();
    }

    @Override
    public IpPool updateIpPool(final UUID ipPoolId, final IpPool newValues) {
        final IpPool ipPool = getIpPoolById(ipPoolId);
        newValues.setId(ipPoolId);
        newValues.setIpAddresses(ipPool.getIpAddresses());
        return ipPoolRepository.save(newValues);
    }

    @Override
    public void deleteIpPool(final UUID ipPoolId) {
        ipPoolRepository.delete(getIpPoolById(ipPoolId));
    }
}
