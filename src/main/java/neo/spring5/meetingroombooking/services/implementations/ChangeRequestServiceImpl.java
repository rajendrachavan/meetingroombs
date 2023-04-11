package neo.spring5.meetingroombooking.services.implementations;

import neo.spring5.meetingroombooking.models.ChangeRequest;
import neo.spring5.meetingroombooking.models.Role;
import neo.spring5.meetingroombooking.models.User;
import neo.spring5.meetingroombooking.repositories.ChangeRequestRepository;
import neo.spring5.meetingroombooking.services.ChangeRequestService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChangeRequestServiceImpl implements ChangeRequestService {

    private final ChangeRequestRepository changeRequestRepository;

    public ChangeRequestServiceImpl(ChangeRequestRepository changeRequestRepository) {
        this.changeRequestRepository = changeRequestRepository;
    }

    @Override
    public <Optional> ChangeRequest findByUser(User user) {
        return changeRequestRepository.findByUser(user);
    }

    @Override
    public List<ChangeRequest> findAllByUserRole(Role role) {
        return changeRequestRepository.findAllByUserRole(role);
    }

    @Override
    public ChangeRequest save(ChangeRequest changeRequest) {
        return changeRequestRepository.save(changeRequest);
    }

    @Override
    public List<ChangeRequest> findAll() {
        return changeRequestRepository.findAll();
    }

    @Override
    public <Optional> ChangeRequest findById(Long id) {
        return changeRequestRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteById(Long id) {
        changeRequestRepository.deleteById(id);
    }
}
