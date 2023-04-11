package neo.spring5.meetingroombooking.bootstrap;

import neo.spring5.meetingroombooking.models.*;
import neo.spring5.meetingroombooking.repositories.*;
import neo.spring5.meetingroombooking.services.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//@Component
public class DataLoader implements CommandLineRunner {

    private final UserService userService;

    private final RoleRepository roleRepository;

    private final DepartmentRepository departmentRepository;
    private final FacilityRepository facilityRepository;
    private final UserRepository userRepository;

    private final MeetingRoomRepository meetingRoomRepository;

    public DataLoader(UserService userService, RoleRepository roleRepository, DepartmentRepository departmentRepository,
                      FacilityRepository facilityRepository,
                      UserRepository userRepository, MeetingRoomRepository meetingRoomRepository) {
        this.userService = userService;
        this.roleRepository = roleRepository;
        this.departmentRepository = departmentRepository;
        this.facilityRepository = facilityRepository;
        this.userRepository = userRepository;
        this.meetingRoomRepository = meetingRoomRepository;
    }

    @Override
    public void run(String... args) throws Exception {

        Department department = new Department("JAVA");
        departmentRepository.save(department);
        department = new Department("ANDROID");
        departmentRepository.save(department);
        department = new Department("IOS");
        departmentRepository.save(department);
        department = new Department("DEVSECOPS");
        departmentRepository.save(department);
        department = new Department("JAVASCRIPT");
        departmentRepository.save(department);
        department = new Department("TESTING");
        department = departmentRepository.save(department);

        Role role = new Role("USER");
        roleRepository.save(role);
        role = new Role("TL");
        roleRepository.save(role);
        role = new Role("PM");
        roleRepository.save(role);
        role = new Role("ADMIN");
        role = roleRepository.save(role);

        Facility facility1 = new Facility("AC");
        facilityRepository.save(facility1);
        Facility facility2 = new Facility("TV Screen");
        facilityRepository.save(facility2);
        Facility facility3 = new Facility("Projector");
        facilityRepository.save(facility3);
        Facility facility4 = new Facility("Whiteboards");
        facilityRepository.save(facility4);
        Facility facility5 = new Facility("Refreshments");
        facilityRepository.save(facility5);
        Facility facility6 = new Facility("Speakers");
        facilityRepository.save(facility6);

        MeetingRoom meetingRoom = new MeetingRoom("A1001", "BLDG1", Collections.singletonList(facility1));
        meetingRoomRepository.save(meetingRoom);

        List<Facility> facilities = new ArrayList<>();
        facilities.add(facility1);
        facilities.add(facility2);
        facilities.add(facility6);

        meetingRoom = new MeetingRoom("A2001", "BLDG1", facilities);
        meetingRoomRepository.save(meetingRoom);

        facilities = new ArrayList<>();
        facilities.add(facility1);
        facilities.add(facility2);
        facilities.add(facility3);
        facilities.add(facility4);
        facilities.add(facility5);
        facilities.add(facility6);

        meetingRoom = new MeetingRoom("B3156", "BLDG2", facilities);
        meetingRoomRepository.save(meetingRoom);

        facilities = new ArrayList<>();
        facilities.add(facility1);
        facilities.add(facility2);
        facilities.add(facility3);
        facilities.add(facility4);
        facilities.add(facility6);

        meetingRoom = new MeetingRoom("B3725", "BLDG2", facilities);
        meetingRoomRepository.save(meetingRoom);

        facilities = new ArrayList<>();
        facilities.add(facility1);
        facilities.add(facility4);
        facilities.add(facility6);

        meetingRoom = new MeetingRoom("B7751", "BLDG3", facilities);
        meetingRoomRepository.save(meetingRoom);

        User user = new User("cpp.x21124213@gmail.com", new BCryptPasswordEncoder().encode("PLMOKNIJB0987"),
                "SCP", "Admin", "MALE", "0892379496", 1);
        userRepository.save(user);

        System.out.println("------------------Booking Details Added------------------");

    }
}
