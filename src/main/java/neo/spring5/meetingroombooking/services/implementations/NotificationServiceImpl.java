package neo.spring5.meetingroombooking.services.implementations;

import neo.spring5.meetingroombooking.models.Notification;
import neo.spring5.meetingroombooking.models.Status;
import neo.spring5.meetingroombooking.repositories.NotificationRepository;
import neo.spring5.meetingroombooking.services.NotificationService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    public Notification save(Notification notification) {
        return notificationRepository.save(notification);
    }

    @Override
    public <Optional> Notification findById(Long id) {
        return notificationRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteExpiredNotifications() {
        LocalDateTime today = LocalDateTime.now();
        List<Notification> notifications = notificationRepository.findAll();
        for (Notification notification: notifications) {
            if(notification.getExpiryDate().isBefore(today) && notification.getStatus() == Status.Read)
                notificationRepository.delete(notification);
        }
    }
}
