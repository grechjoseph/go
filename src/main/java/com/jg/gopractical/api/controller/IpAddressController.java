package com.jg.gopractical.api.controller;

import com.jg.gopractical.api.dto.ApiIpAddress;
import com.jg.gopractical.domain.model.IpAddress;
import com.jg.gopractical.mapper.ModelMapper;
import com.jg.gopractical.service.IpAddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class IpAddressController {

    private final IpAddressService ipAddressService;
    private final ModelMapper mapper;

    @PostMapping("/ip-addresses")
    public List<ApiIpAddress> reserveDynamicIpAddress(@RequestParam final Integer quantity) {
        return mapper.mapAsList(ipAddressService.reserveDynamicIpAddress(quantity), ApiIpAddress.class);
    }

    @PostMapping("ip-pools/{poolId}/ip-addresses")
    public ApiIpAddress reserveStaticIpAddress(@PathVariable final UUID poolId, @RequestBody final ApiIpAddress ipAddress) {
        return mapper.map(
                ipAddressService.reserveIpAddress(poolId, mapper.map(ipAddress, IpAddress.class)),
                ApiIpAddress.class);
    }

    @GetMapping("/ip-addresses")
    public ApiIpAddress getIpAddress(@RequestParam final String ipAddress) {
        return mapper.map(ipAddressService.getIpAddressByValue(ipAddress), ApiIpAddress.class);
    }

    @GetMapping("/ip-addresses/{addressId}")
    public ApiIpAddress getIpAddress(@PathVariable final UUID addressId) {
        return mapper.map(ipAddressService.getIpAddressById(addressId), ApiIpAddress.class);
    }

    @DeleteMapping("/ip-addresses")
    public ApiIpAddress blacklistIpAddress(@RequestBody final ApiIpAddress ipAddress) {
        return mapper.map(ipAddressService.blacklistIpAddress(mapper.map(ipAddress, IpAddress.class)), ApiIpAddress.class);
    }

    @PatchMapping("/ip-addresses/{addressId}")
    public void freeIpAddress(@PathVariable final UUID addressId) {
        ipAddressService.freeIpAddress(addressId);
    }

}
