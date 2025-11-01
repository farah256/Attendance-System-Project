package com.example.gestion_absences.controller;

import com.example.gestion_absences.model.Abscence;
import com.example.gestion_absences.model.Classe;
import com.example.gestion_absences.model.Etudiant;
import com.example.gestion_absences.repository.AbsenceRepository;
import com.example.gestion_absences.repository.ClasseRepository;
import com.example.gestion_absences.repository.EtudiantRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final ClasseRepository classeRepository;
    private final EtudiantRepository etudiantRepository;
    private final AbsenceRepository absenceRepository;

    @GetMapping
    public Map<String, Object> getDashboardStats(@RequestParam String professeurId) {
        Map<String, Object> stats = new HashMap<>();

        // Filtrer les classes du professeur
        List<Classe> classes = classeRepository.findByProfesseurId(professeurId);

        // Récupérer les étudiants de ces classes
        List<Etudiant> etudiants = etudiantRepository.findByClasseIdIn(
                classes.stream().map(Classe::getId).toList()
        );

        // Récupérer les absences de ces étudiants
        List<Abscence> absences = absenceRepository.findByEtudiantIdIn(
                etudiants.stream().map(Etudiant::getId).toList()
        );

        int totalClasses = classes.size();
        int totalEtudiants = etudiants.size();
        int totalAbsences = absences.size();
        int absencesJustifiees = (int) absences.stream().filter(Abscence::isJustifiee).count();

        // Étudiants ayant dépassé 3 absences
        Map<String, Long> absencesParEtudiant = new HashMap<>();
        for (Abscence a : absences) {
            absencesParEtudiant.put(a.getEtudiantId(),
                    absencesParEtudiant.getOrDefault(a.getEtudiantId(), 0L) + 1);
        }

        long alertes = absencesParEtudiant.values().stream().filter(count -> count > 3).count();

        stats.put("totalClasses", totalClasses);
        stats.put("totalEtudiants", totalEtudiants);
        stats.put("totalAbsences", totalAbsences);
        stats.put("absencesJustifiees", absencesJustifiees);
        stats.put("alertes", alertes);
        stats.put("etudiantsParClasse", totalClasses > 0 ? (double) totalEtudiants / totalClasses : 0.0);
        stats.put("pourcentageAbsencesJustifiees", totalAbsences > 0 ? (absencesJustifiees * 100.0) / totalAbsences : 0.0);

        return stats;
    }
}
