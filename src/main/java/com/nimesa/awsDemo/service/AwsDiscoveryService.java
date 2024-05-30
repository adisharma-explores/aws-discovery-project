package com.nimesa.awsDemo.service;

import com.nimesa.awsDemo.model.BucketObject;
import com.nimesa.awsDemo.model.DiscoveryResult;
import com.nimesa.awsDemo.model.Job;
import com.nimesa.awsDemo.repository.BucketObjectRepository;
import com.nimesa.awsDemo.repository.DiscoveryResultRepository;
import com.nimesa.awsDemo.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesRequest;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesResponse;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Bucket;
import software.amazon.awssdk.services.s3.model.ListBucketsResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AwsDiscoveryService {

    @Autowired
    private S3Client s3Client;

    @Autowired
    private Ec2Client ec2Client;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private DiscoveryResultRepository discoveryResultRepository;

    @Autowired
    private BucketObjectRepository bucketObjectRepository;

    @Async
    public void discoverEc2Instances(Job jobId) {
        try {
            DescribeInstancesRequest request = DescribeInstancesRequest.builder().build();
            DescribeInstancesResponse response = ec2Client.describeInstances(request);
            List<String> instanceIds = response.reservations().stream()
                    .flatMap(reservation -> reservation.instances().stream())
                    .map(instance -> instance.instanceId())
                    .collect(Collectors.toList());
            for (String instanceId : instanceIds) {
                DiscoveryResult discoveryResult = new DiscoveryResult();
                discoveryResult.setServiceName("EC2");
                discoveryResult.setResult(instanceId);
                discoveryResultRepository.save(discoveryResult);
            }
            jobId.setStatus("Success");
            jobRepository.save(jobId);
        } catch (Exception e) {
            jobId.setStatus("Failed");
            jobRepository.save(jobId);
        }
    }

    @Async
    public void discoverS3Buckets(Job jobId) {
        try {
            ListBucketsResponse listBucketsResponse = s3Client.listBuckets();
            List<String> bucketNames = listBucketsResponse.buckets().stream()
                    .map(Bucket::name)
                    .collect(Collectors.toList());

            for (String bucketName : bucketNames) {
                DiscoveryResult discoveryResult = new DiscoveryResult();
                discoveryResult.setServiceName("S3");
                discoveryResult.setResult(bucketName);
                discoveryResultRepository.save(discoveryResult);
            }
            jobId.setStatus("Success");
            jobRepository.save(jobId);
        } catch (Exception e) {
            jobId.setStatus("Failed");
            jobRepository.save(jobId);
        }
    }
    @Async
    public void discoverS3BucketObjects(String bucketName,Job jobId,DiscoveryResult bucketObject) {
        try {
            ListObjectsV2Request listObjectsV2Request = ListObjectsV2Request.builder().bucket(bucketName).build();
            ListObjectsV2Response listObjectsV2Response = s3Client.listObjectsV2(listObjectsV2Request);
            List<String> objectNames = listObjectsV2Response.contents().stream()
                    .map(s3Object -> s3Object.key())
                    .collect(Collectors.toList());

            for (String objectName : objectNames) {
                BucketObject discoveryResult = new BucketObject();
                discoveryResult.setBucketObject(bucketObject);
                discoveryResult.setObject(objectName);
                bucketObjectRepository.save(discoveryResult);
            }
            jobId.setStatus("Success");
            jobRepository.save(jobId);
        } catch (Exception e) {
            jobId.setStatus("Failed");
            jobRepository.save(jobId);        }
    }
}


