package com.hospital.registration.service;

import com.hospital.registration.entity.MedicalItem;
import com.hospital.registration.repository.MedicalItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MedicalItemService {

    @Autowired
    private MedicalItemRepository medicalItemRepository;

    public List<MedicalItem> getAllMedicalItems() {
        // 只返回启用的检查项目
        return medicalItemRepository.findByEnabledTrue();
    }

    public MedicalItem getMedicalItemById(String id) {
        return medicalItemRepository.findById(id).orElse(null);
    }

    public MedicalItem createMedicalItem(MedicalItem medicalItem) {
        return medicalItemRepository.save(medicalItem);
    }

    public MedicalItem updateMedicalItem(String id, MedicalItem updatedMedicalItem) {
        if (medicalItemRepository.existsById(id)) {
            updatedMedicalItem.setId(id);
            return medicalItemRepository.save(updatedMedicalItem);
        }
        return null;
    }

    public void deleteMedicalItem(String id) {
        medicalItemRepository.deleteById(id);
    }
}
