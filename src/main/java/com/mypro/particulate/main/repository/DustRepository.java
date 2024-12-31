package com.mypro.particulate.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mypro.particulate.main.model.DustModel;

public interface DustRepository extends JpaRepository<DustModel, Long> {
    
}
