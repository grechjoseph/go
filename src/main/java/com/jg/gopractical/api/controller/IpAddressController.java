package com.jg.gopractical.api.controller;

import com.jg.gopractical.api.dto.ApiBatchInfo;
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
    public List<ApiIpAddress> reserveDynamicIpAddress(@RequestBody final ApiBatchInfo batchInfo) {
        return mapper.mapAsList(ipAddressService.reserveDynamicIpAddress(batchInfo.getQuantity()), ApiIpAddress.class);
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
    public ApiIpAddress getIpAddress(@RequestBody final ApiIpAddress ipAddress) {
        return mapper.map(ipAddressService.getIpAddressByValue(ipAddress.getValue()), ApiIpAddress.class);
    }

    @GetMapping("/ip-addresses/{addressId}")
    @ApiOperation(value = "Get an IP resource by its ID.")
    public ApiIpAddress getIpAddress(@PathVariable final UUID addressId) {
        return mapper.map(ipAddressService.getIpAddressById(addressId), ApiIpAddress.class);
    }

    @DeleteMapping("/ip-pools/{poolId}/ip-addresses")
    @ApiOperation(value = "Blacklist an IP Address.")
    public ApiIpAddress blacklistIpAddress(@PathVariable final UUID poolId, @RequestBody final ApiIpAddress ipAddress) {
        return mapper.map(ipAddressService.blacklistIpAddress(poolId, ipAddress.getValue()), ApiIpAddress.class);
    }

    @PatchMapping("/ip-pools/{poolId}")
    @ApiOperation(value = "Free an IP Address from a pool.")
    public void freeIpAddress(@PathVariable final UUID poolId, @RequestBody final ApiIpAddress ipAddress) {
        ipAddressService.freeIpAddress(poolId, ipAddress.getValue());
    }

}
