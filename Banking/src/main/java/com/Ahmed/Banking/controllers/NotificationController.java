package com.Ahmed.Banking.controllers;

import com.Ahmed.Banking.models.Notification;
import com.Ahmed.Banking.models.NotificationType;
import com.Ahmed.Banking.services.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")  // 🌍 Base URL pour les notifications
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    // ✅ 1️⃣ Récupérer les notifications d'un utilisateur
    @GetMapping("/{userId}")
    public ResponseEntity<List<Notification>> getNotificationsParUtilisateur(@PathVariable Integer userId) {
        List<Notification> notifications = notificationService.getNotificationsParUtilisateur(userId);
        return ResponseEntity.ok(notifications);
    }

    // ✅ 2️⃣ Marquer une notification comme lue
    @PutMapping("/{notificationId}/lue")
    public ResponseEntity<String> marquerNotificationCommeLue(@PathVariable Integer notificationId) {
        notificationService.marquerCommeLue(notificationId);
        return ResponseEntity.ok("✅ Notification marquée comme lue !");
    }

    // ✅ 3️⃣ Supprimer une notification
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<String> supprimerNotification(@PathVariable Integer notificationId) {
        notificationService.supprimerNotification(notificationId);
        return ResponseEntity.ok("🗑️ Notification supprimée !");
    }

    // ✅ 4️⃣ Envoyer une notification manuellement
    @PostMapping("/envoyer")
    public ResponseEntity<String> envoyerNotification(@RequestParam Integer userId,
                                                      @RequestParam String message,
                                                      @RequestParam NotificationType type) {
        notificationService.envoyerNotification(userId, message, type);
        return ResponseEntity.ok("📢 Notification envoyée !");
    }
}
