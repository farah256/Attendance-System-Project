package com.example.gestion_absences.repository;

import com.example.gestion_absences.model.Classe;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ClasseRepository extends MongoRepository<Classe, String>{
    List<Classe> findByProfesseurId(String professeurId);


}
