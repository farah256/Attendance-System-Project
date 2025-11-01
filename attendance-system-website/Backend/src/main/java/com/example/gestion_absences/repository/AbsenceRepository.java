package com.example.gestion_absences.repository;

import com.example.gestion_absences.model.Abscence;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AbsenceRepository extends MongoRepository<Abscence, String> {
    List<Abscence> findByEtudiantIdIn(List<String> etudiantIds);
    List<Abscence> findByEtudiantId(String etudiantId);
    void deleteAllByEtudiantId(String etudiantId);



}
