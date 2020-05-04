package com.jg.gopractical.domain.model;

import com.jg.gopractical.domain.enums.IpState;
import inet.ipaddr.IPAddress;
import lombok.Data;
import lombok.SneakyThrows;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.net.InetAddress;
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
    private IPAddress value;

    @Enumerated(EnumType.STRING)
    private IpState resourceState;

    @SneakyThrows
    public void setValue(final String value) {
        this.value = IPAddress.from(value.getBytes());
    }

}
