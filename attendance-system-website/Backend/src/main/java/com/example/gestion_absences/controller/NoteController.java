package com.example.gestion_absences.controller;

import com.example.gestion_absences.model.Note;
import com.example.gestion_absences.repository.NoteRepository;
import com.example.gestion_absences.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/notes")
public class NoteController {

    private final NoteRepository noteRepository;

    @Autowired
    public NoteController(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    @GetMapping("/{professeurId}")
    public ResponseEntity<List<Note>> getNotesByProf(@PathVariable String professeurId) {
        List<Note> notes = noteRepository.findByProfesseurId(professeurId);
        return ResponseEntity.ok(notes);
    }
    @PostMapping
    public ResponseEntity<Note> createNote(@RequestBody Note note) {
        note.setCreatedAt(LocalDateTime.now()); // si jamais tu veux forcer la date
        Note savedNote = noteRepository.save(note);
        return ResponseEntity.ok(savedNote);
    }
    @PatchMapping("/{id}")
    public ResponseEntity<Note> updateNote(@PathVariable String id, @RequestBody Note updatedNote) {
        Optional<Note> existingNoteOpt = noteRepository.findById(id);
        if (existingNoteOpt.isPresent()) {
            Note existingNote = existingNoteOpt.get();
            existingNote.setTitle(updatedNote.getTitle());
            existingNote.setContenu(updatedNote.getContenu());
            Note savedNote = noteRepository.save(existingNote);
            return ResponseEntity.ok(savedNote);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNote(@PathVariable String id) {
        Optional<Note> existingNoteOpt = noteRepository.findById(id);
        if (existingNoteOpt.isPresent()) {
            noteRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }


}

