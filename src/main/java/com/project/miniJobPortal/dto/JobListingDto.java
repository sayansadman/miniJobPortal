package com.project.miniJobPortal.dto;

import com.project.miniJobPortal.entity.JobSkill;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JobListingDto {
    private Long jobId;
    private String title;
    private String location;
    private Integer minExperience;
    private Date createdAt;
    private String companyName;
    private String industry;
    private List<JobSkill> jobSkills;
    private Integer matchedCandidatesCount;
} 