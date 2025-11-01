package com.example.gestion_absences.repository;

import com.example.gestion_absences.model.Note;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface NoteRepository extends MongoRepository<Note, String> {
    List<Note> findByProfesseurId(String professeurId);

}
