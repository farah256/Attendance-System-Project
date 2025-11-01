package com.example.gestion_absences.service;

import com.example.gestion_absences.model.AppUser;
import com.example.gestion_absences.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.User;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Optional<AppUser> user = userRepository.findByEmail(email.toLowerCase());
        if (user.isPresent()) {
            var userObj = user.get();
            return User.builder()
                    .username(userObj.getEmail())
                    .password(userObj.getPassword())
                    .disabled(!Boolean.TRUE.equals(userObj.getEnabled())) // compte désactivé s'il n'est pas vérifié
                    .build();
        }else{
            throw new UsernameNotFoundException(email);
        }
    }
}
