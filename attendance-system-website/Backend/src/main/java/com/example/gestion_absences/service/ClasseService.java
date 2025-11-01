package com.example.gestion_absences.service;

import com.example.gestion_absences.model.Classe;
import com.example.gestion_absences.repository.ClasseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClasseService {

    @Autowired
    private ClasseRepository classeRepository;

    public List<Classe> getAllClasses() {
        return classeRepository.findAll();
    }

    public List<Classe> getClassesByProfesseurId(String profId) {
        return classeRepository.findByProfesseurId(profId);
    }


    public Classe getClasseById(String id) {
        return classeRepository.findById(id).orElse(null);

    }

    public Classe addClasse(Classe classe) {
        return  classeRepository.save(classe);
    }

    public Classe updateClasse(String id, Classe classe) {
        classe.setId(id);
        return classeRepository.save(classe);
    }

    public void deleteClasse(String id) {
        classeRepository.deleteById(id);
    }
}
