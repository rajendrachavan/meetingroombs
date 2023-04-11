package neo.spring5.meetingroombooking.services;


import neo.spring5.meetingroombooking.models.Role;
import neo.spring5.meetingroombooking.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface UserService {

	User findUserByEmail(String email);
	void saveUser(User user);
	List<User> findAll();
	Optional<User> findById(Long id);
	void deleteById(Long id);

	void editSave(User user);
	Page<User> getPaginatedUsers(Pageable pageable);
	List<User> findAllByRole(Role role);

	User getAdmin();
}
