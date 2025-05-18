package com.project.miniJobPortal.controller;

import com.project.miniJobPortal.entity.Job;
import com.project.miniJobPortal.service.CandidateService;
import com.project.miniJobPortal.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class NavigationController {

    private final JobService jobService;
    private final CandidateService candidateService;

    @Autowired
    public NavigationController(JobService jobService, CandidateService candidateService) {
        this.jobService = jobService;
        this.candidateService = candidateService;
    }

    @GetMapping("/home")
    public String home(Model model) {


        int jobCount = jobService.getJobCount();
        int candidateCount = candidateService.getCandidateCount();

        model.addAttribute("jobCount", jobCount);
        model.addAttribute("candidateCount", candidateCount);

        return "home";
    }

    @GetMapping("/jobs-posted")
    public String showPostedJobs(
            Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "4") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Job> jobs = jobService.getPaginatedJobs(pageable);

        model.addAttribute("jobs", jobs);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", jobs.getTotalPages());
        model.addAttribute("pageSize", size);

        return "jobs-posted";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @GetMapping("/")
    public String index() {
        return "redirect:/home";
    }

}
