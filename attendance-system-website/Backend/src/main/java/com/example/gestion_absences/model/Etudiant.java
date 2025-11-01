package com.example.gestion_absences.model;

import jakarta.persistence.PostLoad;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "etudiants")
public class Etudiant {
    @Id
    private String id;
    private String nom;
    private String code;
    private String prenom;
    private String classeId;
    private List<Abscence> absences = new ArrayList<>();

    // Supprimez @Transient pour permettre la sérialisation

    private Long nbAbsencesJustifiees;


    // Supprimez @Transient pour permettre la sérialisation
    private Long nbAbsencesNonJustifiees;

    public void calculateAbsences() {
        if (this.absences == null) {
            this.nbAbsencesJustifiees = 0L;
            this.nbAbsencesNonJustifiees = 0L;
            return;
        }

        this.nbAbsencesJustifiees = this.absences.stream()
                .filter(Abscence::isJustifiee)
                .count();

        this.nbAbsencesNonJustifiees = this.absences.size() - this.nbAbsencesJustifiees;
    }


}
