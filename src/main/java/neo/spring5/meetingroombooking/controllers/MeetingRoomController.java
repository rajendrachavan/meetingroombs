package neo.spring5.meetingroombooking.controllers;

import neo.spring5.meetingroombooking.models.Facility;
import neo.spring5.meetingroombooking.models.MeetingRoom;
import neo.spring5.meetingroombooking.models.User;
import neo.spring5.meetingroombooking.repositories.FacilityRepository;
import neo.spring5.meetingroombooking.services.MeetingRoomService;
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

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
public class MeetingRoomController {

    private final MeetingRoomService meetingRoomService;
    private final UserService userService;
    private final FacilityRepository facilityRepository;

    public MeetingRoomController(MeetingRoomService meetingRoomService, UserService userService,
                                 FacilityRepository facilityRepository) {
        this.meetingRoomService = meetingRoomService;
        this.userService = userService;
        this.facilityRepository = facilityRepository;
    }

    //----------------------------= COMMON =------------------------------------

    //==============================Display all Meeting Rooms with their details=============================
    @RequestMapping("/meeting-room-details/{page}")
    public ModelAndView meetingRoom(ModelAndView modelAndView,
                                    @PathVariable(value = "page") int page,
                                    @RequestParam(defaultValue = "id") String sortBy,
                                    @ModelAttribute("successMessage") String successMessage,
                                    @ModelAttribute("errorMessage") String errorMessage){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(auth.getName());
        modelAndView.addObject("role", user.getRole().getRole());
        modelAndView.addObject("userName", "Welcome " + user.getFirstName() + " " + user.getLastName() + " (" + user.getEmail() + ")");
        modelAndView.addObject("meetingRoomDetails", "Meeting-Room Details");

        PageRequest pageable = PageRequest.of(page - 1, 5, Sort.Direction.DESC, sortBy);
        Page<MeetingRoom> meetingRoomPage = meetingRoomService.getPaginatedMeetingRooms(pageable);
        int totalPages = meetingRoomPage.getTotalPages();
        if(totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1,totalPages).boxed().collect(Collectors.toList());
            modelAndView.addObject("pageNumbers", pageNumbers);
        }
        LocalDateTime Today = LocalDateTime.parse(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")));
        modelAndView.addObject("startTime", Today);
        modelAndView.addObject("endTime", Today);
        modelAndView.addObject("role", user.getRole().getRole());
        modelAndView.addObject("successMessage", successMessage);
        modelAndView.addObject("errorMessage", errorMessage);
        modelAndView.addObject("meetingRooms", meetingRoomPage.getContent());
        modelAndView.setViewName("meeting-room-details");
        return modelAndView;
    }

    //===========================Check Meeting Room Availability===========================================
    @RequestMapping("/filter-room-with-date")
    public ModelAndView filterRoom(ModelAndView modelAndView,
                                   @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") @RequestParam("startTime") LocalDateTime startTime,
                                   @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") @RequestParam("endTime") LocalDateTime endTime,
                                   RedirectAttributes redirectAttributes){

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(auth.getName());
        modelAndView.addObject("role", user.getRole().getRole());
        modelAndView.addObject("userName", "Welcome " + user.getFirstName() + " " + user.getLastName() + " (" + user.getEmail() + ")");

        LocalDateTime today = LocalDateTime.now();
        long minutes = ChronoUnit.MINUTES.between(startTime, endTime);

        if(startTime.isBefore(today)){
            redirectAttributes.addFlashAttribute("errorDate", "Enter a valid Date.");
            modelAndView.setViewName("redirect:/meeting-room-details/1");
        }else if(startTime.isAfter(endTime)){
            redirectAttributes.addFlashAttribute("errorDate", "Invalid start or end time");
            modelAndView.setViewName("redirect:/meeting-room-details/1");
        }else if(minutes<30){
            redirectAttributes.addFlashAttribute("errorDate", "You can't book a room for less than 30 minutes");
            modelAndView.setViewName("redirect:/meeting-room-details/1");
        }
        else {
            modelAndView.addObject("startTime", startTime);
            modelAndView.addObject("endTime", endTime);
            modelAndView.addObject("temp", 1);
            if(meetingRoomService.filterByDateAndTime(startTime, endTime).isEmpty())
                modelAndView.addObject("noRecords", "Sorry, No Meeting Rooms are available in given time.");
            else
                modelAndView.addObject("meetingRooms", meetingRoomService.filterByDateAndTime(startTime, endTime));
            modelAndView.setViewName("meeting-room-details");
        }
        return modelAndView;
    }
    //-------------------------------------------------------------------------------------------------

