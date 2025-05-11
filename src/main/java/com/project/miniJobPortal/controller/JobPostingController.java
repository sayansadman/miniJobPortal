package com.project.miniJobPortal.controller;

import com.project.miniJobPortal.dto.JobPostingDto;
import com.project.miniJobPortal.entity.Job;
import com.project.miniJobPortal.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class JobPostingController {

    private final JobService jobService;

    @Autowired
    public JobPostingController(JobService jobService) {
        this.jobService = jobService;
    }

    @GetMapping("/post-new-job")
    public String showPostJobForm(Model model) {
        model.addAttribute("jobPostingDto", new JobPostingDto());

        //To Do: add employer attribute to the model


        return "post-new-job";
    }

    @PostMapping("/post-new-job")
    public String postJob(
            @ModelAttribute("jobPostingDto") JobPostingDto jobPostingDto,
            RedirectAttributes redirectAttributes) {
        try {
            Job postedJob = jobService.postJob(jobPostingDto);
            redirectAttributes.addFlashAttribute(("successMessage"), "Job posted successfully! Job ID: " + postedJob.getJobId());
            return "redirect:/home";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to post job: " + e.getMessage());
            return "redirect:/post-new-job";
        }
    }

    @GetMapping("/jobs-posted")
    public String showPostedJobs(Model model) {
        List<Job> jobs = jobService.getAllJobs();
        model.addAttribute("jobs", jobs);
        return "jobs-posted";
    }

}
