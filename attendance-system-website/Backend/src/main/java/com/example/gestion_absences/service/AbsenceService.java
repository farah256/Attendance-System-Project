package com.example.gestion_absences.service;

import com.example.gestion_absences.model.Abscence;
import com.example.gestion_absences.model.Classe;
import com.example.gestion_absences.model.Etudiant;
import com.example.gestion_absences.repository.AbsenceRepository;
import com.example.gestion_absences.repository.ClasseRepository;
import com.example.gestion_absences.repository.EtudiantRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AbsenceService {
    @Autowired
    private EtudiantRepository etudiantRepository;
    @Autowired
    private AbsenceRepository absenceRepository;

    public List<Abscence> getAllAbsences() {
        return absenceRepository.findAll();
    }
    public List<Abscence> getAbsencesByEtudiantId(String etudiantId) {
        return absenceRepository.findByEtudiantId(etudiantId);
    }
    public Abscence getAbsenceById(String id) {
        return absenceRepository.findById(id).orElse(null);

    }
    public Abscence addAbsence(Abscence abscence) {
        return  absenceRepository.save(abscence);
    }
    public Abscence updateAbsence(String id, Abscence absence) {
        absence.setId(id);
        return absenceRepository.save(absence);
    }
    @Transactional

    public void deleteAbsence(String absenceId) {
        // 1. Récupérer l'absence
        Optional<Abscence> absenceOpt = absenceRepository.findById(absenceId);
        if (absenceOpt.isEmpty()) {
            throw new RuntimeException("Absence non trouvée avec ID: " + absenceId);
        }

        Abscence absence = absenceOpt.get();
        String etudiantId = absence.getEtudiantId();

        // 2. Supprimer l'absence de la collection d'absences
        absenceRepository.deleteById(absenceId);

        // 3. Récupérer l'étudiant et retirer l'absence de sa liste
        Optional<Etudiant> etudiantOpt = etudiantRepository.findById(etudiantId);
        if (etudiantOpt.isPresent()) {
            Etudiant etudiant = etudiantOpt.get();

            // Si la liste est de type List<Absence>, on supprime l'objet Absence
            List<Abscence> absences = etudiant.getAbsences();
            if (absences != null && !absences.isEmpty()) {
                absences.removeIf(a -> a.getId().equals(absenceId));
            }

            // Si la liste est de type List<String> (IDs des absences), on supprime l'ID
            // etudiant.getAbsences().remove(absenceId);
            etudiant.calculateAbsences();
            // Enregistrer l'étudiant mis à jour dans la base de données
            etudiantRepository.save(etudiant);
        }
    }

    @Transactional
    public Map<String, Object> marquerAbsence(String etudiantId) {
        // 1. Créer et sauvegarder l'absence
        Abscence absence = new Abscence();
        absence.setEtudiantId(etudiantId);
        absence.setDateAbsence(LocalDate.now());
        absence.setJustifiee(false);
        Abscence savedAbsence = absenceRepository.save(absence);

        // 2. Charger l'étudiant et mettre à jour
        Etudiant etudiant = etudiantRepository.findById(etudiantId)
                .orElseThrow(() -> new EntityNotFoundException("Étudiant non trouvé"));

        // Initialiser la liste si null
        if (etudiant.getAbsences() == null) {
            etudiant.setAbsences(new ArrayList<>());
        }

        // Ajouter la nouvelle absence
        etudiant.getAbsences().add(savedAbsence);

        // 3. Calculer les nouveaux totaux
        etudiant.calculateAbsences();
        etudiantRepository.save(etudiant);

        // 4. Retourner les résultats
        return Map.of(
                "absence", savedAbsence,
                "nbAbsencesJustifiees", etudiant.getNbAbsencesJustifiees(),
                "nbAbsencesNonJustifiees", etudiant.getNbAbsencesNonJustifiees()
        );
    }

    @Value("${app.upload.dir}")
    private String uploadDirectory;

    private String stockerFichier(MultipartFile fichier) throws IOException {
        // Créer le répertoire s'il n'existe pas
        Path uploadPath = Paths.get(uploadDirectory);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Générer un nom de fichier unique
        String nomFichier = UUID.randomUUID().toString() + "_" + fichier.getOriginalFilename();
        Path cheminFichier = uploadPath.resolve(nomFichier);

        // Sauvegarder le fichier
        Files.copy(fichier.getInputStream(), cheminFichier);

        return nomFichier;
    }

    @Transactional
    public Abscence justifierAbsence(String id, String motif, MultipartFile fichier) {
        Abscence absence = absenceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Absence non trouvée"));

        absence.setJustifiee(true);
        absence.setCommentaire(motif);

        // Traitement du fichier
        try {
            if (fichier != null && !fichier.isEmpty()) {
                String nomFichier = stockerFichier(fichier);
                absence.setFichierJustification(nomFichier);
            }
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors du stockage du fichier", e);
        }

        // Enregistrer l'absence mise à jour
        absenceRepository.save(absence);

        // Récupérer l'étudiant et mettre à jour la liste des absences
        Etudiant etudiant = etudiantRepository.findById(absence.getEtudiantId())
                .orElseThrow(() -> new EntityNotFoundException("Étudiant non trouvé"));

        // Supprimer l'ancienne absence (si elle existe) et ajouter l'absence mise à jour
        etudiant.getAbsences().removeIf(a -> a.getId().equals(absence.getId()));
        etudiant.getAbsences().add(absence);  // Ajouter l'absence justifiée

        // Recalculer les absences de l'étudiant
        etudiant.calculateAbsences();

        // Enregistrer l'étudiant avec la liste des absences mise à jour
        etudiantRepository.save(etudiant);

        return absence;
    }
    public List<Etudiant> getAlertesAbsences(int seuil, String classeId) {
        // Récupérer tous les étudiants avec leurs absences
        List<Etudiant> etudiants = etudiantRepository.findAll();

        // Filtrer selon les critères
        return etudiants.stream()
                .filter(e -> classeId == null || e.getClasseId().equals(classeId))
                .peek(Etudiant::calculateAbsences) // Calcule les compteurs
                .filter(e -> e.getNbAbsencesNonJustifiees() >= seuil)
                .sorted((e1, e2) -> e2.getNbAbsencesNonJustifiees().compareTo(e1.getNbAbsencesNonJustifiees()))
                .collect(Collectors.toList());
    }

}






