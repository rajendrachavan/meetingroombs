package neo.spring5.meetingroombooking.controllers;

import neo.spring5.meetingroombooking.models.*;
import neo.spring5.meetingroombooking.services.BookingService;
import neo.spring5.meetingroombooking.services.MeetingRoomService;
import neo.spring5.meetingroombooking.services.NotificationService;
import neo.spring5.meetingroombooking.services.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
public class BookingController {

    private final UserService userService;
    private final BookingService bookingService;
    private final MeetingRoomService meetingRoomService;
    private final NotificationService notificationService;

    public BookingController(UserService userService, BookingService bookingService,
                             MeetingRoomService meetingRoomService, NotificationService notificationService) {
        this.userService = userService;
        this.bookingService = bookingService;
        this.meetingRoomService = meetingRoomService;
        this.notificationService = notificationService;
    }

    //------------------------------------= ADMIN =-------------------------------------------------------------

    @RequestMapping(value = "/admin/booking-requests/{page}")
    public ModelAndView bookingRequests(ModelAndView modelAndView,
                                        @PathVariable(value = "page") int page,
                                        @RequestParam(defaultValue = "id") String sortBy,
                                        @ModelAttribute("successMessage") String successMessage,
                                        @ModelAttribute("errorMessage") String errorMessage){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(auth.getName());
        PageRequest pageable = PageRequest.of(page - 1, 5, Sort.Direction.DESC, sortBy);
        Page<BookingDetails> bookingPage = bookingService.getPaginatedBookingDetails(pageable);
        int totalPages = bookingPage.getTotalPages();
        if(totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1,totalPages).boxed().collect(Collectors.toList());
            modelAndView.addObject("pageNumbers", pageNumbers);
        }else {
            modelAndView.addObject("noRecords", "No Records found!!!");
        }
        modelAndView.addObject("role", user.getRole().getRole());
        modelAndView.addObject("activeBookingList", true);
        modelAndView.addObject("bookingDetails", bookingPage.getContent());
        modelAndView.addObject("successMessage", successMessage);
        modelAndView.addObject("errorMessage", errorMessage);
        modelAndView.setViewName("admin/booking-requests");
        return modelAndView;
    }

    //===========================Filter booking Requests with its status=======================================
    @RequestMapping("/admin/booking-requests/pending")
    public ModelAndView roomAllocationPending(ModelAndView modelAndView){
        if(bookingService.findAllByStatus(Status.Pending).isEmpty()) modelAndView.addObject("noRecords",
                "No Records found!!!");
        else modelAndView.addObject("bookingDetails", bookingService.findAllByStatus(Status.Pending));
        modelAndView.addObject("role", "ADMIN");
        modelAndView.setViewName("admin/booking-requests");
        return modelAndView;
    }

    @RequestMapping("/admin/booking-requests/confirmed")
    public ModelAndView roomAllocationConfirmed(ModelAndView modelAndView){
        if(bookingService.findAllByStatus(Status.Confirmed).isEmpty()) modelAndView.addObject("noRecords",
                "No Records found!!!");
        else modelAndView.addObject("bookingDetails", bookingService.findAllByStatus(Status.Confirmed));
        modelAndView.addObject("role", "ADMIN");
        modelAndView.setViewName("admin/booking-requests");
        return modelAndView;
    }

    @RequestMapping("/admin/booking-requests/rejected")
    public ModelAndView roomAllocationCancelled(ModelAndView modelAndView){
        if(bookingService.findAllByStatus(Status.Rejected).isEmpty()) modelAndView.addObject("noRecords",
                "No Records found!!!");
        else modelAndView.addObject("bookingDetails", bookingService.findAllByStatus(Status.Rejected));
        modelAndView.addObject("role", "ADMIN");
        modelAndView.setViewName("admin/booking-requests");
        return modelAndView;
    }

    //==================================confirm booking request==========================================
    @PostMapping(value = "/admin/confirmRequest/{id}")
    public ModelAndView confirmRequest(ModelAndView modelAndView,
                                       @PathVariable(value = "id") Long id,
                                       RedirectAttributes redirectAttributes){
        BookingDetails bookingDetails = bookingService.findById(id).orElse(null);
        if(!bookingService.isAvailable(bookingDetails)){
            redirectAttributes.addFlashAttribute("errorMessage", "This Room has already been booked.");
        }
        else{
            bookingDetails.setStatus(Status.Confirmed);
            bookingService.save(bookingDetails);
            String description = "Your Booking request for "+bookingDetails.getMeetingRoom().getName()
                    +" on "+bookingDetails.getStartTime()+" is Confirmed";
            Notification notification = new Notification(bookingDetails.getUser(), description,
                    Type.BookingRequest, Status.Unread, bookingDetails.getStartTime());
            notificationService.save(notification);
            redirectAttributes.addFlashAttribute("successMessage", "BookingDetails Confirmed.");
        }
        modelAndView.setViewName("redirect:/admin/booking-requests/1");
        return modelAndView;
    }

    //==================================reject booking request==========================================
    @PostMapping(value = "/admin/rejectRequest/{id}")
    public ModelAndView rejectRequest(ModelAndView modelAndView,
                                      @PathVariable(value = "id") Long id,
                                      RedirectAttributes redirectAttributes){

        BookingDetails bookingDetails = bookingService.findById(id).orElse(null);
        if(bookingDetails == null){
            redirectAttributes.addFlashAttribute("successMessage", "BookingDetails Not Found.");
            modelAndView.setViewName("redirect:/admin/booking-requests");
        }
        else{
            bookingDetails.setStatus(Status.Rejected);
            bookingService.save(bookingDetails);
            String description = "Your Booking request for "+bookingDetails.getMeetingRoom().getName()
                    +" on "+bookingDetails.getStartTime()+" is Rejected";
            Notification notification = new Notification(bookingDetails.getUser(), description,
                    Type.BookingRequest, Status.Unread, bookingDetails.getStartTime());
            notificationService.save(notification);
        }
        redirectAttributes.addFlashAttribute("successMessage", "BookingDetails Rejected.");
        modelAndView.setViewName("redirect:/admin/booking-requests/1");
        return modelAndView;
    }

    //==========================================================================================================
    //-----------------------------------= USER =----------------------------------------------------------------

    @RequestMapping("/user/booking-status/{page}")
    public ModelAndView bookingStatus(@PathVariable(value = "page") int page,
                                      @RequestParam(defaultValue = "id") String sortBy,
                                      @ModelAttribute("successMessage") String successMessage,
                                      @ModelAttribute("errorMessage") String errorMessage){
        ModelAndView modelAndView = new ModelAndView();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(auth.getName());

        modelAndView.addObject("bookingStatus","Booking status");
        PageRequest pageable = PageRequest.of(page - 1, 5, Sort.Direction.DESC, sortBy);
        Page<BookingDetails> bookingDetailsPage = bookingService.getPaginatedBookingDetails(user, pageable);

        int totalPages = bookingDetailsPage.getTotalPages();
        if(totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1,totalPages).boxed().collect(Collectors.toList());
            modelAndView.addObject("pageNumbers", pageNumbers);
        }
        else {
            modelAndView.addObject("noRecords", "No Records found!!!");
        }
        modelAndView.addObject("role", user.getRole().getRole());
        modelAndView.addObject("temp", 1);
        modelAndView.addObject("activeBookingsList", true);
        modelAndView.addObject("bookingDetails", bookingDetailsPage.getContent());
        modelAndView.addObject("successMessage", successMessage);
        modelAndView.addObject("errorMessage", errorMessage);
        modelAndView.setViewName("user/booking-status");
        return modelAndView;
    }

    //======================Filter booking Requests with its status===================================
    @RequestMapping("/user/booking-status/pending")
    public ModelAndView pending(ModelAndView modelAndView){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(auth.getName());
        List<BookingDetails> bookingDetails = new ArrayList<>();
        for (BookingDetails bookingDetail: bookingService.findAllByUser(user)) {
            if(bookingDetail.getStatus() == Status.Pending){
                bookingDetails.add(bookingDetail);
            }
        }
        modelAndView.addObject("temp", 1);
        if(bookingDetails.isEmpty()) modelAndView.addObject("noRecords", "No Records found!!!");
        else modelAndView.addObject("bookingDetails", bookingDetails);
        modelAndView.setViewName("user/booking-status");
        return modelAndView;
    }

    @RequestMapping("/user/booking-status/confirmed")
    public ModelAndView confirmed(ModelAndView modelAndView){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(auth.getName());
        List<BookingDetails> bookingDetails = new ArrayList<>();
        for (BookingDetails bookingDetail: bookingService.findAllByUser(user)) {
            if(bookingDetail.getStatus() == Status.Confirmed){
                bookingDetails.add(bookingDetail);
            }
        }
        modelAndView.addObject("temp", 1);
        if(bookingDetails.isEmpty()) modelAndView.addObject("noRecords", "No Records found!!!");
        else modelAndView.addObject("bookingDetails", bookingDetails);
        modelAndView.setViewName("user/booking-status");
        return modelAndView;
    }

    @RequestMapping("/user/booking-status/rejected")
    public ModelAndView rejected(ModelAndView modelAndView){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(auth.getName());
        List<BookingDetails> bookingDetails = new ArrayList<>();
        for (BookingDetails bookingDetail: bookingService.findAllByUser(user)) {
            if(bookingDetail.getStatus() == Status.Rejected){
                bookingDetails.add(bookingDetail);
            }
        }
        modelAndView.addObject("temp", 1);
        if(bookingDetails.isEmpty()) modelAndView.addObject("noRecords", "No Records found!!!");
        else modelAndView.addObject("bookingDetails", bookingDetails);
        modelAndView.setViewName("user/booking-status");
        return modelAndView;
    }

    //============================Booking History===============================================
    @RequestMapping(value = "/booking-history", method = RequestMethod.GET)
    public ModelAndView bookingHistory(ModelAndView modelAndView){

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(auth.getName());
        List<BookingDetails> bookingDetails = new ArrayList<>();
        LocalDateTime today = LocalDateTime.now();
        for (BookingDetails bookingDetail: user.getBookingDetails()) {
            if(bookingDetail.getStartTime().isBefore(today)){
                bookingDetails.add(bookingDetail);
            }
        }
        modelAndView.addObject("temp", 0);
        if(bookingDetails.isEmpty()) modelAndView.addObject("noRecords", "No Records found!!!");
        else modelAndView.addObject("bookingDetails", bookingDetails);
        modelAndView.addObject("role", user.getRole().getRole());
        modelAndView.setViewName("user/booking-status");
        return modelAndView;
    }

    //============================================================================================
    @RequestMapping("/filter-booking-history")
    public ModelAndView filterBookings(ModelAndView modelAndView,
                                       @RequestParam("month") YearMonth month){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(auth.getName());
        modelAndView.addObject("temp", 0);
        modelAndView.addObject("role", user.getRole().getRole());
        List<BookingDetails> bookingDetails = bookingService.filterByMonth(month, user);
        if(bookingDetails.isEmpty()) modelAndView.addObject("noRecords", "No Records found!!!");
        else modelAndView.addObject("bookingDetails", bookingDetails);
        modelAndView.setViewName("user/booking-status");
        return modelAndView;
    }

    //============================Book a room===============================================
    @PostMapping("/bookRoom/{id}/{startTime}/{endTime}")
    public ModelAndView bookRoom(ModelAndView modelAndView, @PathVariable(value="id") Long id,
                                 @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") @PathVariable("startTime") LocalDateTime startTime,
                                 @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") @PathVariable("endTime") LocalDateTime endTime,
                                 RedirectAttributes redirectAttributes) throws Exception{

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(auth.getName());
        modelAndView.addObject("userName", "Welcome " + user.getFirstName() + " " + user.getLastName() + " (" + user.getEmail() + ")");
        modelAndView.addObject("role", auth.getName());

        BookingDetails bookingDetails = new BookingDetails();
        MeetingRoom meetingRoom = meetingRoomService.findById(id).orElse(null);
        bookingDetails.setStatus(Status.Pending);
        bookingDetails.setMeetingRoom(meetingRoom);
        bookingDetails.setUser(user);
        bookingDetails.setStartTime(startTime);
        bookingDetails.setEndTime(endTime);
        bookingService.save(bookingDetails);

        //sends notification to the admin
        String description = user.getFirstName()+" "+user.getLastName()+" has requested to book "+bookingDetails.getMeetingRoom().getName()
                +" on "+bookingDetails.getStartTime();
        Notification notification = new Notification(userService.getAdmin(), description,
                Type.BookingRequest, Status.Unread, bookingDetails.getStartTime());
        notificationService.save(notification);
      
        redirectAttributes.addFlashAttribute("successMessage", "Booking Request Sent");
        modelAndView.setViewName("redirect:/meeting-room-details/1");
        return modelAndView;
    }

    //================================Cancel Booking===============================================
    @RequestMapping(value = "/deleteRequest/{id}")
    public ModelAndView deleteRequest(ModelAndView modelAndView,
                                      @PathVariable(value="id") Long id,
                                      RedirectAttributes redirectAttributes){

        BookingDetails bookingDetails = bookingService.findById(id).orElse(null);
        if(bookingDetails == null){
            redirectAttributes.addFlashAttribute("successMessage", "BookingDetails Not Found.");
            modelAndView.setViewName("redirect:/user/booking-status/1");
        }
        else{
            bookingService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "BookingDetails Deleted Successfully.");
            modelAndView.setViewName("redirect:/user/booking-status/1");
        }
        return modelAndView;
    }
}