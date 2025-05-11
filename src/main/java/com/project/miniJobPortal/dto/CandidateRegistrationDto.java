package com.project.miniJobPortal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CandidateRegistrationDto {
    private String name;
    private String email;
    private String location;
    private Integer yearsOfExperience;
}
