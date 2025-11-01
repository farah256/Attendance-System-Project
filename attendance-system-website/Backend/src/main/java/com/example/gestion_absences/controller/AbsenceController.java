package com.example.gestion_absences.controller;

import com.example.gestion_absences.model.Abscence;
import com.example.gestion_absences.model.Classe;
import com.example.gestion_absences.model.Etudiant;
import com.example.gestion_absences.repository.AbsenceRepository;
import com.example.gestion_absences.repository.EtudiantRepository;
import com.example.gestion_absences.service.AbsenceService;
import com.example.gestion_absences.service.ClasseService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/absences")
public class AbsenceController {
    @Autowired
    private AbsenceService absenceService;
    @Autowired
    private EtudiantRepository etudiantRepository;
    @Autowired
    private AbsenceRepository absenceRepository;
    @GetMapping
    public List<Abscence> getAllAbsences() {
        return absenceService.getAllAbsences();
    }
    @GetMapping("/etudiant/{etudiantId}")
    public List<Abscence> getAbsenceByEtudiant(@PathVariable String etudiantId) {
        return absenceService.getAbsencesByEtudiantId(etudiantId);
    }
    @GetMapping("/{id}")
    public Abscence getAbsenceById(@PathVariable String id) {
        return absenceService.getAbsenceById(id);
    }
    @PostMapping
    public Abscence addAbsence(@RequestBody Abscence absence) {
        return absenceService.addAbsence(absence);
    }
    @PutMapping("/{id}")
    public ResponseEntity<Abscence> updateAbsence(@PathVariable String id, @RequestBody Abscence updated) {
        Abscence absence = absenceRepository.findById(id).orElse(null);
        if (absence == null) return ResponseEntity.notFound().build();

        // On conserve l'id de l'étudiant
        String etudiantId = absence.getEtudiantId();

        // Mise à jour des champs
        absence.setDateAbsence(updated.getDateAbsence());
        absence.setJustifiee(updated.isJustifiee());
        absence.setCommentaire(updated.getCommentaire());

        // Sauvegarder l'absence
        Abscence savedAbsence = absenceRepository.save(absence);

        // Mettre à jour la liste dans l'objet Etudiant
        Etudiant etudiant = etudiantRepository.findById(etudiantId).orElse(null);
        if (etudiant != null) {
            List<Abscence> absences = etudiant.getAbsences();
            boolean updatedInList = false;

            for (int i = 0; i < absences.size(); i++) {
                if (absences.get(i).getId().equals(savedAbsence.getId())) {
                    absences.set(i, savedAbsence);
                    updatedInList = true;
                    break;
                }
            }

            // Si l'absence n'existait pas encore dans la liste (cas rare), on l'ajoute
            if (!updatedInList) {
                absences.add(savedAbsence);
            }

            etudiant.setAbsences(absences);
            etudiant.calculateAbsences();
            etudiantRepository.save(etudiant);
        }

        return ResponseEntity.ok(savedAbsence);
    }
    @GetMapping("/alertes")
    public ResponseEntity<List<Etudiant>> getAlertesAbsences(
            @RequestParam(defaultValue = "3") int seuil,
            @RequestParam(required = false) String classeId) {

        List<Etudiant> etudiants = absenceService.getAlertesAbsences(seuil, classeId);
        return ResponseEntity.ok(etudiants);
    }




    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAbsence(@PathVariable String id) {
        try {
            absenceService.deleteAbsence(id);
            return ResponseEntity.ok("Absence supprimée avec succès");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/etudiants/{etudiantId}")
    public ResponseEntity<Map<String, Object>> marquerAbsence(
            @PathVariable String etudiantId) {

        Map<String, Object> result = absenceService.marquerAbsence(etudiantId);
        return ResponseEntity.created(
                URI.create("/api/absences/" + ((Abscence)result.get("absence")).getId())
        ).body(result);
    }
    @PutMapping("/justifier/{id}")
    public ResponseEntity<?> justifierAbsence(
            @PathVariable String id,
            @RequestParam(required = false) String motif,
            @RequestParam(required = false) MultipartFile fichier) {

        try {
            Abscence absence = absenceService.justifierAbsence(id, motif, fichier);
            return ResponseEntity.ok(absence);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erreur lors du traitement du fichier");
        }

    }

}
