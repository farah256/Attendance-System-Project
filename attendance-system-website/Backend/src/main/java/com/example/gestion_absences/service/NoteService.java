package com.example.gestion_absences.service;

import com.example.gestion_absences.model.Note;
import com.example.gestion_absences.repository.NoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NoteService {
    @Autowired
    private NoteRepository noteRepository;

    public List<Note> getAllNotes(String professeurId) {
        return noteRepository.findByProfesseurId(professeurId);
    }

    public Note createNote(Note note) {
        note.setCreatedAt(LocalDateTime.now());
        return noteRepository.save(note);
    }

    public void deleteNote(String id) {
        noteRepository.deleteById(id);
    }

    public Note updateNote(String id, Note note) {
        note.setId(id);
        return noteRepository.save(note);
    }
}

