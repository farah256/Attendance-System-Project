package com.example.gestion_absences.controller;


import com.example.gestion_absences.DTO.UserSignupRequest;
import com.example.gestion_absences.model.AppUser;
import com.example.gestion_absences.repository.UserRepository;
import com.example.gestion_absences.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.gestion_absences.config.JwtTokenUtil;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class RegistrationController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;


    @PostMapping(value = "/req/signup", consumes = "application/json")
    public ResponseEntity<String> createUser(@RequestBody UserSignupRequest request){

        Optional<AppUser> existingAppUser = userRepository.findByEmail(request.getEmail());

        if(existingAppUser.isPresent()){
            if(existingAppUser.get().getEnabled()){
                return new ResponseEntity<>("Utilisateur Déjà existant et Vérifié.",HttpStatus.BAD_REQUEST);
            }else{
                String verificationToken = JwtTokenUtil.generateToken(existingAppUser.get().getEmail());
                existingAppUser.get().setVerificationToken(verificationToken);
                userRepository.save(existingAppUser.get());
                emailService.sendVerificationEmail(existingAppUser.get().getEmail(), verificationToken);
                return new ResponseEntity<>("Email de vérification renvoyé. Vérifiez votre boîte de réception ainsi que votre dossier Spam",HttpStatus.OK);
            }
        }

        AppUser appUser = new AppUser();
        appUser.setFirstName(request.getFirstName());
        appUser.setLastName(request.getLastName());
        appUser.setEmail(request.getEmail());
        appUser.setPassword(passwordEncoder.encode(request.getPassword()));
        String verificationToken =JwtTokenUtil.generateToken(appUser.getEmail());
        appUser.setVerificationToken(verificationToken);
        userRepository.save(appUser);
        emailService.sendVerificationEmail(appUser.getEmail(), verificationToken);

        return new ResponseEntity<>("Inscription réussie! Veuillez Vérifier votre boîte de réception ainsi que votre dossier Spam pour activer votre compte", HttpStatus.OK);
    }
}