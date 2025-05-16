package com.project.miniJobPortal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobPostingDto {

    private String title;
    private String location;
    private Integer minExperience;
    private Long employerId;

    private List<String> skills = new ArrayList<>();
}
