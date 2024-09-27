package com.project.vaadintest.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
public class Department {
    @Id
    @GeneratedValue
    private Long id;
    private String title;
    private String description;
}

