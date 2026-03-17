package org.example.backend.repository;

import java.util.List;

import org.example.backend.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByTelephoneOrderByDateCreationDesc(String telephone);

    long countByTelephoneAndLuFalse(String telephone);

    @Modifying
    @Transactional
    @Query("UPDATE Notification n SET n.lu = true WHERE n.telephone = :telephone")
    void marquerToutesCommeLues(@Param("telephone") String telephone);
}
