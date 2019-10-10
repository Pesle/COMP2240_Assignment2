import java.util.LinkedList;
import java.util.concurrent.locks.*;

public class A2CMachine {

	public static final int DISPENSERS = 2;
	
	private LinkedList <A2CClient> clients;
	
	public Lock modeLock = new ReentrantLock(true);
	public Condition modeLockCondition = modeLock.newCondition();
	
	A2CMachine(){
		clients = new LinkedList<A2CClient>();
	}
	

	public void begin() {
		// TODO Auto-generated method stub
		
	}
	
	public void addClient(String ID, int brewTime) {
		String temp = ID.substring(0,1);
		A2CClient newClient = new A2CClient(ID, temp, brewTime, this);
		//clients.add(new Thread(newCustomer));
		clients.add(newClient);
	}

	public void results() {
		// TODO Auto-generated method stub
		
	}

}
