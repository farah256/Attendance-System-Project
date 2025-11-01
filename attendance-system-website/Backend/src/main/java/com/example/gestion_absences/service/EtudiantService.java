package com.example.gestion_absences.service;

import com.example.gestion_absences.model.Classe;
import com.example.gestion_absences.model.Etudiant;
import com.example.gestion_absences.repository.AbsenceRepository;
import com.example.gestion_absences.repository.ClasseRepository;
import com.example.gestion_absences.repository.EtudiantRepository;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

@Service
public class EtudiantService {

    @Autowired
    private EtudiantRepository etudiantRepository;
    @Autowired
    private AbsenceRepository absenceRepository;

    @Autowired
    private ClasseRepository classeRepository;

    public Etudiant ajouterEtudiant(Etudiant etudiant) {
        // Étape 1 : sauvegarder l'étudiant
        Etudiant savedEtudiant = etudiantRepository.save(etudiant);

        // Étape 2 : ajouter l'étudiant à la classe
        Optional<Classe> classeOpt = classeRepository.findById(etudiant.getClasseId());
        classeOpt.ifPresent(classe -> {
            classe.getEtudiants().add(savedEtudiant.getId());
            classeRepository.save(classe);
        });

        return savedEtudiant;
    }
    public void importerDepuisExcel(MultipartFile file, String classeId) throws Exception {
        Classe classe = classeRepository.findById(classeId)
                .orElseThrow(() -> new RuntimeException("Classe non trouvée"));

        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook = WorkbookFactory.create(inputStream);
            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) { // ignorer l’en-tête
                Row row = sheet.getRow(i);
                if (row == null) continue;

                String code = row.getCell(0).getStringCellValue();
                String nom = row.getCell(1).getStringCellValue();
                String prenom = row.getCell(2).getStringCellValue();

                Etudiant etudiant = new Etudiant();
                etudiant.setCode(code);
                etudiant.setNom(nom);
                etudiant.setPrenom(prenom);
                etudiant.setClasseId(classeId);

                Etudiant savedEtudiant = etudiantRepository.save(etudiant);

                // Mise à jour de la classe
                classe.getEtudiants().add(savedEtudiant.getId());
            }

            // Sauvegarder une seule fois la classe après la boucle
            classeRepository.save(classe);

        } catch (IOException e) {
            throw new RuntimeException("Erreur lecture fichier Excel", e);
        }
    }

    public void supprimerEtudiant(String id) {
        Etudiant etudiant = etudiantRepository.findById(id).orElse(null);
        if (etudiant != null) {
            // Supprimer toutes les absences associées à l'étudiant
            absenceRepository.deleteAllByEtudiantId(id); // Méthode à définir dans AbsenceRepository pour supprimer par ID d'étudiant

            // Supprimer l'étudiant de la base de données
            etudiantRepository.deleteById(id);

            // Supprimer l'ID de la liste des étudiants dans la classe
            Classe classe = classeRepository.findById(etudiant.getClasseId()).orElse(null);
            if (classe != null) {
                classe.getEtudiants().remove(id);
                classeRepository.save(classe);
            }
        }
    }

    public Etudiant getEtudiantById(String id) {
        return etudiantRepository.findById(id).orElse(null);

    }

}
