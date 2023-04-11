package neo.spring5.meetingroombooking.controllers;

import neo.spring5.meetingroombooking.commons.MailUtils;
import neo.spring5.meetingroombooking.models.*;
import neo.spring5.meetingroombooking.repositories.DepartmentRepository;
import neo.spring5.meetingroombooking.repositories.RoleRepository;
import neo.spring5.meetingroombooking.services.ChangeRequestService;
import neo.spring5.meetingroombooking.services.NotificationService;
import neo.spring5.meetingroombooking.services.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
public class ChangeRequestController {

    private final UserService userService;
    private final ChangeRequestService changeRequestService;
    private final RoleRepository roleRepository;
    private final MailUtils mailUtils;
    private final DepartmentRepository departmentRepository;
    private final NotificationService notificationService;

    public ChangeRequestController(UserService userService, ChangeRequestService changeRequestService,
                                   RoleRepository roleRepository,MailUtils mailUtils,
                                   DepartmentRepository departmentRepository, NotificationService notificationService) {
        this.userService = userService;
        this.changeRequestService = changeRequestService;
        this.roleRepository = roleRepository;
        this.mailUtils = mailUtils;
        this.departmentRepository = departmentRepository;
        this.notificationService = notificationService;
    }

    //===========================Display Change Requests=========================================
    @RequestMapping(value="/change-requests", method = RequestMethod.GET)
    public ModelAndView changeRequests(ModelAndView modelAndView){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(auth.getName());
        List<ChangeRequest> changeRequests = new ArrayList<>();
        Role role = null;
        switch (user.getRole().getRole()){
            case "ADMIN":
                role = roleRepository.findByRole("PM").orElse(null);
                for (ChangeRequest changeRequest: changeRequestService.findAll()) {
                    if(changeRequest.getUser().getParent() == null) changeRequests.add(changeRequest);
                }
                changeRequests.addAll(changeRequestService.findAllByUserRole(role));
                break;
            case "PM":
                role = roleRepository.findByRole("TL").orElse(null);
                changeRequests.addAll(changeRequestService.findAllByUserRole(role));
                break;
            case "TL":
                role = roleRepository.findByRole("USER").orElse(null);
                changeRequests.addAll(changeRequestService.findAllByUserRole(role));
                break;
        }

        modelAndView.addObject("role", user.getRole().getRole());
        if(changeRequests.isEmpty()) modelAndView.addObject("noRecords", "No Records Found!");
        else modelAndView.addObject("requests", changeRequests);
        modelAndView.setViewName("/change-requests");
        return modelAndView;
    }

    //===========================Confirm Changes=========================================
    @RequestMapping(value="/confirmChangeRequest/{id}", method = RequestMethod.POST)
    public ModelAndView confirmChangeRequest(ModelAndView modelAndView,
                                             @PathVariable("id") Long id){
        ChangeRequest changeRequest = changeRequestService.findById(id);
        User user = userService.findById(changeRequest.getUser().getId()).orElse(null);

        if(changeRequest.getType().equals(Type.Email_ChangeRequest)){
            user.setEmail(changeRequest.getNewValue());
            userService.editSave(user);

            String description = "Your Request for Change in Email Address is Confirmed!";
            Notification notification = new Notification(user, description,
                    Type.Email_ChangeRequest, Status.Unread, LocalDateTime.now().plusDays(2));
            notificationService.save(notification);

            String subject= "Email Change Request";
            String body = "Your Request for Change in Email Address is Confirmed!\n" +
                    "Your can now login with your new Email ID: "+changeRequest.getNewValue();
            mailUtils.sendEmail(changeRequest.getOldValue(), subject, body);
        } else {
            Department department = departmentRepository.findByName(changeRequest.getNewValue());
            user.setDepartment(department);
            userService.editSave(user);

            String description = "Your Request for Change in Department is Confirmed!";
            Notification notification = new Notification(user, description,
                    Type.Department_ChangeRequest, Status.Unread, LocalDateTime.now().plusDays(2));
            notificationService.save(notification);
        }
        changeRequest.setStatus(Status.Confirmed);
        changeRequestService.save(changeRequest);
        modelAndView.setViewName("redirect:/change-requests");
        return modelAndView;
    }

    //===========================Reject Changes=========================================
    @RequestMapping(value="/rejectChangeRequest/{id}", method = RequestMethod.POST)
    public ModelAndView rejectChangeRequest(ModelAndView modelAndView,
                                            @PathVariable("id") Long id){
        ChangeRequest changeRequest = changeRequestService.findById(id);
        User user = userService.findById(changeRequest.getUser().getId()).orElse(null);
        if(changeRequest.getType().equals(Type.Email_ChangeRequest)){
            user.setEmail(changeRequest.getOldValue());
            userService.editSave(user);

            String description = "Your Request for Change in Email Address is Rejected!";
            Notification notification = new Notification(user, description,
                    Type.Email_ChangeRequest, Status.Unread, LocalDateTime.now().plusDays(2));
            notificationService.save(notification);

            String subject= "Email Change Request";
            String body = "Your Request for Change in Email Address is Rejected!\n";
            mailUtils.sendEmail(changeRequest.getOldValue(), subject, body);
        } else {
            Department department = departmentRepository.findByName(changeRequest.getOldValue());
            user.setDepartment(department);
            userService.editSave(user);

            String description = "Your Request for Change in Department is Rejected!";
            Notification notification = new Notification(user, description,
                    Type.Department_ChangeRequest, Status.Unread, LocalDateTime.now().plusDays(2));
            notificationService.save(notification);
        }
        changeRequest.setStatus(Status.Rejected);
        changeRequestService.save(changeRequest);
        modelAndView.setViewName("redirect:/change-requests");
        return modelAndView;
    }
}
