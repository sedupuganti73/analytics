package com.bnsf.analytics.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bnsf.analytics.model.DataSource;

public interface DSRepository extends JpaRepository<DataSource, Long>{

}