    //--------------------------------= ADMIN =---------------------------------------------

    //==========================Meeting Rooms CRUD operations===========================================
    @RequestMapping(value="/admin/add-room", method = RequestMethod.GET)
    public ModelAndView addRoom(ModelAndView modelAndView,
                                @ModelAttribute("successMessage") String successMessage,
                                @ModelAttribute("errorMessage") String errorMessage){
        MeetingRoom meetingRoom = new MeetingRoom();
        modelAndView.addObject("meetingRoom", meetingRoom);
        modelAndView.addObject("facilities", facilityRepository.findAll());
        modelAndView.setViewName("admin/add-room");
        return modelAndView;
    }

    @RequestMapping(value = "/admin/add-room", method = RequestMethod.POST)
    public ModelAndView createNewRoom(ModelAndView modelAndView,
                                      @Valid MeetingRoom meetingRoom,
                                      @RequestParam("facilities") Long[] id,
                                      RedirectAttributes redirectAttributes) {
        MeetingRoom meetingRoom1 = meetingRoomService.findMeetingRoomByName(meetingRoom.getName());
        if (meetingRoom1 != null) {
            modelAndView.addObject("successMessage","Room already exists!!!");
            modelAndView.setViewName("admin/add-room");
        } else {
            if(id != null){
                List<Facility> facilityList = new ArrayList<>();
                Facility facility;
                for (Long i : id){
                    facility = facilityRepository.findFacilityById(i);
                    if(facility != null){
                        facilityList.add(facility);
                    }
                }
                meetingRoom.setFacilities(facilityList);
            }
            meetingRoomService.save(meetingRoom);
            redirectAttributes.addFlashAttribute("successMessage", "Room has been added successfully");
            modelAndView.addObject("meetingRoom", new MeetingRoom());
            modelAndView.setViewName("redirect:/meeting-room-details/1");
        }
        return modelAndView;
    }

    @RequestMapping(value = "/admin/updateMeetingRoom/{id}", method = RequestMethod.GET)
    public ModelAndView updatepage(ModelAndView modelAndView,
                                   @PathVariable(value="id") Long id) throws Exception {
        MeetingRoom meetingRoom = meetingRoomService.findById(id).orElse(null);
        if(meetingRoom == null){
            System.out.println("MeetingRoom Not Found");
            modelAndView.addObject("successMessage","MeetingRoom Not Found");
            modelAndView.setViewName("admin/updateMeetingRoom");
            return modelAndView;
        }
        modelAndView.addObject("meetingRoom", meetingRoom);
        modelAndView.addObject("facilities", facilityRepository.findAll());
        modelAndView.addObject("id", id);
        modelAndView.setViewName("admin/updateMeetingRoom");
        return modelAndView;
    }

    @RequestMapping(value = "/admin/updateMeetingRoom/{id}", method = RequestMethod.PUT)
        public ModelAndView editUser(ModelAndView modelAndView,
                                     @PathVariable(value="id") Long id,
                                     @Valid @ModelAttribute("meetingRoom") MeetingRoom meetingRoomData,
                                     RedirectAttributes redirectAttributes){
        MeetingRoom meetingRoom = meetingRoomService.findById(id).orElse(null);
        meetingRoomService.save(meetingRoomData);
        redirectAttributes.addFlashAttribute("successMessage", "MeetingRoom has been Updated successfully");
        modelAndView.addObject("meetingRoom", meetingRoomData);
        modelAndView.setViewName("redirect:/meeting-room-details/1");
        return modelAndView;
    }

    @RequestMapping(value = "/admin/deleteMeetingRoom/{id}")
    public ModelAndView delete(ModelAndView modelAndView,
                               @PathVariable(value = "id") Long id,
                               RedirectAttributes redirectAttributes){
        meetingRoomService.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "MeetingRoom Deleted Successfully.");
        modelAndView.setViewName("redirect:/meeting-room-details/1");
        return modelAndView;
    }
    //======================================================================================================

    //-----------------------------------------------------------------------------------------------------
}