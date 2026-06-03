package com.fitway.fitmatch.dto;

import lombok.Data;

@Data
public class UserRegisterRequestDTO {
    private String email;
    private String password;
    private String fullName;
}