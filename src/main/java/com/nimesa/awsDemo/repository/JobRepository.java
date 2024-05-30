package com.nimesa.awsDemo.repository;

import com.nimesa.awsDemo.model.Job;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRepository extends JpaRepository<Job, Long> {
}

