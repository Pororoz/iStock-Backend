package com.pororoz.istock.domain.log.repository;

import com.pororoz.istock.domain.log.entity.Log;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogRepository extends JpaRepository<Log, Long> {

}
