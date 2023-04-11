package neo.spring5.meetingroombooking.repositories;

import neo.spring5.meetingroombooking.models.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
