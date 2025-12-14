package com.hospital.registration.service;

import com.hospital.registration.dto.DoctorReviewDTO;
import com.hospital.registration.entity.DoctorReview;
import com.hospital.registration.entity.Patient;
import com.hospital.registration.repository.DoctorReviewRepository;
import com.hospital.registration.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DoctorReviewService {
    
    private final DoctorReviewRepository doctorReviewRepository;
    private final PatientRepository patientRepository;
    
    public Page<DoctorReviewDTO> getDoctorReviews(String doctorId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<DoctorReview> reviews = doctorReviewRepository.findByDoctorIdOrderByCreatedAtDesc(doctorId, pageable);
        return reviews.map(this::convertToDTO);
    }
    
    public Double getAverageRating(String doctorId) {
        return doctorReviewRepository.getAverageRatingByDoctorId(doctorId);
    }
    
    public Integer getReviewCount(String doctorId) {
        return doctorReviewRepository.getReviewCountByDoctorId(doctorId);
    }
    
    private DoctorReviewDTO convertToDTO(DoctorReview review) {
        DoctorReviewDTO dto = new DoctorReviewDTO();
        dto.setId(review.getId());
        dto.setRating(review.getRating());
        dto.setContent(review.getContent());
        dto.setCreatedAt(review.getCreatedAt());
        
        // 获取患者姓名（脱敏处理）
        patientRepository.findById(review.getPatientId())
                .ifPresent(patient -> {
                    String name = patient.getName();
                    if (name != null && name.length() > 1) {
                        dto.setPatientName(name.charAt(0) + "**");
                    } else {
                        dto.setPatientName("匿名");
                    }
                });
        
        return dto;
    }
}
