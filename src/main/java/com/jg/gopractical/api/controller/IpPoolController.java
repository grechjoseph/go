package com.jg.gopractical.api.controller;

import com.jg.gopractical.api.dto.ApiIpAddressExtended;
import com.jg.gopractical.api.dto.ApiIpPool;
import com.jg.gopractical.api.dto.ApiIpPoolExtended;
import com.jg.gopractical.domain.model.IpPool;
import com.jg.gopractical.mapper.ModelMapper;
import com.jg.gopractical.service.IpPoolService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ip-pools")
public class IpPoolController {

    private final IpPoolService ipPoolService;
    private final ModelMapper mapper;

    @PostMapping
    public ApiIpPoolExtended createIpPool(@RequestBody final ApiIpPool ipPool) {
        return mapper.map(ipPoolService.createIpPool(mapper.map(ipPool, IpPool.class)), ApiIpPoolExtended.class);
    }

    @GetMapping("/{poolId}")
    public ApiIpPoolExtended getIpPoolById(@PathVariable final UUID poolId) {
        return mapper.map(ipPoolService.getIpPoolById(poolId), ApiIpPoolExtended.class);
    }

    @GetMapping
    public List<ApiIpPoolExtended> getIpPools() {
        return mapper.mapAsList(ipPoolService.getIpPools(), ApiIpPoolExtended.class);
    }

    @PutMapping("/{poolId}")
    public ApiIpPoolExtended updateIpPoolById(@PathVariable final UUID poolId, @RequestBody final ApiIpPool ipPool) {
        return mapper.map(ipPoolService.updateIpPool(poolId, mapper.map(ipPool, IpPool.class)), ApiIpPoolExtended.class);
    }

    @DeleteMapping("/{poolId}")
    public void deleteIpPoolById(@PathVariable final UUID poolId) {
        ipPoolService.deleteIpPool(poolId);
    }


    @GetMapping("/{poolId}/ip-address")
    public List<ApiIpAddressExtended> getIpAddressForPool(@PathVariable final UUID poolId) {
        final IpPool pool = ipPoolService.getIpPoolById(poolId);
        return mapper.mapAsList(pool.getIpAddresses(), ApiIpAddressExtended.class);
    }
}
