package com.project.miniJobPortal.controller;

import com.project.miniJobPortal.dto.CandidateMatchDto;
import com.project.miniJobPortal.dto.JobListingDto;
import com.project.miniJobPortal.entity.Job;
import com.project.miniJobPortal.repository.JobRepository;
import com.project.miniJobPortal.service.JobService;
import com.project.miniJobPortal.service.MatchingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/matches")
public class MatchController {

    private final MatchingService matchingService;
    private final JobRepository jobRepository;
    private final JobService jobService;

    @Autowired
    public MatchController(MatchingService matchingService, JobRepository jobRepository, JobService jobService) {
        this.matchingService = matchingService;
        this.jobRepository = jobRepository;
        this.jobService = jobService;
    }

    @GetMapping
    public String getAllJobsWithMatches(Model model) {
        List<Job> jobs = jobService.getAllJobs();
        List<JobListingDto> jobListings = new ArrayList<>();
        Map<Long, Integer> candidateCounts = getCandidateCountsForJobs(jobs);
        
        for (Job job : jobs) {
            Integer matchedCandidatesCount = candidateCounts.getOrDefault(job.getJobId(), 0);
            jobListings.add(JobListingDto.builder()
                    .jobId(job.getJobId())
                    .title(job.getTitle())
                    .location(job.getLocation())
                    .minExperience(job.getMinExperience())
                    .createdAt(job.getCreatedAt())
                    .companyName(job.getEmployer().getCompanyName())
                    .industry(job.getEmployer().getIndustry())
                    .jobSkills(job.getJobSkills())
                    .matchedCandidatesCount(matchedCandidatesCount)
                    .build());
        }
        
        model.addAttribute("jobListings", jobListings);
        return "job-matches";
    }

    @GetMapping("/{jobId}")
    public String getJobWithMatches(@PathVariable Long jobId, Model model) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found with ID: " + jobId));
        
        List<CandidateMatchDto> candidateMatches = matchingService.getMatchesForJob(jobId);
        
        model.addAttribute("job", job);
        model.addAttribute("candidateMatches", candidateMatches);
        return "job-match-detail";
    }
    
    private Map<Long, Integer> getCandidateCountsForJobs(List<Job> jobs) {
        Map<Long, Integer> candidateCounts = new HashMap<>();
        
        for (Job job : jobs) {
            List<CandidateMatchDto> matches = matchingService.getMatchesForJob(job.getJobId());
            candidateCounts.put(job.getJobId(), matches.size());
        }
        
        return candidateCounts;
    }
}
