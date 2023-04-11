package neo.spring5.meetingroombooking.repositories;

import neo.spring5.meetingroombooking.models.MeetingRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MeetingRoomRepository extends JpaRepository<MeetingRoom, Long> {

    MeetingRoom findMeetingRoomByName(String name);
    Page<MeetingRoom> findAll(Pageable pageable);
}
