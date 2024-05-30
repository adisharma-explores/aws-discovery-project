package com.nimesa.awsDemo.repository;

import com.nimesa.awsDemo.model.DiscoveryResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
public interface DiscoveryResultRepository extends JpaRepository<DiscoveryResult, Long> {
    List<DiscoveryResult> findByServiceName(String serviceName);
    List<DiscoveryResult> findByResult(String result);
}