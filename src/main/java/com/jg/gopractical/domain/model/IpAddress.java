package com.jg.gopractical.domain.model;

import com.jg.gopractical.domain.enums.IpState;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.UUID;

import static javax.persistence.FetchType.LAZY;

@Data
@Entity
@Table(name = "ip_address")
public class IpAddress {

    @Id
    private UUID id = UUID.randomUUID();

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "ip_pool_id")
    private IpPool ipPool;

    @NotEmpty
    private Integer value;

    @Enumerated(EnumType.STRING)
    private IpState resourceState;

}
