package neo.spring5.meetingroombooking.services.implementations;

import java.util.*;

import neo.spring5.meetingroombooking.commons.MailUtils;
import neo.spring5.meetingroombooking.models.Role;
import neo.spring5.meetingroombooking.models.Token;
import neo.spring5.meetingroombooking.models.User;
import neo.spring5.meetingroombooking.repositories.RoleRepository;
import neo.spring5.meetingroombooking.repositories.TokenRepository;
import neo.spring5.meetingroombooking.repositories.UserRepository;
import neo.spring5.meetingroombooking.services.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service("userService")
public class UserServiceImpl implements UserService {

	@Value("${scp.url}")
	private String appUrl;
	private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final TokenRepository tokenRepository;

	private final MailUtils mailUtils;

	public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository,
						   BCryptPasswordEncoder bCryptPasswordEncoder,
						   TokenRepository tokenRepository, MailUtils mailUtils) {
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
		this.tokenRepository = tokenRepository;
		this.mailUtils = mailUtils;
	}

	@Override
	public User findUserByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	@Override
	public void saveUser(User user) {
		user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setActive(1);
        Role userRole = roleRepository.findByRole("USER").orElse(null);
        user.setRole(userRole);
		userRepository.save(user);

		Token token = new Token();
		token.setToken(UUID.randomUUID().toString());
		token.setUser(user);
		tokenRepository.save(token);

		String subject= "Email Verification";
		String body = "Verify your email id, click the link below:\n" +"<a href='"+ appUrl
				+ "/verifyEmail?token=" + token.getToken()+"'>Click here</a>";
		mailUtils.sendEmail(user.getEmail(), subject, body);
	}

	@Override
	public List<User> findAll() {
		return userRepository.findAll();
	}

	@Override
	public Optional<User> findById(Long id) {
		return userRepository.findById(id);
	}

	@Override
	public void deleteById(Long id) {
		userRepository.deleteById(id);
	}

	@Override
	public void editSave(User user) {
		userRepository.save(user);
	}

	@Override
	public Page<User> getPaginatedUsers(Pageable pageable) {
		return userRepository.findAll(pageable);
	}

	@Override
	public List<User> findAllByRole(Role role) {
		return userRepository.findAllByRole(role);
	}

	@Override
	public User getAdmin() {
		List<User> users = userRepository.findAllByRole(roleRepository.findByRole("ADMIN").orElse(null));
		return users.get(0);
	}

}
