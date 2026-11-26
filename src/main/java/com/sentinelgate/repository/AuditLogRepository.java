package com.sentinelgate.repository;

import com.sentinelgate.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    Page<AuditLog> findByActorUsername(String actorUsername, Pageable pageable);

    Page<AuditLog> findByStatusCode(int statusCode, Pageable pageable);

    Page<AuditLog> findByAction(String action, Pageable pageable);

    @Query("SELECT a FROM AuditLog a WHERE " +
           "(:actorUsername IS NULL OR a.actorUsername = :actorUsername) AND " +
           "(:statusCode IS NULL OR a.statusCode = :statusCode) AND " +
           "(:action IS NULL OR a.action = :action)")
    Page<AuditLog> findFiltered(
            @Param("actorUsername") String actorUsername,
            @Param("statusCode") Integer statusCode,
            @Param("action") String action,
            Pageable pageable);
}
