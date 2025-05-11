package com.project.miniJobPortal.service;

import com.project.miniJobPortal.dto.CandidateRegistrationDto;
import com.project.miniJobPortal.entity.Candidate;
import com.project.miniJobPortal.entity.User;
import com.project.miniJobPortal.repository.CandidateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CandidateService {

    private final CandidateRepository candidateRepository;
    private final UserService userService;

    @Autowired
    public CandidateService(CandidateRepository candidateRepository, UserService userService) {
        this.candidateRepository = candidateRepository;
        this.userService = userService;
    }

    @Transactional
    public Candidate registerCandidate(CandidateRegistrationDto candidateRegistrationDto) {

        User user = new User();
        user.setName(candidateRegistrationDto.getName());
        user.setEmail(candidateRegistrationDto.getEmail());
        user.setType("candidate");

        User savedUser = userService.saveUser(user);

        Candidate candidate = new Candidate();
        candidate.setUser(savedUser);
        candidate.setLocation(candidateRegistrationDto.getLocation());
        candidate.setYearsOfExperience(candidateRegistrationDto.getYearsOfExperience());

        return candidateRepository.save(candidate);
    }
}
