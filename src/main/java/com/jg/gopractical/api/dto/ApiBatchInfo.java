package com.jg.gopractical.api.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "Batch configuration")
public class ApiBatchInfo {

    @ApiModelProperty(value = "The number of IP Addresses to be reserved.")
    private final Integer quantity;

    @ApiModelProperty(value = "Batch note.")
    private final String note;

}
