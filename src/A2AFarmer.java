/*
 *  ----C3282137----
 *  Ryan Jobse
 *  COMP2240 S2 2019
 *  Assignment 2
 *  
 *  A2AFarmer.java
 *  The farmer keeps trying to cross the bridge, but can only cross when available
 */

import java.util.concurrent.Semaphore;

public class A2AFarmer implements Runnable {
	
	private static final Semaphore SEMAPHORE = new Semaphore(1,true);
	private static int count = 0;
	private static final int STEP_SIZE = 5;
	private static final int DISTANCE = 15;
	
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
			
			//Once crossed, swap Directions
			if(direction.equals("North")) {
				direction = "South";
			}else {
				direction = "North";
			}
		}
	}
	
	private void crossBridge() {
		//Try to cross the bridge
		try {
			SEMAPHORE.acquire();
			//Set current step to the stepSize
			currentStep = STEP_SIZE;
			
			//Loop until the farmer has crossed the bridge
			while(currentStep < DISTANCE) {
				System.out.println(ID + ": Crossing bridge Step " + currentStep +".");
				currentStep += STEP_SIZE;
			}		
			System.out.println(ID + ": Across the bridge.");

			count++;
			System.out.println("NEON = " + count);
			SEMAPHORE.release();

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}
