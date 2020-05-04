package com.jg.gopractical.domain.model;

import com.jg.gopractical.utils.IPUtils;
import inet.ipaddr.IPAddress;
import inet.ipaddr.ipv4.IPv4Address;
import lombok.Data;
import lombok.SneakyThrows;

import javax.persistence.*;
import java.util.Set;
import java.util.UUID;

import static com.jg.gopractical.utils.IPUtils.fromString;
import static javax.persistence.CascadeType.ALL;

@Data
@Entity
public class IpPool {

    @Id
    private UUID id = UUID.randomUUID();

    private String description;

    private IPAddress lowerBound;

    private IPAddress upperBound;

    private boolean supportDynamic;

    @OneToMany(cascade = ALL)
    @JoinColumn(name = "ip_pool_id")
    private Set<IpAddress> ipAddresses;

    @Transient
    public Integer getTotalCapacity() {
        return upperBound.subtract(lowerBound).length;
    }

    @Transient
    public Integer getUsedCapacity() {
        return ipAddresses.size();
    }

    @SneakyThrows
    public void setLowerBound(final String lowerBound) {
        this.lowerBound = fromString(lowerBound);
    }

    @SneakyThrows
    public void setUpperBound(final String upperBound) {
        this.upperBound = fromString(upperBound);
    }


}
