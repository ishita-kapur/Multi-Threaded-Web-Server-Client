/*
Developed by : Ishita Kapur
UTA ID : 1001753123
CSE 5344, Fall 2019

InitializeServer Class - Initializes WebServer by passing port from the command line 

References:
            http://docs.oracle.com/javase/tutorial/essential/concurrency/runthread.html - for thread tutorial
            https://www.geeksforgeeks.org/introducing-threads-socket-programming-java/ - for implementing threads
*/

public class InitializeServer {

	//main method
	public static void main(String[] args) {

		//Initialize port number to default 8080
		int port = 8080;
		
		//Check command line arguments for user defined port number
		if(args.length == 1)
		{
			//Port number is provided
			try {
				port = Integer.parseInt(args[0]); //Check if port is an integer
			}
			catch (NumberFormatException nfe)
			{
				System.err.println("[ SERVER ]> Integer Port is not provided! Server starts at Default Port.");
			}
		}

		System.out.println("[ SERVER ]> Server starts at Port Number : " + port);
		
		//Constructing WebServer object
		WebServer ws = new WebServer(port);
		
		//Start WebServer in a new thread
		new Thread(ws).start();
	}
}
