package com.example.gestion_absences.controller;


import com.example.gestion_absences.DTO.UserLoginRequest;
import com.example.gestion_absences.config.JwtTokenUtil;
import com.example.gestion_absences.model.AppUser;
import com.example.gestion_absences.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;

    @PostMapping("/req/login")
    public ResponseEntity<?> login(@RequestBody UserLoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(), request.getPassword()
                    )
            );

            AppUser user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (!user.getEnabled()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Veuillez vérifier votre email !");
            }

            String token = JwtTokenUtil.generateToken(user.getEmail());
            return ResponseEntity.ok().body(Map.of(
                    "token", token,
                    "id", user.getId()  // 👈 Ajoute l'ID de l'utilisateur ici
            ));

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email ou mot de passe incorrect");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Une erreur est survenue");
        }
    }
}

