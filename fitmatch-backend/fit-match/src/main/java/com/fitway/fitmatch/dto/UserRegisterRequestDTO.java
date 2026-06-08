package com.fitway.fitmatch.dto;

import com.fitway.fitmatch.entity.enums.UserRole;
import lombok.Data;

@Data
public class UserRegisterRequestDTO {
    private String email;
    private String password;
    private String fullName;
    private UserRole role; // אופציונלי - ברירת מחדל USER
}
