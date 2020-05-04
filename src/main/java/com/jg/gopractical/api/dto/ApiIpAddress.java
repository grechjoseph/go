package com.jg.gopractical.api.dto;

import com.jg.gopractical.domain.enums.IpState;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.UUID;

@Data
@ApiModel(value = "Represents the creation and reading of IP Address objects.")
public class ApiIpAddress {

    @ApiModelProperty(value = "The ID of the IP Address resource.")
    private final UUID id;

    @ApiModelProperty(value = "The ID of the IP Address's pool.")
    private final UUID ipPoolId;

    @ApiModelProperty(value = "The value of the IP Address.")
    private final String value;

    @ApiModelProperty(value = "The state of the IP Address.")
    private final IpState resourceState;

}
