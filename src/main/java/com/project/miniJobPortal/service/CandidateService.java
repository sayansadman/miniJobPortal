package com.project.miniJobPortal.service;

import com.project.miniJobPortal.dto.CandidateRegistrationDto;
import com.project.miniJobPortal.entity.Candidate;
import com.project.miniJobPortal.entity.CandidateSkill;
import com.project.miniJobPortal.entity.Skill;
import com.project.miniJobPortal.entity.User;
import com.project.miniJobPortal.repository.CandidateRepository;
import com.project.miniJobPortal.repository.CandidateSkillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CandidateService {

    private final CandidateRepository candidateRepository;
    private final CandidateSkillRepository candidateSkillRepository;
    private final UserService userService;
    private final SkillService skillService;

    @Autowired
    public CandidateService(CandidateRepository candidateRepository, UserService userService, CandidateSkillRepository candidateSkillRepository, SkillService skillService) {
        this.candidateRepository = candidateRepository;
        this.candidateSkillRepository = candidateSkillRepository;
        this.userService = userService;
        this.skillService = skillService;
    }

    @Transactional
    public void registerCandidate(CandidateRegistrationDto candidateRegistrationDto) {

        User user = new User();
        user.setName(candidateRegistrationDto.getName());
        user.setEmail(candidateRegistrationDto.getEmail());
        user.setType("candidate");

        User savedUser = userService.saveUser(user);

        Candidate candidate = new Candidate();
        candidate.setUser(savedUser);
        candidate.setLocation(candidateRegistrationDto.getLocation());
        candidate.setYearsOfExperience(candidateRegistrationDto.getYearsOfExperience());

        Candidate savedCandidate = candidateRepository.save(candidate);

        candidateRepository.save(savedCandidate);

        if (candidateRegistrationDto.getSkills() != null && !candidateRegistrationDto.getSkills().isEmpty()) {
            for (int i = 0; i < candidateRegistrationDto.getSkills().size(); i++) {

                String skill = candidateRegistrationDto.getSkills().get(i);
                Integer proficiency = candidateRegistrationDto.getProficiencyLevels().get(i);

                if (skill != null && !skill.trim().isEmpty()) {
                    Skill skillObject = skillService.findOrCreateSkill(skill.trim().toLowerCase());

                    CandidateSkill candidateSkill = new CandidateSkill();
                    candidateSkill.setCandidate(savedCandidate);
                    candidateSkill.setSkill(skillObject);
                    candidateSkill.setProficiency(proficiency);

                    candidateSkillRepository.save(candidateSkill);
                }
            }
        }

    }
}
