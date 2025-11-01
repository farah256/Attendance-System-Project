package com.example.gestion_absences.controller;

import com.example.gestion_absences.model.Classe;
import com.example.gestion_absences.repository.ClasseRepository;
import com.example.gestion_absences.service.ClasseService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/classes")
public class ClasseController {


    @Autowired
    private ClasseService classeService;



    @GetMapping
    public List<Classe> getAllClasses() {
        return classeService.getAllClasses();
    }
    @GetMapping("/professeur/{profId}")
    public List<Classe> getClassesByProfesseur(@PathVariable String profId) {
        return classeService.getClassesByProfesseurId(profId);
    }


    @GetMapping("/{id}")
    public Classe getClasseById(@PathVariable String id) {
        return classeService.getClasseById(id);
    }

    @PostMapping
    public Classe addClasse(@RequestBody Classe classe) {
        return classeService.addClasse(classe);
    }

    @PutMapping("/{id}")
    public Classe updateClasse(@PathVariable String id, @RequestBody Classe classe) {
        return classeService.updateClasse(id, classe);
    }

    @DeleteMapping("/{id}")
    public void deleteClasse(@PathVariable String id) {
        classeService.deleteClasse(id);
    }
    @PatchMapping("/{id}")
    public Classe updateNomEtModule(@PathVariable String id, @RequestBody Map<String, String> updates) {
        Classe existingClasse = classeService.getClasseById(id);

        if (updates.containsKey("nom")) {
            existingClasse.setNom(updates.get("nom"));
        }
        if (updates.containsKey("module")) {
            existingClasse.setModule(updates.get("module"));
        }
        if (updates.containsKey("anneeScolaire")) {
            existingClasse.setAnneeScolaire(updates.get("anneeScolaire"));
        }

        return classeService.updateClasse(id, existingClasse);
    }


}
