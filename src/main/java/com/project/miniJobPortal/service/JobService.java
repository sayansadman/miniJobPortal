package com.project.miniJobPortal.service;

import com.project.miniJobPortal.dto.JobPostingDto;
import com.project.miniJobPortal.entity.Employer;
import com.project.miniJobPortal.entity.Job;
import com.project.miniJobPortal.entity.JobSkill;
import com.project.miniJobPortal.entity.Skill;
import com.project.miniJobPortal.repository.EmployerRepository;
import com.project.miniJobPortal.repository.JobRepository;
import com.project.miniJobPortal.repository.JobSkillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class JobService {

    private final JobRepository jobRepository;
    private final EmployerRepository employerRepository;
    private final JobSkillRepository jobSkillRepository;
    private final SkillService skillService;

    @Autowired
    public JobService(JobRepository jobRepository, EmployerRepository employerRepository, JobSkillRepository jobSkillRepository, SkillService skillService) {
        this.jobRepository = jobRepository;
        this.employerRepository = employerRepository;
        this.jobSkillRepository = jobSkillRepository;
        this.skillService = skillService;
    }

    public List<Job> getAllJobs() {
        return jobRepository.findAll();
    }

    @Transactional
    public Job postJob(JobPostingDto jobPostingDto) {

        //need to change this after adding user login
        Optional<Employer> employerOptional = employerRepository.findById(1L);

        if (employerOptional.isEmpty()) {
            throw new RuntimeException("Employer not found with ID: " + jobPostingDto.getEmployerId());
        }

        Employer employer = employerOptional.get();

        Job job = new Job();
        job.setTitle(jobPostingDto.getTitle());
        job.setLocation(jobPostingDto.getLocation());
        job.setMinExperience(jobPostingDto.getMinExperience());
        job.setCreatedAt(new Date());
        job.setEmployer(employer);

        Job savedJob = jobRepository.save(job);

        if (jobPostingDto.getSkills() != null && !jobPostingDto.getSkills().isEmpty()) {
            for (String skill : jobPostingDto.getSkills()) {
                if (skill != null && !skill.trim().isEmpty()) {
                    Skill skillObject = skillService.findOrCreateSkill(skill.trim().toLowerCase());

                    JobSkill jobSkill = new JobSkill();
                    jobSkill.setJob(savedJob);
                    jobSkill.setSkill(skillObject);

                    jobSkillRepository.save(jobSkill);
                }
            }

        }

        return savedJob;
    }
}
