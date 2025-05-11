package com.project.miniJobPortal.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class NavigationController {

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/jobs-posted")
    public String postedJobs() {
        return "jobs-posted";
    }

    @GetMapping("/available-candidates")
    public String availableCandidates() {
        return "available-candidates";
    }

    @GetMapping("/post-new-job")
    public String postJob() {
        return "post-new-job";
    }

    @GetMapping("/add-candidate")
    public String addCandidate() {
        return "add-candidate";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

}
