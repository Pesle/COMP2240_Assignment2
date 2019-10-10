public class A2CClient implements Runnable{

	private String ID;
	private String temperature;
	private int brewTime;
	private A2CMachine machine;
	
	public A2CClient(String ID, String temp, int brewTime, A2CMachine machine) {
		this.ID = ID;
		this.temperature = temp;
		this.brewTime = brewTime;
		this.machine = machine;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
	
	
}
