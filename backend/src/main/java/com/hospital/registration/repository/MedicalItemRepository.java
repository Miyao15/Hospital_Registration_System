package com.hospital.registration.repository;

import com.hospital.registration.entity.MedicalItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicalItemRepository extends JpaRepository<MedicalItem, String> {
    List<MedicalItem> findByEnabledTrue();
}
