package com.Ahmed.Banking.services.Implementations;

import com.Ahmed.Banking.models.Notification;
import com.Ahmed.Banking.models.NotificationType;
import com.Ahmed.Banking.models.Utilisateur;
import com.Ahmed.Banking.services.NotificationService;
import com.Ahmed.repositories.NotificationRepository;
import com.Ahmed.repositories.UtilisateurRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UtilisateurRepository utilisateurRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository, UtilisateurRepository utilisateurRepository) {
        this.notificationRepository = notificationRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    @Override
    public void envoyerNotification(Integer utilisateurId, String message, NotificationType type) {
        Optional<Utilisateur> utilisateurOpt = utilisateurRepository.findById(utilisateurId);
        if (utilisateurOpt.isEmpty()) {
            throw new RuntimeException("Utilisateur introuvable !");
        }

        Notification notification = Notification.builder()
                .utilisateur(utilisateurOpt.get())
                .message(message)
                .type(type)
                .date(LocalDateTime.now())
                .build();

        notificationRepository.save(notification);
    }

    @Override
    public List<Notification> getNotificationsParUtilisateur(Integer utilisateurId) {
        return notificationRepository.findByUtilisateurId(utilisateurId);
    }
}
