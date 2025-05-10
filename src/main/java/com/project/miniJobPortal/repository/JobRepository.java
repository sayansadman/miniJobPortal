package com.project.miniJobPortal.repository;

import com.project.miniJobPortal.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRepository extends JpaRepository<Job, Long> {
}
