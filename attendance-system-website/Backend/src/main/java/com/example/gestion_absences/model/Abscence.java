package com.example.gestion_absences.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document(collection = "absences")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Abscence {
    @Id
    private String id;
    private String etudiantId;
    private LocalDate dateAbsence;
    private boolean justifiee;
    private String commentaire;
    private String fichierJustification;
}
