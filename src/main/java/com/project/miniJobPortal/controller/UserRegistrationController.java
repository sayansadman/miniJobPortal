package com.project.miniJobPortal.controller;

import com.project.miniJobPortal.dto.CandidateRegistrationDto;
import com.project.miniJobPortal.dto.EmployerRegistrationDto;
import com.project.miniJobPortal.service.CandidateService;
import com.project.miniJobPortal.service.EmployerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserRegistrationController {

    private final EmployerService employerService;
    private final CandidateService candidateService;

    @Autowired
    public UserRegistrationController(EmployerService employerService, CandidateService candidateService) {
        this.employerService = employerService;
        this.candidateService = candidateService;
    }

    @GetMapping("/register-employer")
    public String showEmployerRegistrationForm(Model model) {
        model.addAttribute("employerRegistrationDto", new EmployerRegistrationDto());
        return "register-employer";
    }

    @PostMapping("/register-employer")
    public String registerEmployer(
            @ModelAttribute("employerRegistrationDto") EmployerRegistrationDto employerRegistrationDto,
            RedirectAttributes redirectAttributes) {
        try {
            employerService.registerEmployer(employerRegistrationDto);
            redirectAttributes.addFlashAttribute("successMessage", "Employer registered successfully!");
            return "redirect:/jobs-posted";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error registering employer: " + e.getMessage());
            return "redirect:/register-employer";
        }
    }

    @GetMapping("/register-candidate")
    public String showCandidateRegistrationForm(Model model) {
        model.addAttribute("candidateRegistrationDto", new CandidateRegistrationDto());
        return "register-candidate";
    }

    @PostMapping("/register-candidate")
    public String registerCandidate(
            @ModelAttribute("candidateRegistrationDto") CandidateRegistrationDto candidateRegistrationDto,
            RedirectAttributes redirectAttributes) {
        try {
            candidateService.registerCandidate(candidateRegistrationDto);
            redirectAttributes.addFlashAttribute("successMessage", "Employer registered successfully!");
            return "redirect:/";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error registering candidate: " + e.getMessage());
            return "redirect:/register-candidate";
        }
    }
}
