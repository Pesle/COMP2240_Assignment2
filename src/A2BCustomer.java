/*
 *  ----C3282137----
 *  Ryan Jobse
 *  COMP2240 S2 2019
 *  Assignment 2
 *  
 *  A2BCustomer.java
 *  The Customer looks for a seat to sit in
 *  Also records data about itself
 */

public class A2BCustomer implements Runnable{

	private int arriveTime;		//Time the customer arrives
	private String ID;			//ID of the customer
	private int eatingTime;		//How long it takes the customer to eat
	private int seatedTime;		//Time the customer sits down
	private int leaveTime;		//Time the customer leaves
	
	private A2BParlour currentParlour;
	private boolean finished;	//Check if customer has left
	private boolean seated;		//Check if the customer is seated
	
	
//Constructor
	A2BCustomer(int arriveTime, String ID, int eatingTime, A2BParlour currentParlour){
		this.arriveTime = arriveTime;
		this.ID = ID;
		this.eatingTime = eatingTime;
		this.seatedTime = 0;
		this.leaveTime = 0;
		
		this.currentParlour = currentParlour;
		this.finished = false;
		this.seated = false;
	}
	
	@Override
	public void run() {
		
		//Loop while the customer has not finished
		while(!finished) {
			
			//Sleep thread to allow fairness
			try { Thread.sleep(1); }
        	catch (InterruptedException e) { e.printStackTrace(); }
			
			//Try to seat customer if not currently seated
			if(!seated) {
				//Check if they have arrived yet
				if(currentParlour.getCurrentTime() >= arriveTime) {
					//Check if the parlour is ready
					if(currentParlour.isReady()) {
						//Try to acquire a seat
						try {
							currentParlour.getSeats().acquire();
							seatedTime = currentParlour.getCurrentTime();
							currentParlour.addSeated(); 
							seated = true;
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}else {
				//If already seated, wait till eating time is finished, then leave
				if(currentParlour.getCurrentTime() >= (seatedTime + eatingTime)) {
					currentParlour.getSeats().release();
					leaveTime = currentParlour.getCurrentTime();
					seated = false;
					finished = true;
				}
			}
		}
	}
		
//Getters
	public int getArriveTime() {
		return arriveTime;
	}

	public String getID() {
		return ID;
	}

	public int getEatingTime() {
		return eatingTime;
	}

	public int getSeatedTime() {
		return seatedTime;
	}

	public int getLeaveTime() {
		return leaveTime;
	}

	public boolean isFinished() {
		return finished;
	}

	public boolean isSeated() {
		return seated;
	}
	
}
