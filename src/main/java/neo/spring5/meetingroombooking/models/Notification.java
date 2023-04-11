package neo.spring5.meetingroombooking.models;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;


@Data
@Entity
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private User to;
    private String description;

    @Enumerated(value = EnumType.STRING)
    private Type type;

    @Enumerated(value = EnumType.STRING)
    private Status status;

    private LocalDateTime expiryDate;

    public Notification() {
    }

    public Notification(User to, String description, Type type, Status status, LocalDateTime expiryDate) {
        this.to = to;
        this.description = description;
        this.type = type;
        this.status = status;
        this.expiryDate = expiryDate;
    }
}
