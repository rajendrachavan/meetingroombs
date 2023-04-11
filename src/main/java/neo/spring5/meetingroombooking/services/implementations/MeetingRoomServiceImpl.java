package neo.spring5.meetingroombooking.services.implementations;

import neo.spring5.meetingroombooking.models.BookingDetails;
import neo.spring5.meetingroombooking.models.MeetingRoom;
import neo.spring5.meetingroombooking.models.Status;
import neo.spring5.meetingroombooking.repositories.MeetingRoomRepository;
import neo.spring5.meetingroombooking.services.MeetingRoomService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MeetingRoomServiceImpl implements MeetingRoomService {

    private final MeetingRoomRepository meetingRoomRepository;

    public MeetingRoomServiceImpl(MeetingRoomRepository meetingRoomRepository) {
        this.meetingRoomRepository = meetingRoomRepository;
    }

    @Override
    public void save(MeetingRoom meetingRoom) {
        meetingRoomRepository.save(meetingRoom);
    }

    @Override
    public List<MeetingRoom> findAll() {
        List<MeetingRoom> meetingRooms = meetingRoomRepository.findAll();
        return meetingRooms;
    }

    @Override
    public Optional<MeetingRoom> findById(Long id) {
        Optional<MeetingRoom> meetingRoom = meetingRoomRepository.findById(id);
        return meetingRoom;
    }

    @Override
    public void deleteById(Long id) {
        meetingRoomRepository.deleteById(id);
    }

    @Override
    public MeetingRoom findMeetingRoomByName(String name) {
        return meetingRoomRepository.findMeetingRoomByName(name);
    }

    @Override
    public Page<MeetingRoom> getPaginatedMeetingRooms(Pageable pageable) {
        return meetingRoomRepository.findAll(pageable);
    }

    @Override
    public List<MeetingRoom> filterByDateAndTime(LocalDateTime startTime, LocalDateTime endTime) {

        List<MeetingRoom> meetingRooms = new ArrayList<>();
        for (MeetingRoom meetingRoom : meetingRoomRepository.findAll()) {
            boolean flag = true;
            if (meetingRoom.getBookingDetails().isEmpty()) meetingRooms.add(meetingRoom);
            else {
                for (BookingDetails bookingDetail : meetingRoom.getBookingDetails()) {
                    if (bookingDetail.getStatus() == Status.Confirmed) {
                        if((startTime.isAfter(bookingDetail.getStartTime()) || startTime.equals(bookingDetail.getStartTime()))
                        && startTime.isBefore(bookingDetail.getEndTime())){
                            flag = false; break;
                        }else if(startTime.isBefore(bookingDetail.getStartTime()) && endTime.isAfter(bookingDetail.getStartTime())){
                            flag = false; break;
                        }else { flag = true; break;}
                    } else { flag = true; break;}
                }
                if (flag)
                    meetingRooms.add(meetingRoom);
            }
        }
        return meetingRooms;
    }
}