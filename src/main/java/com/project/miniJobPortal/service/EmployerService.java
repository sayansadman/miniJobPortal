package com.project.miniJobPortal.service;

import com.project.miniJobPortal.dto.EmployerRegistrationDto;
import com.project.miniJobPortal.entity.Employer;
import com.project.miniJobPortal.entity.User;
import com.project.miniJobPortal.repository.EmployerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmployerService {

    private final EmployerRepository employerRepository;
    private final UserService userService;

    @Autowired
    public EmployerService(EmployerRepository employerRepository, UserService userService) {
        this.employerRepository = employerRepository;
        this.userService = userService;
    }

    @Transactional
    public void registerEmployer(EmployerRegistrationDto employerRegistrationDto) {

        User user = new User();
        user.setName(employerRegistrationDto.getName());
        user.setEmail(employerRegistrationDto.getEmail());
        user.setType("employer");

        userService.saveUser(user);

        Employer employer = new Employer();
        employer.setUser(user);
        employer.setCompanyName(employerRegistrationDto.getCompanyName());
        employer.setIndustry(employerRegistrationDto.getIndustry());

        employerRepository.save(employer);
    }
}
