package com.project.miniJobPortal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployerRegistrationDto {
    private String name;
    private String email;
    private String companyName;
    private String industry;
}
