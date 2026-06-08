package com.fitway.fitmatch.controller;

import com.fitway.fitmatch.dto.UserRegisterRequestDTO;
import com.fitway.fitmatch.dto.UserResponseDTO;
import com.fitway.fitmatch.dto.LoginRequestDTO; // DTO פשוט שמכיל שדות email ו-password
import com.fitway.fitmatch.service.UserService;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // מאפשר ל-React להתחבר ל-API בלי בעיות חסימה של דפדפן (CORS)
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@RequestBody UserRegisterRequestDTO request) {
        UserResponseDTO response = userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponseDTO> login(@RequestBody LoginRequestDTO request) {
        UserResponseDTO response = userService.login(request);
        return ResponseEntity.ok(response);
    }

    // 3. שליפת פרופיל משתמש (עבור ה-Dashboard בריאקט)
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserProfile(@PathVariable Long id) {
        UserResponseDTO response = userService.getUserById(id);
        return ResponseEntity.ok(response);
    }

    // 4. עדכון קלוריות בזמן אמת כשהמשתמש מסיים אימון!
    @PutMapping("/{id}/add-calories")
    public ResponseEntity<UserResponseDTO> updateBurnedCalories(
            @PathVariable Long id, 
            @RequestParam int calories) {
        UserResponseDTO updatedUser = userService.addCaloriesToUser(id, calories);
        return ResponseEntity.ok(updatedUser);
    }

    // 3. קבלת כל המשתמשים (פיצ'ר מנהל)
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

}