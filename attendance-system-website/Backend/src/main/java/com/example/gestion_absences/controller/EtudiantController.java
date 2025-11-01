package com.example.gestion_absences.controller;

import com.example.gestion_absences.model.Etudiant;
import com.example.gestion_absences.repository.EtudiantRepository;
import com.example.gestion_absences.service.EtudiantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/etudiants")
public class EtudiantController {

    @Autowired
    private EtudiantService etudiantService;
    @Autowired
    private EtudiantRepository etudiantRepository;

    @PostMapping
    public ResponseEntity<Etudiant> ajouterEtudiant(@RequestBody Etudiant etudiant) {
        Etudiant saved = etudiantService.ajouterEtudiant(etudiant);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @GetMapping("/classe/{classeId}")
    public List<Etudiant> getEtudiantsByClasse(@PathVariable String classeId) {
        return etudiantRepository.findByClasseId(classeId);

    }
    @PostMapping("/importer")
    public ResponseEntity<String> importerEtudiantsDepuisExcel(@RequestParam("file") MultipartFile file,
                                                               @RequestParam("classeId") String classeId) {
        try {
            etudiantService.importerDepuisExcel(file,classeId);
            return ResponseEntity.ok("Étudiants importés avec succès !");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de l'importation : " + e.getMessage());
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerEtudiant(@PathVariable String id) {
        etudiantService.supprimerEtudiant(id);
        return ResponseEntity.noContent().build();
    }
    @PutMapping("/{id}")
    public ResponseEntity<Etudiant> updateEtudiant(@PathVariable String id, @RequestBody Etudiant updated) {
        Etudiant etudiant = etudiantRepository.findById(id).orElse(null);
        if (etudiant == null) return ResponseEntity.notFound().build();

        etudiant.setCode(updated.getCode());
        etudiant.setNom(updated.getNom());
        etudiant.setPrenom(updated.getPrenom());

        etudiantRepository.save(etudiant);
        return ResponseEntity.ok(etudiant);
    }
    @GetMapping("/{id}")
    public Etudiant getEtudiantById(@PathVariable String id) {
        return etudiantService.getEtudiantById(id);
    }



}
