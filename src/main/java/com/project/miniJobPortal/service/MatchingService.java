package com.project.miniJobPortal.service;

import com.project.miniJobPortal.dto.CandidateMatchDto;
import com.project.miniJobPortal.entity.Job;
import com.project.miniJobPortal.repository.JobRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MatchingService {

    @PersistenceContext
    private EntityManager entityManager;

    private final JobRepository jobRepository;

    @Autowired
    public MatchingService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    public List<CandidateMatchDto> getMatchesForJob(Long jobId) {
        Optional<Job> jobOptional = jobRepository.findById(jobId);
        if (jobOptional.isEmpty()) {
            throw new RuntimeException("Job not found with ID: " + jobId);
        }
//                JOIN job_skill js ON j.job_id = js.job_id
        String sql = """
            WITH JobSkillsCount AS (
                SELECT 
                    COUNT(js.skill_id) as total_skills
                FROM job_skill js

                WHERE js.job_id = :jobId
            ),
            CandidateMatches AS (
                SELECT 
                    c.candidate_id,
                    u.name as candidate_name,
                    c.location as candidate_location,
                    c.years_of_experience,
                    j.location as job_location,
                    j.min_experience,
                    COUNT(DISTINCT js.skill_id) as matching_skills_count,
                    SUM(CASE 
                        WHEN cs.skill_id IS NOT NULL THEN cs.proficiency * js.importance 
                        ELSE 0 
                    END) as skill_score,
                    (SELECT total_skills FROM JobSkillsCount) as total_job_skill
                FROM candidates c
                JOIN users u ON c.user_id = u.user_id
                JOIN jobs j ON j.job_id = :jobId
                JOIN job_skill js ON j.job_id = js.job_id
                LEFT JOIN candidate_skill cs ON c.candidate_id = cs.candidate_id AND js.skill_id = cs.skill_id
                GROUP BY c.candidate_id, u.name, c.location, c.years_of_experience, j.location, j.min_experience
            )
            SELECT 
                cm.candidate_id,
                cm.candidate_name,
                cm.candidate_location,
                cm.years_of_experience,
                cm.matching_skills_count,
                cm.total_job_skill,
                (cm.matching_skills_count * 100.0 / NULLIF(cm.total_job_skill, 0)) as skills_match_percentage,
                cm.skill_score,
                CASE 
                    WHEN cm.candidate_location = cm.job_location THEN 20 
                    ELSE 0 
                END as location_bonus,
                CASE 
                    WHEN cm.years_of_experience < cm.min_experience THEN -10 * (cm.min_experience - cm.years_of_experience)
                    ELSE 0 
                END as experience_penalty,
                (cm.skill_score + 
                CASE 
                    WHEN cm.candidate_location = cm.job_location THEN 20 
                    ELSE 0 
                END + 
                CASE 
                    WHEN cm.years_of_experience < cm.min_experience THEN -10 * (cm.min_experience - cm.years_of_experience)
                    ELSE 0 
                END) as total_score
            FROM CandidateMatches cm
            WHERE (cm.matching_skills_count * 100.0 / NULLIF(cm.total_job_skill, 0)) >= 60
            ORDER BY total_score DESC
        """;

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("jobId", jobId);
        
        List<Object[]> results = query.getResultList();
        List<CandidateMatchDto> matches = new ArrayList<>();
        
        for (Object[] result : results) {
            CandidateMatchDto match = new CandidateMatchDto();
            match.setCandidateId(((Number) result[0]).longValue());
            match.setCandidateName((String) result[1]);
            match.setCandidateLocation((String) result[2]);
            match.setYearsOfExperience(((Number) result[3]).intValue());
            match.setMatchingSkillsCount(((Number) result[4]).intValue());
            match.setTotalJobSkills(((Number) result[5]).intValue());
            match.setSkillsMatchPercentage(((Number) result[6]).doubleValue());
            match.setSkillScore(((Number) result[7]).doubleValue());
            match.setLocationBonus(((Number) result[8]).intValue());
            match.setExperiencePenalty(((Number) result[9]).intValue());
            match.setTotalScore(((Number) result[10]).doubleValue());
            matches.add(match);
        }
        
        return matches;
    }
    
    public List<Job> getAllJobsWithCandidateCount() {
        String sql = """
            SELECT 
                j.job_id,
                COUNT(DISTINCT cm.candidate_id) as candidates_count
            FROM jobs j
            LEFT JOIN (
                WITH JobSkillsCount AS (
                    SELECT 
                        j.job_id,
                        COUNT(js.skill_id) as total_skills
                    FROM jobs j
                    JOIN job_skill js ON j.job_id = js.job_id
                    GROUP BY j.job_id
                )
                SELECT 
                    j.job_id,
                    c.candidate_id,
                    COUNT(DISTINCT js.skill_id) as matching_skills_count,
                    (SELECT total_skills FROM JobSkillsCount WHERE job_id = j.job_id) as total_job_skill
                FROM jobs j
                JOIN job_skill js ON j.job_id = js.job_id
                JOIN candidate_skill cs ON js.skill_id = cs.skill_id
                JOIN candidates c ON cs.candidate_id = c.candidate_id
                GROUP BY j.job_id, c.candidate_id
                HAVING (matching_skills_count * 100.0 / NULLIF(total_job_skill, 0)) >= 60
            ) cm ON j.job_id = cm.job_id
            GROUP BY j.job_id
        """;
        
        Query query = entityManager.createNativeQuery(sql);
        List<Object[]> results = query.getResultList();
        
        // Create a map of job ID to candidate count
        return jobRepository.findAll().stream()
            .peek(job -> {
                // Find the candidate count for this job
                for (Object[] result : results) {
                    Long resultJobId = ((Number) result[0]).longValue();
                    if (job.getJobId().equals(resultJobId)) {
                        Integer candidateCount = ((Number) result[1]).intValue();
                        job.getJobSkills().size(); // Initialize the jobSkills collection
                        // Store count in a property or use transient field if needed
                        // For simplicity, we'll use a transient property in DTO
                        break;
                    }
                }
            })
            .toList();
    }
}
