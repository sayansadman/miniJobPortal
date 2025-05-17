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
        
        String sql = """
            WITH JobSkillsCount AS (
                SELECT
                    COUNT(js.skill_id) as total_skills
                FROM job_skill js
                WHERE js.job_id = :jobId
            ),
            CandidateMatches AS (
                SELECT
                    c.user_id as candidate_id,
                    u.name as candidate_name,
                    u.email as candidate_email,
                    c.location as candidate_location,
                    c.years_of_experience,
                    j.location as job_location,
                    j.min_experience,
                    COUNT(DISTINCT CASE WHEN cs.skill_id IS NOT NULL THEN cs.skill_id END) as matching_skills_count,
                    -- Calculate skill score for candidate
                    SUM(CASE
                        WHEN cs.skill_id IS NOT NULL THEN cs.proficiency
                        ELSE 0
                    END) as raw_skill_score,
                    (SELECT total_skills FROM JobSkillsCount) as total_job_skills
                FROM candidates c
                JOIN users u ON c.user_id = u.user_id
                JOIN jobs j ON j.job_id = :jobId
                JOIN job_skill js ON j.job_id = js.job_id
                LEFT JOIN candidate_skill cs ON c.user_id = cs.candidate_id AND js.skill_id = cs.skill_id
                GROUP BY c.user_id, u.name, c.location, c.years_of_experience, j.location, j.min_experience
            )
            SELECT
                cm.candidate_id as candidateId,
                cm.candidate_name as candidateName,
                cm.candidate_email as candidateEmail,
                cm.candidate_location as candidateLocation,
                cm.years_of_experience as yearsOfExperience,
                cm.matching_skills_count as matchingSkillsCount,
                cm.total_job_skills as totalJobSkills,
                -- Calculate skill match percentage
                (cm.matching_skills_count * 100.0 / NULLIF(cm.total_job_skills, 0)) as skillsMatchPercentage,
                -- Skill score - max 60 points based on matched skills and proficiency
                -- First calculate the match percentage, then scale to 60 points max
                -- Adjust based on proficiency levels relative to maximum possible proficiency
                (cm.raw_skill_score * 60.0) / (cm.total_job_skills * 5) as skillScore,
                -- Location bonus: 20 if locations match, 0 otherwise
                CASE
                    WHEN cm.candidate_location = cm.job_location THEN 20
                    ELSE 0
                END as locationBonus,
                -- Experience score: 20 if meets requirement, penalty of 5 points per year lacking
                CASE
                    WHEN cm.years_of_experience >= cm.min_experience THEN 20
                    ELSE GREATEST(0, 20 - ((cm.min_experience - cm.years_of_experience) * 5))
                END as experiencePenalty,
                -- Total score calculation
                (cm.raw_skill_score * 60.0) / (cm.total_job_skills * 5) +
                CASE
                    WHEN cm.candidate_location = cm.job_location THEN 20
                    ELSE 0
                END +
                CASE
                    WHEN cm.years_of_experience >= cm.min_experience THEN 20
                    ELSE GREATEST(0, 20 - ((cm.min_experience - cm.years_of_experience) * 5))
                END as totalScore
            FROM CandidateMatches cm
            WHERE
                ((cm.raw_skill_score * 60.0) / (cm.total_job_skills * 5)) +
                    CASE
                        WHEN cm.candidate_location = cm.job_location THEN 20
                        ELSE 0
                    END +
                    CASE
                        WHEN cm.years_of_experience >= cm.min_experience THEN 20
                        ELSE GREATEST(0, 20 - ((cm.min_experience - cm.years_of_experience) * 5))
                    END
                >= 60
            ORDER BY totalScore DESC
        """;

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("jobId", jobId);
        
        List<Object[]> results = query.getResultList();
        List<CandidateMatchDto> matches = new ArrayList<>();
        
        for (Object[] result : results) {
            CandidateMatchDto match = new CandidateMatchDto();
            
            int i = -1;
            
            match.setCandidateId(((Number) result[++i]).longValue());
            match.setCandidateName((String) result[++i]);
            match.setCandidateEmail((String) result[++i]);
            match.setCandidateLocation((String) result[++i]);
            match.setYearsOfExperience(((Number) result[++i]).intValue());
            match.setMatchingSkillsCount(((Number) result[++i]).intValue());
            match.setTotalJobSkills(((Number) result[++i]).intValue());
            match.setSkillsMatchPercentage(((Number) result[++i]).doubleValue());
            match.setSkillScore(((Number) result[++i]).doubleValue());
            match.setLocationBonus(((Number) result[++i]).intValue());
            match.setExperiencePenalty(((Number) result[++i]).intValue());
            match.setTotalScore(((Number) result[++i]).doubleValue());
            matches.add(match);
        }
        
        return matches;
    }

}
