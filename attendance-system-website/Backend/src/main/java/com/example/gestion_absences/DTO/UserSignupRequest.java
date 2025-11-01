package com.example.gestion_absences.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSignupRequest {

    private String firstName;
    private String lastName;
    private String email;
    private String password;
}
