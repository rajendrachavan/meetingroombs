package neo.spring5.meetingroombooking.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "role")
@NoArgsConstructor
public class Role {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")
	private Long id;
	@Column(name="role")
	private String role;

	@OneToMany(mappedBy = "role")
	private Set<User> users;

    public Role(String role) {
        this.role = role;
    }
}
