package com.example.attendanceproject.Model;

import java.util.ArrayList;
import java.util.List;

public class Etudiant {
    private String id;
    private String nom;
    private String code;
    private String prenom;
    private String classeId;
    private List<Abscence> absences = new ArrayList<>();


    public Etudiant(String nom, String code, String prenom, String classeId, List<Abscence> absences) {
        this.nom = nom;
        this.code = code;
        this.prenom = prenom;
        this.classeId = classeId;
        this.absences = absences;
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getClasseId() {
        return classeId;
    }

    public void setClasseId(String classeId) {
        this.classeId = classeId;
    }

    public List<Abscence> getAbsences() {
        return absences;
    }

    public void setAbsences(List<Abscence> absences) {
        this.absences = absences;
    }
}
