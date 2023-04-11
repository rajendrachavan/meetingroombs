package neo.spring5.meetingroombooking.services;

import neo.spring5.meetingroombooking.models.ChangeRequest;
import neo.spring5.meetingroombooking.models.Role;
import neo.spring5.meetingroombooking.models.User;

import java.util.List;

public interface ChangeRequestService {
    <Optional> ChangeRequest findByUser(User user);
    List<ChangeRequest> findAllByUserRole(Role role);
    ChangeRequest save(ChangeRequest changeRequest);
    List<ChangeRequest> findAll();
    <Optional> ChangeRequest findById(Long id);
    void deleteById(Long id);
}
