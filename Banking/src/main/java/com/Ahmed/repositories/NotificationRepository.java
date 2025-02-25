package com.Ahmed.repositories;

import com.Ahmed.Banking.models.Notification;
import com.Ahmed.Banking.models.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    List<Notification> findAllByUtilisateurOrderByDateCreationDesc(Utilisateur utilisateur);
    List<Notification> findByUtilisateurId(Integer utilisateurId);

}
