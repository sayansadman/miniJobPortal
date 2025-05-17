package com.project.miniJobPortal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CandidateMatchDto {
    private Long candidateId;
    private String candidateName;
    private String candidateEmail;
    private String candidateLocation;
    private Integer yearsOfExperience;
    private Integer matchingSkillsCount;
    private Integer totalJobSkills;
    private Double skillsMatchPercentage;
    private Double skillScore;
    private Integer locationBonus;
    private Integer experiencePenalty;
    private Double totalScore;
}
