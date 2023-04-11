package neo.spring5.meetingroombooking.controllers;

import neo.spring5.meetingroombooking.models.Facility;
import neo.spring5.meetingroombooking.services.FacilityService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class FacilityController {

    private final FacilityService facilityService;

    public FacilityController(FacilityService facilityService) {
        this.facilityService = facilityService;
    }

    @RequestMapping(value = "/view-facilities")
    public ModelAndView viewFacilities(ModelAndView modelAndView,
                                       @ModelAttribute("successMessage") String successMessage,
                                       @ModelAttribute("errorMessage") String errorMessage){
        modelAndView.addObject("facilities", facilityService.findAll());
        modelAndView.addObject("successMessage", successMessage);
        modelAndView.addObject("errorMessage", errorMessage);
        modelAndView.setViewName("admin/view-facilities");
        return modelAndView;
    }

    @RequestMapping(value = "/addFacility")
    public ModelAndView addFacility(ModelAndView modelAndView,
                                    RedirectAttributes redirectAttributes,
                                    @RequestParam("newFacility") String facility){
        Facility facility1 = facilityService.findByFacility(facility);
        if(facility1 != null){
            redirectAttributes.addFlashAttribute("errorMessage", "Facility already exists.");
            modelAndView.setViewName("redirect:/admin/view-facilities");
            return modelAndView;
        } else {
            Facility newFacility = new Facility();
            newFacility.setFacility(facility);
            facilityService.save(newFacility);
            redirectAttributes.addFlashAttribute("successMessage", "Facility Added");
            modelAndView.setViewName("redirect:/admin/view-facilities");
            return modelAndView;
        }
    }

    @RequestMapping(value = "/deleteFacility/{id}")
    public ModelAndView deleteFacility(ModelAndView modelAndView,
                                       RedirectAttributes redirectAttributes,
                                       @PathVariable("id") Long id){
        Facility facility = facilityService.findById(id).orElse(null);
        if(facility == null){
            redirectAttributes.addFlashAttribute("errorMessage", "Facility not found.");
            modelAndView.setViewName("redirect:/admin/view-facilities");
            return modelAndView;
        } else {
            facilityService.deleteById(facility.getId());
            redirectAttributes.addFlashAttribute("successMessage", "Facility deleted.");
            modelAndView.setViewName("redirect:/admin/view-facilities");
            return modelAndView;
        }
    }
}
