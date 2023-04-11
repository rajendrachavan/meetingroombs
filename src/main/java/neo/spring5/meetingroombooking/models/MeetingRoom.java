package neo.spring5.meetingroombooking.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "meeting_room")
@NoArgsConstructor
public class MeetingRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String location;

    @ManyToMany
    private List<Facility> facilities;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "meetingRoom")
    private List<BookingDetails> bookingDetails;

    public MeetingRoom(String name, String location, List<Facility> facilities) {
        this.name = name;
        this.location = location;
        this.facilities = facilities;
    }
}
