public class A2A {

	public static void main(String[] args) {

		//Make sure that the file has been set
		if (args.length == 2){
			int north = -1, south = -1;
			for(int i = 0; i < 2; i++) {
				String[] cur = args[i].split("=");
				
				if(cur[0].contentEquals("N")) {
					north = Integer.parseInt(cur[1]);
				}
				if(cur[0].contentEquals("S")) {
					south = Integer.parseInt(cur[1]);
				}
			}
			if(north >= 0 && south >= 0) {
				System.out.println("Problem A - North: " + north + " / South: " + south + "\n");

				begin(north, south);
				
			}else {
				System.out.println("North and South Data is Invalid!");
			}
		}else {
			System.out.println("Wrong Parameters!\nNeeds N=x S=x");
		}
	}
	
	public static void begin(int n, int s) {
		
		//Generate North Farmers
		for (int i = 0; i < n; i++) {
			new Thread(new A2AFarmer("S_Farmer" + (i+1), "South")).start();
		}
		
		//Generate South Farmers
		for (int i = 0; i < s; i++) {
			new Thread(new A2AFarmer("N_Farmer" + (i+1), "North")).start();
		}	
	}
}
