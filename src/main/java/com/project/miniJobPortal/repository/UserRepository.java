package com.project.miniJobPortal.repository;

import com.project.miniJobPortal.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
