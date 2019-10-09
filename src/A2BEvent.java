
public class A2BEvent {
	private String ID;
	private int arriveTime;
	private int seats;
	private int leaveTime;
	
	A2BEvent(String ID, int arriveTime){
		this.ID = ID;
		this.arriveTime = arriveTime;
	}

	public int getArriveTime() {
		return arriveTime;
	}

	public int getSeats() {
		return seats;
	}

	public void setSeats(int seats) {
		this.seats = seats;
	}

	public int getLeaveTime() {
		return leaveTime;
	}

	public void setLeaveTime(int leaveTime) {
		this.leaveTime = leaveTime;
	}

	public String getID() {
		return ID;
	}
	
}
