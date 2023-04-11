package neo.spring5.meetingroombooking.controllers;

import javax.validation.Valid;

import neo.spring5.meetingroombooking.models.*;
import neo.spring5.meetingroombooking.repositories.DepartmentRepository;
import neo.spring5.meetingroombooking.repositories.RoleRepository;
import neo.spring5.meetingroombooking.repositories.TokenRepository;
import neo.spring5.meetingroombooking.services.NotificationService;
import neo.spring5.meetingroombooking.services.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;


@Controller
public class IndexController {
	
	private final UserService userService;
	private final RoleRepository roleRepository;
	private final DepartmentRepository departmentRepository;
	private final TokenRepository tokenRepository;
	private final NotificationService notificationService;

	public IndexController(UserService userService, RoleRepository roleRepository,
						   DepartmentRepository departmentRepository, TokenRepository tokenRepository,
						   NotificationService notificationService) {
		this.userService = userService;
		this.roleRepository = roleRepository;
		this.departmentRepository = departmentRepository;
		this.tokenRepository = tokenRepository;
		this.notificationService = notificationService;
	}


	@RequestMapping(value={"/", "/login"}, method = RequestMethod.GET)
	public ModelAndView login(ModelAndView modelAndView,
							  @ModelAttribute("successMessage") String successMessage,
							  @ModelAttribute("errorMessage") String errorMessage){
		modelAndView.addObject("successMessage", successMessage);
		modelAndView.addObject("errorMessage", errorMessage);
		modelAndView.setViewName("login");
		return modelAndView;
	}

	@RequestMapping(value="/registration", method = RequestMethod.GET)
	public ModelAndView registration(ModelAndView modelAndView){
		User user = new User();
		modelAndView.addObject("user", user);
		modelAndView.setViewName("registration");
		return modelAndView;
	}
	
	@RequestMapping(value = "/registration", method = RequestMethod.POST)
	public ModelAndView createNewUser(ModelAndView modelAndView,
									  @Valid User user, BindingResult bindingResult,
									  @RequestParam(name = "department") Long dept_id) {
		if(bindingResult.hasErrors()){
			modelAndView.addObject("errorMessage", "*Invalid Mobile Number");
			modelAndView.setViewName("registration");
			return modelAndView;
		}
		User userExists = userService.findUserByEmail(user.getEmail());
		if (userExists != null) {
			modelAndView.addObject("errorMessage", "User Already Exists.");
			modelAndView.setViewName("registration");
		} else {
			Department department = departmentRepository.findById(dept_id).orElse(null);
			user.setDepartment(department);
			userService.saveUser(user);
			modelAndView.addObject("successMessage", "User has been registered successfully, please check ' "+user.getEmail()+" ' to activate account' .");
			modelAndView.addObject("user", new User());
			modelAndView.setViewName("registration");
		}
		return modelAndView;
	}

	@RequestMapping(value = "/access-denied")
	public ModelAndView accessDenied(ModelAndView modelAndView){
		modelAndView.addObject("accessDeniedMessage", "Access Denied.");
		modelAndView.setViewName("access-denied");
		return modelAndView;
	}

	@RequestMapping(value = "/homepage", method = RequestMethod.GET)
	public ModelAndView homepage(ModelAndView modelAndView){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByEmail(auth.getName());
		modelAndView.addObject("id", user.getId());
		modelAndView.addObject("role", user.getRole().getRole());
		modelAndView.addObject("userName", "Welcome " + user.getFirstName() + " " + user.getLastName() + " (" + user.getEmail() + ")");
		notificationService.deleteExpiredNotifications();
		if(user.getNotifications().isEmpty()) modelAndView.addObject("noNotifications", "No Notifications");
		else modelAndView.addObject("notifications", user.getNotifications());
		modelAndView.setViewName("homepage");
		return modelAndView;
	}

	@RequestMapping(value = "/read/{id}")
	public ModelAndView changeNotificationStatus(ModelAndView modelAndView,
												 @PathVariable("id") Long id){

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByEmail(auth.getName());
		String role = user.getRole().getRole();
		Notification notification = notificationService.findById(id);

		if(notification.getType().equals(Type.BookingRequest)) {
			if(role.equals("ADMIN")) modelAndView.setViewName("redirect:/admin/booking-requests/1");
			else modelAndView.setViewName("redirect:/user/booking-status/1");
		}
		else if(notification.getType().equals(Type.Email_ChangeRequest) || notification.getType().equals(Type.Department_ChangeRequest)) {
			modelAndView.setViewName("redirect:/user/user-profile");
		}
		else if(notification.getType().equals(Type.Department_ChangeRequest_List) || notification.getType().equals(Type.Email_ChangeRequest_List)) {
			modelAndView.setViewName("redirect:/change-requests");
		}

		notification.setStatus(Status.Read);
		notificationService.save(notification);
		return modelAndView;
	}

	@RequestMapping(value = "/verifyEmail")
	public ModelAndView verifyEmail(ModelAndView modelAndView,
									@RequestParam("token") String token1){
		Token token = tokenRepository.findByToken(token1);
		User user = userService.findById(token.getUser().getId()).orElse(null);
		if(user.equals(null)){
			modelAndView.addObject("errorMessage", "Oops!  This is an invalid password reset link.");
			modelAndView.setViewName("login");
			return modelAndView;
		} else {
			user.setActive(1);
			userService.editSave(user);
			tokenRepository.delete(token);
			modelAndView.addObject("successMessage", "Email Verified Successfully, you can now login with your credentials");
			modelAndView.setViewName("login");
			return modelAndView;
		}
	}
}