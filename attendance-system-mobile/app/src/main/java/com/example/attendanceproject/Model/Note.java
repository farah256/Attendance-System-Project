package com.example.attendanceproject.Model;

import java.time.LocalDateTime;

public class Note {

    private String id;
    private String professeurId;
    private String contenu;
    private String title;
    private String createdAt ;

    public Note() {
    }

    public Note(String professeurId, String contenu, String title, String createdAt) {
        this.professeurId = professeurId;
        this.contenu = contenu;
        this.title = title;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProfesseurId() {
        return professeurId;
    }

    public void setProfesseurId(String professeurId) {
        this.professeurId = professeurId;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
