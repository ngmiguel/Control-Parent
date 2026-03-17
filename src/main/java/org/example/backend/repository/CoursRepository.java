package org.example.backend.repository;

import org.example.backend.entity.Cours;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CoursRepository extends JpaRepository<Cours, Long> {
    
    List<Cours> findByClasseIdOrderByJourSemaineAscHeureDebutAsc(Long classeId);
}
