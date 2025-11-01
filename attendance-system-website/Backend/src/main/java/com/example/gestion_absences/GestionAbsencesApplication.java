package com.example.gestion_absences;

import com.example.gestion_absences.model.Abscence;
import com.example.gestion_absences.model.Classe;
import com.example.gestion_absences.model.Etudiant;
import com.example.gestion_absences.model.Note;
import com.example.gestion_absences.repository.AbsenceRepository;
import com.example.gestion_absences.repository.ClasseRepository;
import com.example.gestion_absences.repository.EtudiantRepository;
import com.example.gestion_absences.repository.NoteRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;

@SpringBootApplication
public class GestionAbsencesApplication {

	public static void main(String[] args) {
		SpringApplication.run(GestionAbsencesApplication.class, args);
	}




}
