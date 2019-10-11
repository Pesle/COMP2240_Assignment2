/*
 *  ----C3282137----
 *  Ryan Jobse
 *  COMP2240 S2 2019
 *  Assignment 2
 *  
 *  A2CClient.java
 *  The client is waiting for the machine to be both the right temperature
 *  and the available for them to start brewing
 *  It also records data about the brew
 */

import java.util.concurrent.locks.*;

public class A2CClient implements Runnable{

	private String ID;				//ID of the client
	private String temperature;		//Same as mode
	private int brewTime;			//Time it takes to brew the clients coffee
	private int startTime;			//Time the client starts
	private int finishTime;			//Time the clients brew finishes
	private int dispenser;			//The dispenser number used by the client
	
	private A2CMachine machine;
	private boolean queued;			//If the client is queued
	private boolean brewing;		//If the client is brewing
	private boolean finished;		//If the client is finished
	
	//Lock for when the client is first created so that clients are called in order
	public ReentrantLock initialWaitLock = new ReentrantLock(true);
	public Condition initialWaitLockCondition = initialWaitLock.newCondition();
	
	//Lock for when the client is in queue as the machine is the right temperature
	public ReentrantLock queueLock = new ReentrantLock(true);
	public Condition queueLockCondition = queueLock.newCondition();
	
	//Lock for when the client is brewing
	public ReentrantLock brewLock = new ReentrantLock(true);
	public Condition brewLockCondition = brewLock.newCondition();
	
//Constructor
	public A2CClient(String ID, String temp, int brewTime, A2CMachine machine) {
		this.ID = ID;
		this.temperature = temp;
		this.brewTime = brewTime;
		this.machine = machine;
		queued = false;
		brewing = false;
		finished = false;
	}

//Run
	@Override
	public void run() {
		//Wait until called by a signal
		synchronized(this) {
			initialWaitLock.lock();
			try {
				initialWaitLockCondition.await();
			} catch (InterruptedException e) { 
				e.printStackTrace(); 
			}
			initialWaitLock.unlock();
		}
		
		//Make sure the machine is in the correct mode
		checkMachineMode();
		
		//Make sure that the machine has a dispenser free
		checkMachineDispensers();

		//Start the brew
		startBrewing();
	}
	
	private void checkMachineMode() {
		//Lock the machine mode
		machine.modeLock.lock();
	
		//Loop until machine is in correct mode
		while (true) {
			//Check current mode
			if(!machine.getMode().equals(temperature)) {
				try {
					machine.modeLockCondition.await();
				} catch (Exception e) {
					e.printStackTrace();
					machine.modeLock.unlock();
					break;
				}
				continue;
			}else {
				break;
			}
		}
		
		//If there are clients waiting, call the next one
		if (machine.countWaiting() > 0) {
			machine.iterateClient();
			A2CClient temp = machine.getCurrentClient();
			temp.initialWaitLock.lock();
			temp.initialWaitLockCondition.signal();
			temp.initialWaitLock.unlock();
		}
		
		//Send a signal to the modeLock
		machine.modeLockCondition.signal();
		try { Thread.sleep(1); } 
		catch (InterruptedException e) { e.printStackTrace(); }
		machine.modeLock.unlock();
	}
	
	private synchronized void checkMachineDispensers(){ 
		
		//Lock the queueLock
		queueLock.lock();
		queued = true;		//Currently queued
		
		//Loop until the machine is ready for the client
		while(true) {
			if(!machine.isReady(this)) {
				try {
					queueLockCondition.await();
				} catch (InterruptedException e) {
					e.printStackTrace();
					queueLockCondition.signal();
					break;
				}
			}else {
				break;
			}
		}
		
		queueLock.unlock();
		
	}
	
	private void startBrewing() {
		
		dispenser = machine.occupyDispenser(this);		//Occupy a dispenser and get its number
		startTime = machine.getCurrentTime();			//Set the start time
		queued = false;									//Not queued anymore
		brewing = true;									//Currently brewing
		finishTime = brewTime + machine.getCurrentTime();	//Set finish time
		
		//Lock brewLock
		brewLock.lock();
		
		//Loop till the coffee is finished brewing
		while (true) {	
			if(machine.getCurrentTime() < finishTime) {
				try {
					brewLockCondition.await();//if brew-time is not yet finished, continue to wait
				} catch (InterruptedException e) {
					e.printStackTrace();
					brewLockCondition.signal();
					break;
				}
				continue;
			}else {
				break;
			}
		}	

		brewing = false;	//Finished brewing
		finished = true;	//Finished
		machine.finishDispenser(this);		//Deallocate dispenser
		brewLock.unlock();
		
		//If there is clients waiting and nobody is brewing, change the temperature and call the next client stuck at modeLock
		if (machine.countWaiting() > 0) {
			if(machine.countBrewing() == 0 && machine.countQueued() == 0) {
				machine.setMode(machine.getCurrentClient().getTemperature());
				machine.modeLock.lock();
				machine.modeLockCondition.signal();
				machine.modeLock.unlock();
			}
		}
		
		machine.signalNextInQueue();	
	}
	
//Getters
	public String getTemperature() {
		return temperature;
	}
	
	public String getID() {
		return ID;
	}
	
	public int getBrewTime() {
		return brewTime;
	}
	
	public int getStartTime() {
		return startTime;
	}

	public int getFinishTime() {
		return finishTime;
	}
	
	public int getDispenser() {
		return dispenser + 1;
	}

	public boolean isBrewing() {
		return brewing;
	}
	
	public boolean isQueued() {
		return queued;
	}
	
	public boolean isFinished() {
		return finished;
	}
	
}
