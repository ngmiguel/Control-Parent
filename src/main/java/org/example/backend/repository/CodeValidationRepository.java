package org.example.backend.repository;

import org.example.backend.entity.CodeValidation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CodeValidationRepository extends JpaRepository<CodeValidation, Long> {
    Optional<CodeValidation> findByTelephoneAndCodeAndUtiliseFalse(String telephone, String code);
}
