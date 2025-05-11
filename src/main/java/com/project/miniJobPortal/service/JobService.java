package com.project.miniJobPortal.service;

import com.project.miniJobPortal.dto.JobPostingDto;
import com.project.miniJobPortal.entity.Employer;
import com.project.miniJobPortal.entity.Job;
import com.project.miniJobPortal.entity.JobSkill;
import com.project.miniJobPortal.entity.Skill;
import com.project.miniJobPortal.repository.EmployerRepository;
import com.project.miniJobPortal.repository.JobRepository;
import com.project.miniJobPortal.repository.JobSkillRepository;
import com.project.miniJobPortal.repository.SkillRepository;
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
    private final SkillRepository skillRepository;
    private final JobSkillRepository jobSkillRepository;

    @Autowired
    public JobService(JobRepository jobRepository, EmployerRepository employerRepository, SkillRepository skillRepository, JobSkillRepository jobSkillRepository) {
        this.jobRepository = jobRepository;
        this.employerRepository = employerRepository;
        this.skillRepository = skillRepository;
        this.jobSkillRepository = jobSkillRepository;
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

        System.out.println("Skills: " + jobPostingDto.getSkills());
        System.out.println("Importance Levels: " + jobPostingDto.getImportanceLevels());

        if (jobPostingDto.getSkills() != null && !jobPostingDto.getSkills().isEmpty()) {
            for (int i = 0; i < jobPostingDto.getSkills().size(); i++) {
                String skill = jobPostingDto.getSkills().get(i);
                Integer importance = (i < jobPostingDto.getImportanceLevels().size())
                        ? jobPostingDto.getImportanceLevels().get(i)
                        : 3;

                System.out.println("Skill: " + skill);
                System.out.println("Importance: " + importance);

                if (skill != null && !skill.trim().isEmpty()) {
                    Skill skillObject = findOrCreateSkill(skill.trim().toLowerCase());

                    JobSkill jobSkill = new JobSkill();
                    jobSkill.setJob(savedJob);
                    jobSkill.setSkill(skillObject);
                    jobSkill.setImportance(importance);

                    jobSkillRepository.save(jobSkill);
                }
            }


        }

        return savedJob;
    }

    private Skill findOrCreateSkill(String skillName) {
        Optional<Skill> existingSkill = skillRepository.findByName(skillName);
        if (existingSkill.isPresent()) {
            return existingSkill.get();
        } else {
            Skill newSkill = new Skill();
            newSkill.setName(skillName);
            return skillRepository.save(newSkill);
        }
    }

    public List<Job> getAllJobs() {
        return jobRepository.findAll();
    }
}
