import java.util.concurrent.Semaphore;

public class A2AFarmer implements Runnable {
	
	private static final Semaphore semaphore = new Semaphore(1,true);
	private static int count = 0;
	private static final int stepSize = 5;
	private static final int distance = 15;
	
	private String direction;
	private String ID;
	private int currentStep;
	
	public A2AFarmer(String ID, String direction) {
		this.ID = ID;
		this.direction = direction;
		this.currentStep = 0;
	}
	
	@Override
	public void run() {
		while(true) {
			System.out.println(ID + ": Waiting for bridge. Going towards "+ direction);
			
			//Try to cross Bridge
			crossBridge();
			
			//Swap Directions
			if(direction.equals("North")) {
				direction = "South";
			}else {
				direction = "North";
			}
		}
	}
	
	private void crossBridge() {
		try {
			semaphore.acquire();
			currentStep = stepSize;
			while(currentStep < distance) {
				System.out.println(ID + ": Crossing bridge Step " + currentStep +".");
				currentStep += stepSize;
			}		
			System.out.println(ID + ": Across the bridge.");

			count++;
			System.out.println("NEON = " + count);
			semaphore.release();

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}
