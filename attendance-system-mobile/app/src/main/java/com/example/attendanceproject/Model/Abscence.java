package com.example.attendanceproject.Model;

import java.time.LocalDate;

public class Abscence {
    private String id;
    private String etudiantId;
    private String dateAbsence;
    private boolean justifiee;
    private String commentaire;
    private String fichierJustification;

    public Abscence(String etudiantId, String dateAbsence, boolean justifiee, String commentaire, String fichierJustification) {
        this.etudiantId = etudiantId;
        this.dateAbsence = dateAbsence;
        this.justifiee = justifiee;
        this.commentaire = commentaire;
        this.fichierJustification = fichierJustification;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEtudiantId() {
        return etudiantId;
    }

    public void setEtudiantId(String etudiantId) {
        this.etudiantId = etudiantId;
    }

    public String getDateAbsence() {
        return dateAbsence;
    }

    public void setDateAbsence(String dateAbsence) {
        this.dateAbsence = dateAbsence;
    }

    public boolean isJustifiee() {
        return justifiee;
    }

    public void setJustifiee(boolean justifiee) {
        this.justifiee = justifiee;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public String getFichierJustification() {
        return fichierJustification;
    }

    public void setFichierJustification(String fichierJustification) {
        this.fichierJustification = fichierJustification;
    }
}
