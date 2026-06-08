package com.fitway.fitmatch.service;

import java.util.List;

import com.fitway.fitmatch.dto.LoginRequestDTO;
import com.fitway.fitmatch.dto.UserRegisterRequestDTO;
import com.fitway.fitmatch.dto.UserResponseDTO;

public interface UserService {
    UserResponseDTO register(UserRegisterRequestDTO request);
    UserResponseDTO login(LoginRequestDTO request);
    UserResponseDTO getUserById(Long id); // שליפת פרופיל
    List<UserResponseDTO> getAllUsers(); // פיצ'ר אדמין
    UserResponseDTO addCaloriesToUser(Long id, int calories); // סגירת אימון
}