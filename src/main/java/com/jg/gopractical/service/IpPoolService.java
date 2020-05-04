package com.jg.gopractical.service;

import com.jg.gopractical.domain.model.IpPool;

import java.util.List;
import java.util.UUID;

public interface IpPoolService {

    IpPool createIpPool(final IpPool ipPool);

    IpPool getIpPoolById(final UUID ipPoolId);

    List<IpPool> getIpPools();

    IpPool updateIpPool(final UUID ipPoolId, final IpPool newValues);

    void deleteIpPool(final UUID ipPoolId);

}
