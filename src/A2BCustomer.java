
public class A2BCustomer implements Runnable{

	private int arriveTime;
	private String ID;
	private int eatingTime;
	private A2BParlour currentParlour;
	private boolean finished;
	private boolean seated;
	
	A2BCustomer(int arriveTime, String ID, int eatingTime, A2BParlour currentParlour){
		this.arriveTime = arriveTime;
		this.ID = ID;
		this.eatingTime = eatingTime;
		this.currentParlour = currentParlour;
		this.finished = false;
		this.seated = false;
	}
	
	@Override
	public void run() {
		while(!finished) {
			try { Thread.sleep(1); }
        	catch (InterruptedException e) { e.printStackTrace(); }
			if(!seated) {
				if(currentParlour.getCurrentTime() >= arriveTime) {
					if(currentParlour.isReady()) {
						try {
							currentParlour.getSeats().acquire();
							currentParlour.setEventSeatTime(this.ID);
							currentParlour.addSeated();
							seated = true;
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}else {
				System.out.println(ID);
				if(currentParlour.getCurrentTime() >= (arriveTime + eatingTime)) {
					currentParlour.getSeats().release();
					currentParlour.setEventLeaveTime(this.ID);
					seated = false;
					finished = true;
				}
			}
		}
	}
		
	public boolean isFinished() {
		return finished;
	}

	public boolean isSeated() {
		return seated;
	}
	
}
