import java.io.*;
import java.util.Scanner;

public class A2B {
	
	static A2BParlour parlour = new A2BParlour();
	
	public static void main(String[] args) {
		if (args.length == 1){
			String name = args[0];
			System.out.println("Problem B - File: "+ System.getProperty("user.dir") + "\\" + name + "\n");

			if(importFile(name, parlour)) {
		    	
		    	//Begin the scheduling algorithms
				parlour.begin();
		    }else {
		    	System.out.println("Error Occured While Importing!");
		    }
		}else {
			System.out.println("Wrong Parameters!\nNeeds Data File Location");
		}
	}
	

	public static boolean importFile(String name, A2BParlour parlour) {
		boolean result = true;
		try{
			 
			File file = new File(name);
			Scanner inputStream = new Scanner (file);
		    
			//Read contents into this string
			String contents = new String();
			
		    while (inputStream.hasNextLine ()){
		        contents += inputStream.nextLine().trim() + "\n";
		    }
		    inputStream.close ();
		    
		    //Remove all blank lines
		    contents = contents.replaceAll("(?m)^[ \t]*\r?\n", "");
		    
		    //Split by lines
		    String[] lines = contents.split("\\r?\\n");
		            
	    	//Loop through lines
		    for(int i = 0; i < lines.length; i++) {
		    	
		    	//Search for END
		        if(lines[i].equals("END")) {
		        	break;
		        }else {
		        	//Split lines by space
			        String[] data = lines[i].split(" ");
			        parlour.addCustomer(Integer.parseInt(data[0]), data[1], Integer.parseInt(data[2]));
		        }
		    }
		}
		catch(IOException e){
		    System.out.println("File Does Not Exist");
		    result = false;
		}
		return result;
	}
}
