/*
 *  ----C3282137----
 *  Ryan Jobse
 *  COMP2240 S2 2019
 *  Assignment 2
 *  
 *  A2CMachine.java
 *  The machine has dispensers and clients
 *  It manages what clients go to what dispensers and at what times
 */

import java.util.LinkedList;
import java.util.concurrent.locks.*;

public class A2CMachine {

	public static final int DISPENSERS = 2;		//Number of dispensers the machine has
	
	private String currentMode;		//Current temperature that the machine is
	private int currentTime;		//Current Time
	
	private LinkedList <Thread> clientThreads;		//Stores clients as threads
	private LinkedList <A2CClient> clients;			//Stores clients
	private int currentPosition;					//Current Client in the list
	
	private A2CClient[] dispensers;			//Array of dispensers that holds clients
	
	//Mode Lock
	public Lock modeLock = new ReentrantLock(true);	
	public Condition modeLockCondition = modeLock.newCondition();
	
	
//Constructor
	A2CMachine(){
		//Initialize dispensers and fill them
		dispensers = new A2CClient[DISPENSERS];
		for (int i = 0; i < DISPENSERS; i++) {
			dispensers[i] = null;
		}
		
		clientThreads = new LinkedList<Thread>();
		clients = new LinkedList<A2CClient>();
		currentPosition = 0;
		currentMode = "H";
		currentTime = 0;
	}
	

	public void begin() {			
		//Start all client threads
        for (Thread curThread: clientThreads) {
	    	curThread.start();
	    }
        
        //Sleep at the start to allow threads to all start and run
        try { Thread.sleep(20); }
    	catch (InterruptedException e) { e.printStackTrace(); }
        
        //Start the first client
        getCurrentClient().initialWaitLock.lock();
        getCurrentClient().initialWaitLockCondition.signal();
        getCurrentClient().initialWaitLock.unlock();
        
        //Loop while ever there is a client that has not finished
        while(true) {
        	
        	//Delay depending on the number of clients waiting, helps with fairness
        	try { Thread.sleep(countWaiting()+5); }
        	catch (InterruptedException e) { e.printStackTrace(); }
        	
 		  	//Loop through the clients
    		for (A2CClient curClient: clients) {
    			
    			//Process clients that are currently brewing
    			synchronized(clients) {
	    			if(curClient.isBrewing()) {
	    				curClient.brewLock.lock();
	    				try {
	    					curClient.brewLockCondition.signal();
						} catch (Exception e) {
							e.printStackTrace();
						}
						curClient.brewLock.unlock();
	    			}
    			}
    		}

    		//Delay added to help fairness and stability
        	try { Thread.sleep(countWaiting()+5); }
        	catch (InterruptedException e) { e.printStackTrace(); }
        	
        	//Break loop when there are no more clients waiting
        	if(countFinished() == clients.size()) {
        		break;
        	}
        	currentTime++;
        	
        }
	}

	//Add clients to lists
	public void addClient(String ID, int brewTime) {
		String temp = ID.substring(0,1);
		A2CClient newClient = new A2CClient(ID, temp, brewTime, this);
		clientThreads.add(new Thread(newClient));
		clients.add(newClient);
	}
	
	//Iterate to the next client
	public synchronized void iterateClient() {
		if(currentPosition < clients.size()-1) {
			currentPosition++;
		}
	}
	
	//Return the current selected client
	public A2CClient getCurrentClient() {
		return clients.get(currentPosition);
	}
	
	//Count the number of finished clients
	public int countFinished() {
		int result = 0;
		
		//Loop through the clients
		for (A2CClient curClient: clients) {
			
			//Add finished clients to the end result
			if(curClient.isFinished()) {
				result++;
			}
		}
		return result;
	}
	
	//Count the number of clients that are brewing
	public int countBrewing() {
		int result = 0;
		for (int i = 0; i < DISPENSERS; i++) {
			if(dispensers[i] != null) {
				result++;
			}
		}
		return result;
	}
	
	//Count the number of clients queued for brewing
	public int countQueued() {
		int result = 0;
		
		//Loop through the clients
		for (A2CClient curClient: clients) {
			
			//Add brewing clients to the end result
			if(curClient.isQueued()) {
				result++;
			}
		}
		return result;
	}
	
	//Send a signal to the next client in queue that the dispenser is ready
	public synchronized void signalNextInQueue() {
		//Loop through the clients
		for (A2CClient curClient: clients) {
			
			//Add brewing clients to the end result
			if(curClient.isQueued()) {
				curClient.queueLock.lock();
				curClient.queueLockCondition.signal();
				curClient.queueLock.unlock();
				break;
			}
		}
	}
	
	//Check if the machine is ready for another client
	public boolean isReady(A2CClient client) {
		
		//Check if there are any dispensers free
		if(countBrewing() >= DISPENSERS) {
			return false;
		}
		return true;
	}
	
	//Find a dispenser for the client to use, returns dispenser number
	public int occupyDispenser(A2CClient client) {
		int result = 0;
		for (int i = 0; i < DISPENSERS; i++) {
			if(dispensers[i] == null) {
				dispensers[i] = client;
				result = i;
				break;
			}
		}
		return result;
	}
	
	//Removes client from the dispenser
	public void finishDispenser(A2CClient client) {
		for (int i = 0; i < DISPENSERS; i++) {
			if(dispensers[i] == client) {
				dispensers[i] = null;
			}
		}
	}
	
	//Count the number of clients waiting
	public int countWaiting() {
		return clients.size() - (countFinished() + countBrewing());
	}
	
	//Get current temperature
	public String getMode() {
		return currentMode;
	}
	
	//Set current temperature
	public void setMode(String mode) {
		this.currentMode = mode;
	}
	
	//Get current time
	public int getCurrentTime() {
		return currentTime;
	}

	//Return results from the clients
	public void results() {
		for (A2CClient curClient: clients) {
			System.out.println("("+curClient.getStartTime()+") "+curClient.getID()+" uses dispenser "+curClient.getDispenser()+" (time: "+curClient.getBrewTime()+")");
		}
		System.out.println("("+currentTime+") DONE");
	}

}
