package neo.spring5.meetingroombooking.commons;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseDTO<T> {

	private int status;
	private T data;
	private String message;
	private String errorCode;

	public ResponseDTO() {
	}

	public ResponseDTO(int status, String message) {
		this.status = status;
		this.message = message;
	}
}
