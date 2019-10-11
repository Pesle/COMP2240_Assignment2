import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.locks.*;

public class A2CMachine {

	public static final int DISPENSERS = 2;
	
	private String currentMode;
	private int currentTime;
	
	private LinkedList <Thread> clientThreads;
	private LinkedList <A2CClient> clients;
	private int currentPosition;
	
	private A2CClient[] dispensers;
	
	public Lock modeLock = new ReentrantLock(true);
	public Condition modeLockCondition = modeLock.newCondition();
	
	public Lock generalLock = new ReentrantLock(true);
	public Condition generalLockCondition = generalLock.newCondition();
	
	A2CMachine(){
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
        
        try { Thread.sleep(100); }
    	catch (InterruptedException e) { e.printStackTrace(); }
        
        getCurrentClient().initialWaitLock.lock();
        getCurrentClient().initialWaitLockCondition.signal();
        getCurrentClient().initialWaitLock.unlock();
        
        //Loop while ever there is a client that has not finished
        while(true) {
        	
        	try { Thread.sleep(countWaiting()*2); }
        	catch (InterruptedException e) { e.printStackTrace(); }
        	
 		  	//Loop through the clients
    		for (A2CClient curClient: clients) {
    			
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

        	System.out.println("Time: "+ currentTime + " 	Waiting: " + countWaiting() + " 	Queued: " + countQueued() + "	Brewing: " + countBrewing() + "	Finished: " + countFinished());
        	
        	try { Thread.sleep(countWaiting()*2+2); }
        	catch (InterruptedException e) { e.printStackTrace(); }
        	//Add to the current time
        	
        	if(countFinished() == clients.size()) {
        		break;
        	}
        	currentTime++;
        	
        }
	}


	public void addClient(String ID, int brewTime) {
		String temp = ID.substring(0,1);
		A2CClient newClient = new A2CClient(ID, temp, brewTime, this);
		clientThreads.add(new Thread(newClient));
		clients.add(newClient);
	}
	
	public synchronized void iterateClient() {
		if(currentPosition < clients.size()-1) {
			currentPosition++;
		}
	}
	
	public A2CClient getCurrentClient() {
		return clients.get(currentPosition);
	}
	
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
	
	public int countBrewing() {
		int result = 0;
		
		for (int i = 0; i < DISPENSERS; i++) {
			if(dispensers[i] != null) {
				result++;
			}
		}
		return result;
	}
	
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
	
	public void signalNextInQueue() {
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
	
	public boolean isReady(A2CClient client) {
		if(countBrewing() >= DISPENSERS) {
			System.out.println("Not Ready! Dispensers occupied");
			return false;
		}
		
		if(client.getTemperature().equals(currentMode)) {
			return true;
		}else {
			if(countBrewing() == 0) {
				currentMode = client.getTemperature();
				return true;
			}else {
				System.out.println("Not Ready! Wrong Temp " + client.getTemperature() + " vs " + currentMode);
				return false;
			}
		}
	}
	
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
	
	public void finishDispenser(A2CClient client) {
		for (int i = 0; i < DISPENSERS; i++) {
			if(dispensers[i] == client) {
				dispensers[i] = null;
			}
		}
	}
	
	public int countWaiting() {
		return clients.size() - (countFinished() + countBrewing());
	}
	
	public String getMode() {
		return currentMode;
	}
	
	public void setMode(String mode) {
		this.currentMode = mode;
	}
	
	public int getCurrentTime() {
		return currentTime;
	}

	public void results() {
		for (A2CClient curClient: clients) {
			System.out.println("("+curClient.getStartTime()+") "+curClient.getID()+" uses dispenser "+curClient.getDispenser()+" (time: "+curClient.getBrewTime()+")");
		}
		System.out.println("("+currentTime+") DONE");
	}

}
