package com.virtualdoctor.virtual_doctor.repository;

import com.virtualdoctor.virtual_doctor.model.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {
    List<Session> findByUserIdOrderByCreatedAtDesc(Long userId);
}