package com.project.miniJobPortal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CandidateRegistrationDto {
    private String name;
    private String email;
    private String location;
    private Integer yearsOfExperience;

    private List<String> skills = new ArrayList<>();
    private List<Integer> proficiencyLevels = new ArrayList<>();
}
