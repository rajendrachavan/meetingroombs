package neo.spring5.meetingroombooking.controllers;

import neo.spring5.meetingroombooking.commons.MailUtils;
import neo.spring5.meetingroombooking.models.Feedback;
import neo.spring5.meetingroombooking.models.Role;
import neo.spring5.meetingroombooking.models.User;
import neo.spring5.meetingroombooking.repositories.DepartmentRepository;
import neo.spring5.meetingroombooking.repositories.FeedbackRepository;
import neo.spring5.meetingroombooking.repositories.RoleRepository;
import neo.spring5.meetingroombooking.services.ChangeRequestService;
import neo.spring5.meetingroombooking.services.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final RoleRepository roleRepository;
    private final ChangeRequestService changeRequestService;
    private final MailUtils mailUtils;
    private final DepartmentRepository departmentRepository;
    private final FeedbackRepository feedbackRepository;

    public AdminController(UserService userService, RoleRepository roleRepository,
                           ChangeRequestService changeRequestService, MailUtils mailUtils,
                           DepartmentRepository departmentRepository, FeedbackRepository feedbackRepository) {
        this.userService = userService;
        this.roleRepository = roleRepository;
        this.changeRequestService = changeRequestService;
        this.mailUtils = mailUtils;
        this.departmentRepository = departmentRepository;
        this.feedbackRepository = feedbackRepository;
    }

    //================================display all users==============================================
    @RequestMapping(value="/user-management/{page}", method = RequestMethod.GET)
    public ModelAndView userManagement(ModelAndView modelAndView,
                                       @PathVariable(value = "page") int page,
                                       @RequestParam(defaultValue = "id") String sortBy,
                                       @ModelAttribute("successMessage") String successMessage,
                                       @ModelAttribute("errorMessage") String errorMessage){

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(auth.getName());
        PageRequest pageable = PageRequest.of(page - 1, 5, Sort.Direction.DESC, sortBy);
        Page<User> userPage = userService.getPaginatedUsers(pageable);
        int totalPages = userPage.getTotalPages();
        if(totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1,totalPages).boxed().collect(Collectors.toList());
            modelAndView.addObject("pageNumbers", pageNumbers);
        }
        modelAndView.addObject("activeUserList", true);
        modelAndView.addObject("users", userPage.getContent());
        modelAndView.addObject("userName", "Welcome " + user.getFirstName() + " " + user.getLastName() + " (" + user.getEmail() + ")");
        modelAndView.addObject("successMessage", successMessage);
        modelAndView.addObject("errorMessage", errorMessage);
        modelAndView.setViewName("admin/user-management");
        return modelAndView;
    }

    //==================================update user=================================================
    @RequestMapping(value = "/update/{id}", method = RequestMethod.GET)
    public ModelAndView updatepage(ModelAndView modelAndView,
                                   @PathVariable(value="id") Long id) throws Exception {

        User user1 = userService.findById(id).orElse(null);
        if(user1 == null){
            System.out.println("User Not Found");
            modelAndView.addObject("successMessage","User Not Found");
            modelAndView.setViewName("admin/home");
            return modelAndView;
        }
        modelAndView.addObject("user", user1);
        modelAndView.addObject("id", id);
        modelAndView.setViewName("admin/update");
        return modelAndView;
    }

    //================================save updated user==============================================
    @RequestMapping(value = "/updateUser/{id}", method = RequestMethod.PUT)
    public ModelAndView editUser(ModelAndView modelAndView,
                                 @PathVariable(value="id") Long id,
                                 @Valid @ModelAttribute("user") User user,
                                 RedirectAttributes redirectAttributes){
        User userDataDB = userService.findById(id).orElse(null);
        user.setPassword(userDataDB.getPassword());
        userService.editSave(user);
        redirectAttributes.addFlashAttribute("successMessage", "User has been Updated successfully");
        modelAndView.setViewName("redirect:/admin/user-management/1");
        return modelAndView;
    }

    //==================================delete user=================================================
    @RequestMapping(value = "/delete/{id}")
    public ModelAndView delete(ModelAndView modelAndView,
                               @PathVariable Long id,
                               RedirectAttributes redirectAttributes){
        userService.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "User Deleted Successfully.");
        modelAndView.setViewName("redirect:/admin/user-management/1");
        return modelAndView;
    }

    //==================================register user=================================================
    @RequestMapping(value="/registration", method = RequestMethod.GET)
    public ModelAndView registration(ModelAndView modelAndView){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User userRole = userService.findUserByEmail(auth.getName());
        User user = new User();
        modelAndView.addObject("user", user);
        modelAndView.addObject("role", userRole.getRole().getRole());
        modelAndView.setViewName("admin/add-user");
        return modelAndView;
    }

    //==================================save registered user============================================
    @RequestMapping(value = "/registration", method = RequestMethod.POST)
    public ModelAndView createNewUser(@Valid User user,
                                      ModelAndView modelAndView) {
        User userExists = userService.findUserByEmail(user.getEmail());
        if (userExists != null) {
            modelAndView.addObject("errorMessage", "User Already Exists.");
            modelAndView.setViewName("registration");
        } else {
            userService.saveUser(user);
            modelAndView.addObject("successMessage", "User has been registered successfully");
            modelAndView.addObject("user", new User());
            modelAndView.setViewName("admin/add-user");
        }
        return modelAndView;
    }

    //===========================Assign - users ==============================================
    @RequestMapping("/assign-users")
    public ModelAndView assignUser(ModelAndView modelAndView){
        Role role = roleRepository.findByRole("USER").orElse(null);
        //Role role1 = roleRepository.findByRole("TL").orElse(null);
        List<User> users = new ArrayList<>();
        List<User> parents = new ArrayList<>();
        for ( User user : userService.findAllByRole(role)) {
            if(user.getParent() == null) users.add(user);
            for (User parent:user.getDepartment().getUsers()) {
                if(parent.getRole().getRole().equals("TL") && !parents.contains(parent)) parents.add(parent);
            }
        }
        modelAndView.addObject("users", users);
        modelAndView.addObject("parents", parents);
        modelAndView.addObject("temp",1);
        modelAndView.setViewName("admin/assign-users");
        return modelAndView;
    }

    @RequestMapping("/assign-users/TL")
    public ModelAndView assignTL(ModelAndView modelAndView){
        Role role = roleRepository.findByRole("TL").orElse(null);
        Role role1 = roleRepository.findByRole("PM").orElse(null);
        List<User> users = new ArrayList<>();
        for ( User user : userService.findAllByRole(role)) {
            if(user.getParent() == null) users.add(user);
        }
        modelAndView.addObject("users", users);
        modelAndView.addObject("parents", userService.findAllByRole(role1));
        modelAndView.setViewName("admin/assign-users");
        return modelAndView;
    }

    @PostMapping("/assign-users/{id}")
    public ModelAndView assignRoles(ModelAndView modelAndView,
                                    @RequestParam("parent_id") Long parent_id,
                                    @PathVariable("id") Long id,
                                    RedirectAttributes redirectAttributes){
        User user = userService.findById(id).orElse(null);
        user.setParent(userService.findById(parent_id).orElse(null));
        userService.editSave(user);
        redirectAttributes.addFlashAttribute("successMessage", "Operation successful.");
        modelAndView.setViewName("redirect:/admin/assign-users");
        return modelAndView;
    }

    @RequestMapping("/feedback")
    public ModelAndView getAllFeedback(ModelAndView modelAndView){
        modelAndView.addObject("feedbackAll", feedbackRepository.findAll());
        modelAndView.setViewName("admin/feedback");
        return modelAndView;
    }

    @RequestMapping("/emailFeedback/{id}")
    public ModelAndView sendReply(ModelAndView modelAndView,
                                  @RequestParam("email-content") String content,
                                  @PathVariable("id") Long id,
                                  RedirectAttributes redirectAttributes){
        Feedback feedback = feedbackRepository.findById(id).orElse(null);
        String subject = "Reply to Feedback";
        mailUtils.sendEmail(feedback.getUser().getEmail(), subject, content);
        redirectAttributes.addFlashAttribute("successMessage", "Feedback sent");
        modelAndView.setViewName("redirect:/admin/feedback");
        return modelAndView;
    }
}

