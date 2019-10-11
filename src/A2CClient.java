import java.util.concurrent.locks.*;

public class A2CClient implements Runnable{

	private String ID;
	private String temperature;		//Same as mode
	private int brewTime;
	private int startTime;
	private int finishTime;
	private int dispenser;
	
	private A2CMachine machine;
	private boolean queued;
	private boolean brewing;
	private boolean finished;
	
	public ReentrantLock initialWaitLock = new ReentrantLock(true);
	public Condition initialWaitLockCondition = initialWaitLock.newCondition();
	
	public ReentrantLock queueLock = new ReentrantLock(true);
	public Condition queueLockCondition = queueLock.newCondition();
	
	public ReentrantLock brewLock = new ReentrantLock(true);
	public Condition brewLockCondition = brewLock.newCondition();
	
	public A2CClient(String ID, String temp, int brewTime, A2CMachine machine) {
		this.ID = ID;
		this.temperature = temp;
		this.brewTime = brewTime;
		this.machine = machine;
		queued = false;
		brewing = false;
		finished = false;
	}

	@Override
	public void run() {
		synchronized(this) {
			initialWaitLock.lock();
			try {
				initialWaitLockCondition.await();
			} catch (InterruptedException e) { 
				e.printStackTrace(); 
			}
			initialWaitLock.unlock();
		}
		
		System.out.println(ID + " Checking Mode");
		checkMachineMode();
		
		System.out.println(ID + " Checking Dispensers");
		checkMachineDispensers();
		
		System.out.println(ID + " Starting Brew " + machine.getCurrentTime());
		startBrewing();
	}
	
	private void checkMachineMode() {
		//Lock the machine mode
		machine.modeLock.lock();
	
		//Loop until machine is in correct mode
		while (true) {
			//Check current mode
			if(!machine.getMode().equals(temperature)) {
				System.out.println(ID + " Waiting till mode change");
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
		
		if (machine.countWaiting() > 0) {
			machine.iterateClient();
			A2CClient temp = machine.getCurrentClient();
			temp.initialWaitLock.lock();
			temp.initialWaitLockCondition.signal();
			temp.initialWaitLock.unlock();
		}
		
		machine.modeLockCondition.signal();
		try { Thread.sleep(1); } catch (InterruptedException e) { e.printStackTrace(); }
		machine.modeLock.unlock();
	}
	
	private synchronized void checkMachineDispensers(){ 
		queueLock.lock();
		
		queued = true;
		
		while(true) {
			if(!machine.isReady(this)) {
				try {
					queueLockCondition.await();//if brew-time is not yet finished, continue to wait
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
		
		dispenser = machine.occupyDispenser(this);
		startTime = machine.getCurrentTime();
		queued = false;
		brewing = true;
		finishTime = brewTime + machine.getCurrentTime();
		
		brewLock.lock();
		
		System.out.println(ID + " Brew Till "  + finishTime);
		
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
		
		System.out.println(ID + " Finished Brew " + machine.getCurrentTime());
		brewing = false;
		finished = true;
		machine.finishDispenser(this);
		brewLock.unlock();
		
		if (machine.countWaiting() > 0) {
			if(machine.countBrewing() == 0) {
				System.out.println("Changing Mode to "+machine.getCurrentClient().getTemperature());
				machine.setMode(machine.getCurrentClient().getTemperature());
				machine.modeLock.lock();
				machine.modeLockCondition.signal();
				machine.modeLock.unlock();
			}
		}
		
		machine.signalNextInQueue();	
	}
	
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
