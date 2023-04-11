package neo.spring5.meetingroombooking.services.implementations;

import neo.spring5.meetingroombooking.models.BookingDetails;
import neo.spring5.meetingroombooking.models.MeetingRoom;
import neo.spring5.meetingroombooking.models.Status;
import neo.spring5.meetingroombooking.models.User;
import neo.spring5.meetingroombooking.repositories.BookingRepository;
import neo.spring5.meetingroombooking.services.BookingService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;

    public BookingServiceImpl(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @Override
    public void save(BookingDetails bookingDetails) {
        bookingRepository.save(bookingDetails);
    }

    @Override
    public List<BookingDetails> findAll() {
        List<BookingDetails> bookingDetails = bookingRepository.findAll();
        return bookingDetails;
    }

    @Override
    public Optional<BookingDetails> findById(Long id) {
        Optional<BookingDetails> bookingDetails = bookingRepository.findById(id);
        return bookingDetails;
    }

    @Override
    public void deleteById(Long id) {
        bookingRepository.deleteById(id);
    }

    @Override
    public Page<BookingDetails> getPaginatedBookingDetails(Pageable pageable) {
        return bookingRepository.findAll(pageable);
    }

    @Override
    public Page<BookingDetails> getPaginatedBookingDetails(User user, Pageable pageable) {
        return bookingRepository.findAllByUser(user, pageable);
    }

    @Override
    public List<BookingDetails> findAllByStatus(Status status) {
        return bookingRepository.findAllByStatus(status);
    }

    @Override
    public List<BookingDetails> findAllByUser(User user) {
        return bookingRepository.findAllByUser(user);
    }

    @Override
    public List<BookingDetails> filterByMonth(YearMonth month, User user) {
        Month month1 = month.getMonth();
        List<BookingDetails> bookingDetailsList = new ArrayList<>();
        for (BookingDetails bookingDetails :bookingRepository.findAllByUser(user)) {
            if(bookingDetails.getStartTime().getYear() == month.getYear()){
                if(bookingDetails.getStartTime().getMonth() == (month1)){
                    bookingDetailsList.add(bookingDetails);
                }
            }
        }
        return bookingDetailsList;
    }

    @Override
    public Boolean isAvailable(BookingDetails bookingDetails) {
        boolean flag = true;
        MeetingRoom meetingRoom = bookingDetails.getMeetingRoom();
        LocalDateTime startTime = bookingDetails.getStartTime();
        LocalDateTime endTime = bookingDetails.getEndTime();

        for (BookingDetails bookingDetail : meetingRoom.getBookingDetails()) {
            if (bookingDetail.getStatus() == Status.Confirmed) {
                if((startTime.isAfter(bookingDetail.getStartTime()) || startTime.equals(bookingDetail.getStartTime()))
                        && startTime.isBefore(bookingDetail.getEndTime())){
                    flag = false; break;
                }else if(startTime.isBefore(bookingDetail.getStartTime()) && endTime.isAfter(bookingDetail.getStartTime())){
                    flag = false; break;
                }
            }
        }
        return flag;
    }
}
