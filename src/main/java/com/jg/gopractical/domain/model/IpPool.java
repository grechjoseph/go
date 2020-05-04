package com.jg.gopractical.domain.model;

import com.jg.gopractical.utils.IPUtils;
import inet.ipaddr.ipv4.IPv4Address;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.Set;
import java.util.UUID;

import static javax.persistence.CascadeType.ALL;

@Data
@Entity
public class IpPool {

    @Id
    private UUID id = UUID.randomUUID();

    private String description;

    @NotEmpty
    private String lowerBound;

    @NotEmpty
    private String upperBound;

    private boolean supportDynamic;

    @OneToMany(cascade = ALL)
    @JoinColumn(name = "ip_pool_id")
    private Set<IpAddress> ipAddresses;

    @Transient
    public Integer getTotalCapacity() {
        final IPv4Address upper = IPUtils.fromString(upperBound);
        final IPv4Address lower = IPUtils.fromString(lowerBound);
        return IPUtils.getDifference(upper, lower) + 1;
    }

    @Transient
    public Integer getUsedCapacity() {
        return ipAddresses.size();
    }

    @Transient
    public Integer getAvailableCapacity() {
        return getTotalCapacity() - getUsedCapacity();
    }

}
