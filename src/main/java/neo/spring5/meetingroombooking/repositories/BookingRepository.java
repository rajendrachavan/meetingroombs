package neo.spring5.meetingroombooking.repositories;

import neo.spring5.meetingroombooking.models.BookingDetails;
import neo.spring5.meetingroombooking.models.Status;
import neo.spring5.meetingroombooking.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<BookingDetails, Long> {
    List<BookingDetails> findAllByStatus(Status status);
    Page<BookingDetails> findAll(Pageable pageable);
    Page<BookingDetails> findAllByUser(User user, Pageable pageable);
    List<BookingDetails> findAllByUser(User user);
    List<BookingDetails> findAllByStartTime(LocalDateTime startTime);
}
