package com.jg.gopractical.domain.enums;

import lombok.Getter;

import java.util.List;

public enum IpState {

    FREE, RESERVED, BLACKLISTED;

    @Getter
    private List<IpState> nextSteps;

    static {
        FREE.nextSteps = List.of(RESERVED, BLACKLISTED);
        RESERVED.nextSteps = List.of(FREE);
        BLACKLISTED.nextSteps = List.of(FREE);
    }

}
