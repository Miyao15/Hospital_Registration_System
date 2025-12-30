package com.hospital.registration.repository;

import com.hospital.registration.entity.DoctorReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DoctorReviewRepository extends JpaRepository<DoctorReview, String> {
    
    Page<DoctorReview> findByDoctorIdOrderByCreatedAtDesc(String doctorId, Pageable pageable);
    
    @Query("SELECT AVG(r.rating) FROM DoctorReview r WHERE r.doctorId = :doctorId")
    Double getAverageRatingByDoctorId(@Param("doctorId") String doctorId);
    
    @Query("SELECT COUNT(r) FROM DoctorReview r WHERE r.doctorId = :doctorId")
    Integer getReviewCountByDoctorId(@Param("doctorId") String doctorId);
    
    boolean existsByDoctorIdAndPatientId(String doctorId, String patientId);
    
    Page<DoctorReview> findByPatientIdOrderByCreatedAtDesc(String patientId, Pageable pageable);
    
    // 按星级筛选评价
    Page<DoctorReview> findByDoctorIdAndRatingGreaterThanEqualOrderByCreatedAtDesc(String doctorId, Integer rating, Pageable pageable);
    Page<DoctorReview> findByDoctorIdAndRatingOrderByCreatedAtDesc(String doctorId, Integer rating, Pageable pageable);
    Page<DoctorReview> findByDoctorIdAndRatingLessThanOrderByCreatedAtDesc(String doctorId, Integer rating, Pageable pageable);
    
    // 按星级计数
    @Query("SELECT COUNT(r) FROM DoctorReview r WHERE r.doctorId = :doctorId AND r.rating = :rating")
    Long countByDoctorIdAndRating(@Param("doctorId") String doctorId, @Param("rating") Integer rating);
}
