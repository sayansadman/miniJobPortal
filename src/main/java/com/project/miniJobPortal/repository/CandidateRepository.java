package com.project.miniJobPortal.repository;

import com.project.miniJobPortal.entity.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CandidateRepository extends JpaRepository<Candidate, Long> {
}
