package com.Ahmed.Banking.services;

import com.Ahmed.Banking.models.Notification;
import com.Ahmed.Banking.models.NotificationType;

import java.util.List;

public interface NotificationService {
    void envoyerNotification(Integer utilisateurId, String message, NotificationType type);
    List<Notification> getNotificationsParUtilisateur(Integer utilisateurId);
}
