import java.util.LinkedList;
import java.util.concurrent.Semaphore;

import javafx.event.Event;

public class A2BParlour {
	
	private final static int MAX_SEATS = 4;
	private static int currentTime = 0;
	private final Semaphore seats = new Semaphore(MAX_SEATS, true);
	
	private LinkedList<Thread> customers;
	private LinkedList<A2BCustomer> arrivedCustomers;
	private LinkedList<A2BEvent> events;
	
	private boolean ready;
	private int seated;
	
	public A2BParlour() {
		ready = true;
		seated = 0;
		customers = new LinkedList<Thread>();
		arrivedCustomers = new LinkedList<A2BCustomer>();
		events = new LinkedList<A2BEvent>();
	}
	
	public void begin() {
		//Start all customer threads
        for (Thread curThread: customers) {
	    	curThread.start();
	    }
        
        while(!allCustomersFinished()) {
        	
        	try { Thread.sleep(5); }
        	catch (InterruptedException e) { e.printStackTrace(); }

        	if(seated == 4) {
        		ready = false;
        	}
        	if(countSeated() == 0) {
        		ready = true;
        		seated = 0;
        	}
        	
        	
        	currentTime++;
        	System.out.println("Current Time: " +currentTime + "Seated: " + seated);
        }
	}
	
	public void addCustomer(int arriveTime, String ID, int eatingTime) {
		A2BCustomer newCustomer = new A2BCustomer(arriveTime, ID, eatingTime, this);
		customers.add(new Thread(newCustomer));
		arrivedCustomers.add(newCustomer);
		events.add(new A2BEvent(ID,arriveTime));
	}
	
	private boolean allCustomersFinished() {
		boolean result = true;
		for (A2BCustomer curCustomer: arrivedCustomers) {
			if(!curCustomer.isFinished()) {
				result = false;
			}
		}
		return result;
	}
	
	private int countSeated() {
		int result = 0;
		for (A2BCustomer curCustomer: arrivedCustomers) {
			if(curCustomer.isSeated()) {
				result++;
			}
		}
		return result;
	}
	
	public void setEventSeatTime(String ID) {
		for (A2BEvent curEvent: events) {
			if(curEvent.getID().equals(ID)) {
				curEvent.setSeats(currentTime);
			}
		}
	}

	public void setEventLeaveTime(String ID) {
		for (A2BEvent curEvent: events) {
			if(curEvent.getID().equals(ID)) {
				curEvent.setLeaveTime(currentTime);
			}
		}
	}
	
	public int getCurrentTime() {
		return currentTime;
	}
	
	public void addSeated() {
		seated++;
	}
	
	public Semaphore getSeats() {
		return seats;
	}
	
	public boolean isReady() {
		return ready;
	}

	public void results() {
		System.out.format("%-16s%-16s%-16s%-16s\n", "Customer", "Arrives", "Seats", "Leaves");
		for (A2BEvent curEvent: events) {
			System.out.format("%-16s%-16s%-16s%-16s\n", curEvent.getID(), curEvent.getArriveTime(), curEvent.getSeats(), curEvent.getLeaveTime());
		}
	}
}
