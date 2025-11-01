package com.example.gestion_absences.controller;


import com.example.gestion_absences.model.AppUser;
import com.example.gestion_absences.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import com.example.gestion_absences.config.JwtTokenUtil;

import java.util.Optional;

@RestController
@RequestMapping("/api")
public class VerificationController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenUtil jwtUtil;

    @GetMapping("/req/signup/verify")
    public ResponseEntity<String> verifyEmail(@RequestParam("token") String token) {
        System.out.println("Verification Token: " + token);

        String emailString = jwtUtil.extractEmail(token);
        Optional<AppUser> user = userRepository.findByEmail(emailString);
        if (user.isEmpty() || user.get().getVerificationToken() == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Lien invalide ou expiré.");
        }

        if (!jwtUtil.validateToken(token) || !user.get().getVerificationToken().equals(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Lien invalide ou expiré.");
        }
        user.get().setVerificationToken(null);
        user.get().setEnabled(true);
        userRepository.save(user.get());

        return ResponseEntity.status(HttpStatus.CREATED).body("Votre adresse e-mail a été vérifiée avec succès.");
    }

}

