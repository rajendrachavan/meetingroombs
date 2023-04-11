package neo.spring5.meetingroombooking.services;

import neo.spring5.meetingroombooking.models.BookingDetails;
import neo.spring5.meetingroombooking.models.Status;
import neo.spring5.meetingroombooking.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

public interface BookingService {

    void save(BookingDetails bookingDetails);
    List<BookingDetails> findAll();
    Optional<BookingDetails> findById(Long id);
    void deleteById(Long id);
    Page<BookingDetails> getPaginatedBookingDetails(Pageable pageable);
    Page<BookingDetails> getPaginatedBookingDetails(User user, Pageable pageable);
    List<BookingDetails> findAllByStatus(Status status);
    List<BookingDetails> findAllByUser(User user);
    List<BookingDetails> filterByMonth(YearMonth month, User user);

    Boolean isAvailable(BookingDetails bookingDetails);
}
