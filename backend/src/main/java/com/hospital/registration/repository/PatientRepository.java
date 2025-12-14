package com.hospital.registration.repository;

import com.hospital.registration.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, String> {
    Optional<Patient> findByUserId(String userId);
    Optional<Patient> findByIdCard(String idCard);
    boolean existsByIdCard(String idCard);
}
