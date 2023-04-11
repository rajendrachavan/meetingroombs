package neo.spring5.meetingroombooking.repositories;

import neo.spring5.meetingroombooking.models.Role;
import neo.spring5.meetingroombooking.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("userRepository")
public interface UserRepository extends JpaRepository<User, Long> {
	User findByEmail(String email);

	Page<User> findAll(Pageable pageable);
	List<User> findAllByRole(Role role);
}
