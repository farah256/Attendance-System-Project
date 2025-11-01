package com.example.gestion_absences.repository;

import com.example.gestion_absences.model.Etudiant;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface EtudiantRepository extends MongoRepository<Etudiant, String> {

    List<Etudiant> findByClasseIdIn(List<String> classeIds);
    List<Etudiant> findByClasseId(String classeId);


}
