package neo.spring5.meetingroombooking.models;

import javax.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.Transient;

import java.util.List;


@Getter
@Setter
@Entity
@Table(name = "user")
@NoArgsConstructor
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private Long id;

	@Column(name = "email")
	private String email;

	@Column(name = "password")
	@Length(min = 5, message = "*Your password must have at least 5 characters")
	@Transient
	private String password;

	@Column(name = "first_name")
	private String firstName;

	@Column(name = "last_name")
	private String lastName;

	@Column(name = "gender")
	private String gender;

	@Column(name = "mobile_no")
    @Length(max = 10, min = 9, message = "*Invalid mobile number")
	private String mobileNo;

	@ManyToOne(cascade = CascadeType.PERSIST)
	private Department department;

	@Column(name = "active")
	private int active;

	@ManyToOne(cascade = CascadeType.PERSIST)
	private Role role;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	private List<BookingDetails> bookingDetails;


	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	private List<ChangeRequest> changeRequests;

	@ManyToOne
	private User parent;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	private List<Feedback> feedback;

	@OneToMany(mappedBy = "to", cascade = CascadeType.ALL)
	private List<Notification> notifications;

	public User(String email, String password, String firstName, String lastName, String gender, String mobileNo, int active) {
		this.email = email;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
		this.gender = gender;
		this.mobileNo = mobileNo;
		this.active = active;
	}
}
