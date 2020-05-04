package com.jg.gopractical.api.controller;

import com.jg.gopractical.api.dto.ApiIpAddress;
import com.jg.gopractical.domain.model.IpAddress;
import com.jg.gopractical.mapper.ModelMapper;
import com.jg.gopractical.service.IpAddressService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Api(value = "Manage issuing of IPs.")
public class IpAddressController {

    private final IpAddressService ipAddressService;
    private final ModelMapper mapper;

    @PostMapping("/ip-addresses")
    @ApiOperation(value = "Reserve a number of dynamic IPs from the Dynamic IPs pool.")
    public List<ApiIpAddress> reserveDynamicIpAddress(@RequestParam final Integer quantity) {
        return mapper.mapAsList(ipAddressService.reserveDynamicIpAddress(quantity), ApiIpAddress.class);
    }

    @PostMapping("ip-pools/{poolId}/ip-addresses")
    @ApiOperation(value = "Reserve a Static IP from a Static IP Pool.")
    public ApiIpAddress reserveStaticIpAddress(@PathVariable final UUID poolId, @RequestBody final ApiIpAddress ipAddress) {
        return mapper.map(
                ipAddressService.reserveIpAddress(poolId, mapper.map(ipAddress, IpAddress.class)),
                ApiIpAddress.class);
    }

    @GetMapping("/ip-addresses")
    @ApiOperation(value = "Get an IP resource by its address.")
    public ApiIpAddress getIpAddress(@RequestParam final String ipAddress) {
        return mapper.map(ipAddressService.getIpAddressByValue(ipAddress), ApiIpAddress.class);
    }

    @GetMapping("/ip-addresses/{addressId}")
    @ApiOperation(value = "Get an IP resource by its ID.")
    public ApiIpAddress getIpAddress(@PathVariable final UUID addressId) {
        return mapper.map(ipAddressService.getIpAddressById(addressId), ApiIpAddress.class);
    }

    @DeleteMapping("/ip-addresses")
    @ApiOperation(value = "Blacklist an IP Address.")
    public ApiIpAddress blacklistIpAddress(@RequestBody final ApiIpAddress ipAddress) {
        return mapper.map(ipAddressService.blacklistIpAddress(mapper.map(ipAddress, IpAddress.class)), ApiIpAddress.class);
    }

    @PatchMapping("/ip-addresses/{addressId}")
    @ApiOperation(value = "Free an IP Address by its ID.")
    public void freeIpAddress(@PathVariable final UUID addressId) {
        ipAddressService.freeIpAddress(addressId);
    }

}
