package com.hospital.registration.service;

import com.hospital.registration.dto.CreateReviewDTO;
import com.hospital.registration.dto.DoctorReviewDTO;
import com.hospital.registration.entity.Doctor;
import com.hospital.registration.entity.DoctorReview;
import com.hospital.registration.entity.Patient;
import com.hospital.registration.exception.BusinessException;
import com.hospital.registration.repository.DoctorRepository;
import com.hospital.registration.repository.DoctorReviewRepository;
import com.hospital.registration.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class DoctorReviewService {
    
    private final DoctorReviewRepository doctorReviewRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    
    @Transactional
    public DoctorReviewDTO createReview(String userId, CreateReviewDTO dto) {
        // 获取患者信息
        Patient patient = patientRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException("患者信息不存在"));
        
        // 检查是否已经评价过
        if (doctorReviewRepository.existsByDoctorIdAndPatientId(dto.getDoctorId(), patient.getId())) {
            throw new BusinessException("您已经评价过该医生");
        }
        
        DoctorReview review = new DoctorReview();
        review.setDoctorId(dto.getDoctorId());
        review.setPatientId(patient.getId());
        review.setRating(dto.getRating());
        review.setContent(dto.getContent());
        
        review = doctorReviewRepository.save(review);
        log.info("创建评价成功 - reviewId: {}, doctorId: {}", review.getId(), dto.getDoctorId());
        
        return convertToDTO(review);
    }
    
    public Page<DoctorReviewDTO> getMyReviews(String userId, int page, int size) {
        Patient patient = patientRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException("患者信息不存在"));
        
        Pageable pageable = PageRequest.of(page, size);
        Page<DoctorReview> reviews = doctorReviewRepository.findByPatientIdOrderByCreatedAtDesc(patient.getId(), pageable);
        return reviews.map(this::convertToDTO);
    }
    
    @Transactional
    public void deleteReview(String userId, String reviewId) {
        Patient patient = patientRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException("患者信息不存在"));
        
        DoctorReview review = doctorReviewRepository.findById(reviewId)
                .orElseThrow(() -> new BusinessException("评价不存在"));
        
        if (!review.getPatientId().equals(patient.getId())) {
            throw new BusinessException("无权删除该评价");
        }
        
        doctorReviewRepository.delete(review);
    }
    
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
    
    /**
     * 医生端 - 获取针对自己的评价
     */
    public Page<DoctorReviewDTO> getDoctorReviewsByUserId(String userId, int page, int size, String filter) {
        Doctor doctor = doctorRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException("医生信息不存在"));
        
        Pageable pageable = PageRequest.of(page, size);
        Page<DoctorReview> reviews;
        
        if (filter != null) {
            switch (filter) {
                case "positive": // 4-5星好评
                    reviews = doctorReviewRepository.findByDoctorIdAndRatingGreaterThanEqualOrderByCreatedAtDesc(
                            doctor.getId(), 4, pageable);
                    break;
                case "neutral": // 3星
                    reviews = doctorReviewRepository.findByDoctorIdAndRatingOrderByCreatedAtDesc(
                            doctor.getId(), 3, pageable);
                    break;
                case "negative": // 1-2星
                    reviews = doctorReviewRepository.findByDoctorIdAndRatingLessThanOrderByCreatedAtDesc(
                            doctor.getId(), 3, pageable);
                    break;
                default:
                    reviews = doctorReviewRepository.findByDoctorIdOrderByCreatedAtDesc(doctor.getId(), pageable);
            }
        } else {
            reviews = doctorReviewRepository.findByDoctorIdOrderByCreatedAtDesc(doctor.getId(), pageable);
        }
        
        return reviews.map(this::convertToDTO);
    }
    
    /**
     * 医生端 - 获取评价统计
     */
    public Map<String, Object> getDoctorReviewStatsByUserId(String userId) {
        Doctor doctor = doctorRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException("医生信息不存在"));
        
        Map<String, Object> stats = new HashMap<>();
        
        Double avgRating = doctorReviewRepository.getAverageRatingByDoctorId(doctor.getId());
        Integer totalCount = doctorReviewRepository.getReviewCountByDoctorId(doctor.getId());
        
        stats.put("averageRating", avgRating != null ? avgRating : 5.0);
        stats.put("totalCount", totalCount != null ? totalCount : 0);
        
        // 获取各星级分布
        Map<Integer, Long> distribution = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            Long count = doctorReviewRepository.countByDoctorIdAndRating(doctor.getId(), i);
            distribution.put(i, count != null ? count : 0L);
        }
        stats.put("distribution", distribution);
        
        return stats;
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
