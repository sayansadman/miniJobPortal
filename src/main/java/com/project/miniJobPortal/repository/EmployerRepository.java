package com.project.miniJobPortal.repository;

import com.project.miniJobPortal.entity.Employer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployerRepository extends JpaRepository<Employer, Long> {
}
