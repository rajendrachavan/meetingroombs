package neo.spring5.meetingroombooking.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
public class ChangeRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(value = EnumType.STRING)
    private Type type;
    private String oldValue;
    private String newValue;

    @Enumerated(value = EnumType.STRING)
    private Status status;

    @ManyToOne
    private User user;
}
