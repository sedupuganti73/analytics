package com.bnsf.analytics.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bnsf.analytics.model.SFDCDataSource;

public interface ForceConnectionRepository extends JpaRepository<SFDCDataSource, Long> {

}
