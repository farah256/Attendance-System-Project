package com.example.gestion_absences.model;


import org.springframework.data.mongodb.core.mapping.Document;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "classes")
public class Classe {

    @Id
    private String id;

    private String nom;
    private String anneeScolaire;
    private String module;
    private String professeurId; // id dyal professeur
    private List<String> etudiants = new ArrayList<>();  // Initialisez comme une liste vide

    public Classe(String nom, String anneeScolaire, String module, String professeurId, List<String> etudiants) {
        this.nom = nom;
        this.anneeScolaire = anneeScolaire;
        this.module = module;
        this.professeurId = professeurId;
        this.etudiants = etudiants;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getAnneeScolaire() {
        return anneeScolaire;
    }

    public void setAnneeScolaire(String anneeScolaire) {
        this.anneeScolaire = anneeScolaire;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getProfesseurId() {
        return professeurId;
    }

    public void setProfesseurId(String professeurId) {
        this.professeurId = professeurId;
    }

    public List<String> getEtudiants() {
        return etudiants;
    }

    public void setEtudiants(List<String> etudiants) {
        this.etudiants = etudiants;
    }
}
