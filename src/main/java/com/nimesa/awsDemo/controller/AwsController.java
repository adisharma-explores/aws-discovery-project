package com.nimesa.awsDemo.controller;

import com.nimesa.awsDemo.model.BucketObject;
import com.nimesa.awsDemo.model.DiscoveryResult;
import com.nimesa.awsDemo.model.Job;
import com.nimesa.awsDemo.repository.BucketObjectRepository;
import com.nimesa.awsDemo.repository.DiscoveryResultRepository;
import com.nimesa.awsDemo.repository.JobRepository;
import com.nimesa.awsDemo.service.AwsDiscoveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/aws")
public class AwsController {
    @Autowired
    private AwsDiscoveryService awsDiscoveryService;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private DiscoveryResultRepository discoveryResultRepository;
    @Autowired
    private BucketObjectRepository bucketObjectRepository;

    @PostMapping("/discoverServices")
    public Long discoverServices(@RequestBody List<String> services) {
        Job job = new Job();
        job.setStatus("In Progress");
        job = jobRepository.save(job);
        if (services.contains("EC2")) {
            awsDiscoveryService.discoverEc2Instances(job);
        }
        if (services.contains("S3")) {
            awsDiscoveryService.discoverS3Buckets(job);
        }
        return job.getId();
    }

    @GetMapping("/getJobResult/{jobId}")
    public String getJobResult(@PathVariable Long jobId) {
        Job job = jobRepository.findById(jobId).orElseThrow();
        return job.getStatus();
    }

    @GetMapping("/getDiscoveryResult/{serviceName}")
    public List<String> getDiscoveryResult(@PathVariable String serviceName) {
        List<DiscoveryResult> results = discoveryResultRepository.findByServiceName(serviceName);
        return results.stream().map(DiscoveryResult::getResult).collect(Collectors.toList());
    }

    @PostMapping("/getS3BucketObjects/{bucketName}")
    public Long getS3BucketObjects(@PathVariable String bucketName) {
        Job job = new Job();
        job.setStatus("In Progress");
        job = jobRepository.save(job);
        List<DiscoveryResult> results = discoveryResultRepository.findByResult(bucketName);
        if(results.isEmpty()){
            job.setStatus("Failed");
            job = jobRepository.save(job);
        }
        awsDiscoveryService.discoverS3BucketObjects(bucketName, job,results.get(0) );
        return job.getId();
    }

    @GetMapping("/getS3BucketObjectCount/{bucketName}")
    public Long getS3BucketObjectCount(@PathVariable String bucketName) {
        List<DiscoveryResult> discoveryResults = discoveryResultRepository.findByResult(bucketName);
        if(discoveryResults.isEmpty()){
            return 0L;
        }
        return bucketObjectRepository.countByBucketObject(discoveryResults.get(0));
    }

    @GetMapping("/getS3BucketObjectlike/{bucketName}/{pattern}")
    public List<String> getS3BucketObjectlike(@PathVariable String bucketName, @PathVariable String pattern) {
        List<DiscoveryResult> discoveryResults = discoveryResultRepository.findByResult(bucketName);
        if(discoveryResults.isEmpty()){
            return new ArrayList<>();
        }
        List<BucketObject> results = bucketObjectRepository.findByBucketObjectAndObjectLike(discoveryResults.get(0), pattern);
        return results.stream().map(BucketObject::getObject).collect(Collectors.toList());
    }
}
