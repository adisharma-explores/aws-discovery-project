package com.nimesa.awsDemo.model;

import jakarta.persistence.*;

@Entity
public class BucketObject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "bucket_object_id")
    private DiscoveryResult bucketObject;
    private String object;

    public DiscoveryResult getBucketObject() {
        return bucketObject;
    }
    public void setBucketObject(DiscoveryResult bucketObject) {
        this.bucketObject = bucketObject;
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }
}
