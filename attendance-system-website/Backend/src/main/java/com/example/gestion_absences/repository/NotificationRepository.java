package com.example.gestion_absences.repository;

import com.example.gestion_absences.model.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface NotificationRepository extends MongoRepository<Notification, String> {
}
