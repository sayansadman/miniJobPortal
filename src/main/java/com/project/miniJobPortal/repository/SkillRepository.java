package com.project.miniJobPortal.repository;

import com.project.miniJobPortal.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SkillRepository extends JpaRepository<Skill, Long> {
    Optional<Skill> findByName(String skillName);
}
