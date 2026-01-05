package com.hospital.registration.repository;

import com.hospital.registration.entity.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HospitalRepository extends JpaRepository<Hospital, String> {
    Optional<Hospital> findByName(String name);
    List<Hospital> findByEnabledTrue();
    
    // 按地区查询
    List<Hospital> findByRegionAndEnabledTrue(String region);
    
    // 按城市查询
    List<Hospital> findByCityAndEnabledTrue(String city);
    
    // 按省份查询
    List<Hospital> findByProvinceAndEnabledTrue(String province);
    
    // 按地区和城市查询
    List<Hospital> findByRegionAndCityAndEnabledTrue(String region, String city);
    
    // 按区县查询
    List<Hospital> findByDistrictAndEnabledTrue(String district);
    
    // 按城市和区县查询
    List<Hospital> findByCityAndDistrictAndEnabledTrue(String city, String district);
    
    // 查询所有启用的医院，按城市排序
    List<Hospital> findByEnabledTrueOrderByCityAsc();
}
