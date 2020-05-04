package com.jg.gopractical.domain.model;

import inet.ipaddr.IPAddress;
import inet.ipaddr.ipv4.IPv4Address;
import lombok.Data;
import lombok.SneakyThrows;

import javax.persistence.*;
import java.net.InetAddress;
import java.util.Set;
import java.util.UUID;

import static javax.persistence.CascadeType.ALL;

@Data
@Entity
public class IpPool {

    @Id
    private UUID id = UUID.randomUUID();

    private String description;

    private IPAddress lowerBound;

    private IPAddress upperBound;

    @OneToOne(cascade = ALL)
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
        this.lowerBound = IPAddress.from(lowerBound.getBytes());
    }

    @SneakyThrows
    public void setUpperBound(final String upperBound) {
        this.upperBound = IPAddress.from(upperBound.getBytes());
    }


}
