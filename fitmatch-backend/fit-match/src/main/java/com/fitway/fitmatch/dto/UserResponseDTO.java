package com.fitway.fitmatch.dto;

import lombok.Data;

@Data
public class UserResponseDTO {
    private Long id;
    private String email;
    private String fullName;
    private int totalCaloriesBurned;
}