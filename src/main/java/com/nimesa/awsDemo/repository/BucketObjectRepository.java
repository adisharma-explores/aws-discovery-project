package com.nimesa.awsDemo.repository;

import com.nimesa.awsDemo.model.BucketObject;
import com.nimesa.awsDemo.model.DiscoveryResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BucketObjectRepository extends JpaRepository<BucketObject, Long> {
    long countByBucketObject(DiscoveryResult bucketObject);
    List<BucketObject> findByBucketObjectAndObjectLike(DiscoveryResult bucketObject,String pattern);
}