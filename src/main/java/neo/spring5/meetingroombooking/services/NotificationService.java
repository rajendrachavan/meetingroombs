package neo.spring5.meetingroombooking.services;

import neo.spring5.meetingroombooking.models.Notification;

public interface NotificationService {
    Notification save(Notification notification);
    <Optional> Notification findById(Long id);
    void deleteExpiredNotifications();
}
