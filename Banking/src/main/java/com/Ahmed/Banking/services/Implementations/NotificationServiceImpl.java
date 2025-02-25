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
    private final WebSocketNotificationService webSocketNotificationService;


    /**
     * ✅ **Constructeur avec injection des dépendances**
     */
    public NotificationServiceImpl(NotificationRepository notificationRepository, UtilisateurRepository utilisateurRepository, WebSocketNotificationService webSocketNotificationService) {
        this.notificationRepository = notificationRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.webSocketNotificationService = webSocketNotificationService;
    }

    /**
     * ✅ **Créer et sauvegarder une notification**
     * - Crée une notification pour un utilisateur donné.
     * - Associe un type (`PRELEVEMENT`, `TRANSACTION_RECURRENTE`, etc.).
     * - Enregistre la date de création avec `LocalDateTime.now()`.
     */
    @Override
    public void creerNotification(Utilisateur utilisateur, NotificationType type, String message) {
        Notification notification = Notification.builder()
                .utilisateur(utilisateur)
                .type(type)
                .message(message)
                .dateCreation(LocalDateTime.now())  // ✅ Correction : Changement de `date` en `dateCreation`
                .build();

        // ✅ Envoi immédiat via WebSocket
        webSocketNotificationService.envoyerNotificationEnDirect(utilisateur, type, message);
        notificationRepository.save(notification);
    }

    /**
     * ✅ **Envoyer une notification à un utilisateur spécifique**
     * - Vérifie si l'utilisateur existe.
     * - Crée une nouvelle notification.
     * - Enregistre la notification dans la base de données.
     */
    @Override
    public void envoyerNotification(Integer utilisateurId, String message, NotificationType type) {
        Optional<Utilisateur> utilisateurOpt = utilisateurRepository.findById(utilisateurId);

        if (utilisateurOpt.isEmpty()) {
            throw new RuntimeException("❌ Utilisateur introuvable !");
        }

        Notification notification = Notification.builder()
                .utilisateur(utilisateurOpt.get())
                .message(message)
                .type(type)
                .dateCreation(LocalDateTime.now())  // ✅ Correction : Remplacement de `date` par `dateCreation`
                .build();

        notificationRepository.save(notification);
    }

    /**
     * ✅ **Récupérer les notifications d'un utilisateur**
     * - Vérifie que `findByUtilisateurId()` est bien défini dans `NotificationRepository`.
     * - Retourne la liste des notifications de l'utilisateur.
     */
    @Override
    public List<Notification> getNotificationsParUtilisateur(Integer utilisateurId) {
        return notificationRepository.findByUtilisateurId(utilisateurId); // ✅ Assurez-vous que cette méthode existe dans le repository
    }
    @Override
    public void marquerCommeLue(Integer notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("❌ Notification introuvable !"));
        notification.setLue(true);
        notificationRepository.save(notification);
    }

    @Override
    public void supprimerNotification(Integer notificationId) {
        notificationRepository.deleteById(notificationId);
    }
}
