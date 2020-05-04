package com.jg.gopractical.domain.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Set;
import java.util.UUID;

import static javax.persistence.CascadeType.ALL;

@Data
@Entity
public class IpPool {

    @Id
    private UUID id = UUID.randomUUID();

    private String description;

    private Integer lowerBound;

    private Integer upperBound;

    @OneToOne(cascade = ALL)
    @JoinColumn(name = "ip_pool_id")
    private Set<IpAddress> ipAddresses;

    @Transient
    public Integer getTotalCapacity() {
        return upperBound - lowerBound;
    }

    @Transient
    public Integer getUsedCapacity() {
        return ipAddresses.size();
    }

}
