/*
 *  ----C3282137----
 *  Ryan Jobse
 *  COMP2240 S2 2019
 *  Assignment 2
 *  
 *  A2C.java
 *  Main file for Problem C
 *  Imports file and runs the Coffee Machine
 */

import java.io.*;
import java.util.Scanner;

public class A2C {
	
	static A2CMachine machine = new A2CMachine();
	
	public static void main(String[] args) {
		
		//Make sure the file is specified
		if (args.length == 1){
			String name = args[0];
			
			//Show the location of the file
			System.out.println("Assignment 2, Problem C - File: "+ System.getProperty("user.dir") + "\\" + name + "\n");

			//Import the contents of the file to the machine
			if(importFile(name, machine)) {
		    	
		    	//Begin serving clients
				machine.begin();
				
				//Display results
				machine.results();
				
		    }else {
		    	System.out.println("Error Occured While Importing!");
		    }
		}else {
			System.out.println("Wrong Parameters!\nNeeds Data File Location");
		}
	}
	

	public static boolean importFile(String name, A2CMachine machine) {
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
		    for(int i = 1; i <= Integer.parseInt(lines[0]); i++) {
		 
	        	//Split lines by space
		        String[] data = lines[i].split(" ");
		        machine.addClient(data[0], Integer.parseInt(data[1]));
		    }
		}
		catch(IOException e){
		    System.out.println("File Does Not Exist");
		    result = false;
		}
		return result;
	}
}
