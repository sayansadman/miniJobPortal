package com.project.miniJobPortal.service;

import com.project.miniJobPortal.entity.Skill;
import com.project.miniJobPortal.repository.SkillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SkillService {

    private final SkillRepository skillRepository;

    @Autowired
    public SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    public Skill findOrCreateSkill(String skillName) {
        Optional<Skill> existingSkill = skillRepository.findByName(skillName);
        if (existingSkill.isPresent()) {
            return existingSkill.get();
        } else {
            Skill newSkill = new Skill();
            newSkill.setName(skillName);
            return skillRepository.save(newSkill);
        }
    }
}
