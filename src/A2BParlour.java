/*
 *  ----C3282137----
 *  Ryan Jobse
 *  COMP2240 S2 2019
 *  Assignment 2
 *  
 *  A2BParlour.java
 *  Parlour has seats and customers
 *  Manages the seats and what customers can sit at what times
 */

import java.util.LinkedList;
import java.util.concurrent.Semaphore;

public class A2BParlour {
	
	private final static int MAX_SEATS = 4;			//Max number of seats
	private static int currentTime = 0;				//Starting Time
	
	private final Semaphore seats = new Semaphore(MAX_SEATS, true);
	
	private LinkedList<Thread> customerThreads;				//List of customers as threads
	private LinkedList<A2BCustomer> customers;	//List of customers
	
	private boolean ready;			//If the parlour is ready for more customers
	private int previousSeated;		//Number of customers seated before ready
	
	
//Constructor
	public A2BParlour() {
		ready = true;
		previousSeated = 0;
		customerThreads = new LinkedList<Thread>();
		customers = new LinkedList<A2BCustomer>();
	}
	
//Begin the code
	public void begin() {
		//Start all customer threads
        for (Thread curThread: customerThreads) {
	    	curThread.start();
	    }
        
        //Loop while ever there is a customer that hasnt finished
        while(!allCustomersFinished()) {
        	
        	//Sleep to allow the customer threads to try and access the seats
        	try { Thread.sleep(MAX_SEATS); }
        	catch (InterruptedException e) { e.printStackTrace(); }

        	//Add to the current time
        	currentTime++;
        }
	}
	
	//Add customers to the queue
	public void addCustomer(int arriveTime, String ID, int eatingTime) {
		A2BCustomer newCustomer = new A2BCustomer(arriveTime, ID, eatingTime, this);
		customerThreads.add(new Thread(newCustomer));
		customers.add(newCustomer);
	}
	
	//Checks if all customers have finished
	private boolean allCustomersFinished() {
		//Loop through customers
		for (A2BCustomer curCustomer: customers) {
			
			//If a customer hasnt finished, then return false
			if(!curCustomer.isFinished()) {
				return false;
			}
		}
		return true;
	}
	
	//Count the number of customers currently seated
	private int countSeated() {
		int result = 0;
		
		//Loop through the customers
		for (A2BCustomer curCustomer: customers) {
			
			//Add seated customers to the end result
			if(curCustomer.isSeated()) {
				result++;
			}
		}
		return result;
	}
	
//Getters	
	public int getCurrentTime() {
		return currentTime;
	}
	
	public Semaphore getSeats() {
		return seats;
	}
	
	public boolean isReady() {
		
		//If the parlour is still accepting customers and has 4 people previously seated, stop seating more customers
    	if(ready == true && previousSeated == 4) {
    		ready = false;
    	}
    	
    	//If there are no customers currently seated, then allow new customers to be seated
    	if(countSeated() == 0) {
    		ready = true;
    		previousSeated = 0;
    	}
		return ready;
	}

	public void results() {
		System.out.format("%-10s%-10s%-10s%-10s\n", "Customer", "Arrives", "Seats", "Leaves");
		for (A2BCustomer curCustomer: customers) {
			System.out.format("%-10s%-10s%-10s%-10s\n", curCustomer.getID(), curCustomer.getArriveTime(), curCustomer.getSeatedTime(), curCustomer.getLeaveTime());
		}
	}
	
	//Adds to the number of previous seated customers.
	public void addSeated() {
		previousSeated++;
	}
}
