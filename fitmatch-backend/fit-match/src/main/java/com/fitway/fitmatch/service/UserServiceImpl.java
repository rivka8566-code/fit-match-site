package com.fitway.fitmatch.service;

import com.fitway.fitmatch.dto.LoginRequestDTO;
import com.fitway.fitmatch.dto.UserRegisterRequestDTO;
import com.fitway.fitmatch.dto.UserResponseDTO;
import com.fitway.fitmatch.entity.User;
import com.fitway.fitmatch.exception.UserAuthException;
import com.fitway.fitmatch.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;

import java.util.List;

import org.mindrot.jbcrypt.BCrypt; // ספריה להצפנת סיסמאות
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ModelMapper mapper;

    @Override
    public UserResponseDTO register(UserRegisterRequestDTO request) {
        // בדיקה האם האימייל כבר תפוס
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new UserAuthException("כתובת האימייל הזו כבר רשומה במערכת!");
        }

        // הצפנת הסיסמה (הפיכה ל-Hash מוצפן)
        String hashedPassword = BCrypt.hashpw(request.getPassword(), BCrypt.gensalt());

        User user = new User();
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setPassword(hashedPassword);
        user.setTotalCaloriesBurned(0);
        user.setRole(request.getRole() != null ? request.getRole() : com.fitway.fitmatch.entity.enums.UserRole.USER);

        User savedUser = userRepository.save(user);
        return mapper.map(savedUser, UserResponseDTO.class);
    }

    @Override
    public UserResponseDTO login(LoginRequestDTO request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserAuthException("שם המשתמש שגוי."));

        // בדיקה האם הסיסמה שהוקלדה תואמת ל-Hash המוצפן ב-DB
        if (!BCrypt.checkpw(request.getPassword(), user.getPassword())) {
            throw new UserAuthException("הסיסמה שגוייה.");
        }

        return mapper.map(user, UserResponseDTO.class);
    }

    @Override
    public UserResponseDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserAuthException("User not found"));
        return mapper.map(user, UserResponseDTO.class);
    }

    @Override
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> mapper.map(user, UserResponseDTO.class))
                .toList();
    }

    @Override
    @Transactional
    public UserResponseDTO addCaloriesToUser(Long id, int calories) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserAuthException("User not found"));
        
        user.setTotalCaloriesBurned(user.getTotalCaloriesBurned() + calories);
        User updatedUser = userRepository.save(user);
        return mapper.map(updatedUser, UserResponseDTO.class);
    }
}