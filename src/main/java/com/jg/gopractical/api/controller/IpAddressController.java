package com.jg.gopractical.api.controller;

import com.jg.gopractical.api.dto.ApiIpAddress;
import com.jg.gopractical.api.dto.ApiIpAddressExtended;
import com.jg.gopractical.domain.model.IpAddress;
import com.jg.gopractical.mapper.ModelMapper;
import com.jg.gopractical.service.IpAddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ip-addresses")
public class IpAddressController {

    private final IpAddressService ipAddressService;
    private final ModelMapper mapper;

    @GetMapping("/{addressId}")
    public ApiIpAddressExtended getIpAddressById(@PathVariable final UUID addressId) {
        return mapper.map(ipAddressService.getIpAddressById(addressId), ApiIpAddressExtended.class);
    }

    @GetMapping
    public List<ApiIpAddressExtended> getIpAddress() {
        return mapper.mapAsList(ipAddressService.getIpAddresses(), ApiIpAddressExtended.class);
    }

    @PutMapping
    public ApiIpAddressExtended reserveIpAddress(@RequestBody final ApiIpAddress ipAddress) {
        return mapper.map(ipAddressService.reserveIpAddress(mapper.map(ipAddress, IpAddress.class)), ApiIpAddressExtended.class);
    }

    @DeleteMapping
    public ApiIpAddressExtended blacklistIpAddress(@RequestBody final ApiIpAddress ipAddress) {
        return mapper.map(ipAddressService.blacklistIpAddress(mapper.map(ipAddress, IpAddress.class)), ApiIpAddressExtended.class);
    }

    @DeleteMapping("/{addressId}")
    public ApiIpAddressExtended blacklistIpAddress(@PathVariable final UUID addressIp) {
        return mapper.map(ipAddressService.blacklistIpAddress(addressIp), ApiIpAddressExtended.class);
    }

    @PatchMapping("/")



}
