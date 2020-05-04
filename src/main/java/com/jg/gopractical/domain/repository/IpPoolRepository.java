package com.jg.gopractical.domain.repository;

import com.jg.gopractical.domain.model.IpPool;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface IpPoolRepository extends JpaRepository<IpPool, UUID> {
}
