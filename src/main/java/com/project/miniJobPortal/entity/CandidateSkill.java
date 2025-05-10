package com.project.miniJobPortal.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "candidate_skill",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"candidate_id", "skill_id"})
        }
)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CandidateSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long candidateSkillId;

    private Integer proficiency;

    @ManyToOne
    @JoinColumn(name = "candidate_id")
    private Candidate candidate;

    @ManyToOne
    @JoinColumn(name = "skill_id")
    private Skill skill;
}
