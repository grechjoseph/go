package com.jg.gopractical.domain.repository;

import com.jg.gopractical.domain.model.IpAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface IpAddressRepository extends JpaRepository<IpAddress, UUID> {
}
