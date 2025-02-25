package com.Ahmed.Banking.services.Implementations;

import com.Ahmed.Banking.models.Notification;
import com.Ahmed.Banking.models.NotificationType;
import com.Ahmed.Banking.models.Utilisateur;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class WebSocketNotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public void envoyerNotificationEnDirect(Utilisateur utilisateur, NotificationType type, String message) {
        Notification notification = Notification.builder()
                .utilisateur(utilisateur)
                .type(type)
                .message(message)
                .dateCreation(LocalDateTime.now())
                .build();

        messagingTemplate.convertAndSend("/topic/notifications/" + utilisateur.getId(), notification);
    }
}
