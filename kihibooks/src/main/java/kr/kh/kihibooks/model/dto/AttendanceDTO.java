package kr.kh.kihibooks.model.dto;

public class AttendanceDTO {
	private String status;
	private String message;
	private int point;

	public AttendanceDTO() {}

	public AttendanceDTO(String status, String message, int point) {
			this.status = status;
			this.message = message;
			this.point = point;
	}

	public String getStatus() { return status; }
	public void setStatus(String status) { this.status = status; }

	public String getMessage() { return message; }
	public void setMessage(String message) { this.message = message; }

	public int getPoint() { return point; }
	public void setPoint(int point) { this.point = point; }
}
