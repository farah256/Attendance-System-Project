package com.example.gestion_absences.controller;

import com.example.gestion_absences.DTO.UserLoginRequest;
import com.example.gestion_absences.DTO.UserSignupRequest;
import com.example.gestion_absences.model.AppUser;
import com.example.gestion_absences.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserProfile(@PathVariable String userId) {
        Optional<AppUser> userOpt = userRepository.findById(userId);

        if (userOpt.isPresent()) {
            AppUser user = userOpt.get();
            return ResponseEntity.ok().body(Map.of(
                    "id", user.getId(),
                    "firstName", user.getFirstName(),
                    "lastName", user.getLastName(),
                    "email", user.getEmail()
            ));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
