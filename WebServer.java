/*
Developed by : Ishita Kapur
UTA ID : 1001753123
CSE 5344, Fall 2019

WebServer Class - Starts serverSocket and listens to Client request. This class implements Runnable interface
                  and overrides the public void run() method.

References:
            https://www.youtube.com/watch?v=RQ2v0CSV4tY - for implementing multithreading in servers
            http://docs.oracle.com/javase/tutorial/essential/concurrency/runthread.html - for thread tutorials
            http://www.oracle.com/technetwork/java/socket-140484.html - for socket communications
            https://www.geeksforgeeks.org/introducing-threads-socket-programming-java/ - for implementing threads
            https://www.javatpoint.com/socket-programming - for socket programming
*/

import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class WebServer implements Runnable {

	private ServerSocket serverSocket; //serverSocket reference - server starts here
	private String serverHost; //Hostname/IP address of the server
	private int serverPort; //Port number of server
	
	//Default hostname and port values for the serverSocket
	private final String DEFAULT_HOST = "localhost";
	private final int DEFAULT_PORT = 8080;
	
	//Default constructor if no port number is passed, it takes default values of hostname and port number
	public WebServer ()
	{
		this.serverHost = DEFAULT_HOST; //hostname of the server
		this.serverPort = DEFAULT_PORT; //default port 8080
	}
		
	//Parameterized constructor if a port and serverHost are passed, user entered values from the command line
	public WebServer (String sHost, int port)
	{
		this.serverHost = sHost; //hostname of the server
		this.serverPort = port; //default port 8080
	}
	
	//Parameterized constructor if a port is passed, user entered values from the command line
	public WebServer (int port)
	{
		this.serverHost = DEFAULT_HOST; //hostname of the server
		this.serverPort = port; //port number passed by the ServerInitializer
	}

	
	@Override
	public void run() {
		
		try {

			//Getting inet address of the host
			InetAddress serverInet = InetAddress.getByName(serverHost);
			
			
			//Initializing serverSocket using serverInet address and serverPort
			//Using a default backlog value which depends on the implementation
			serverSocket = new ServerSocket(serverPort, 0, serverInet);

			System.out.println("[ SERVER ]> SERVER started at host: " + serverSocket.getInetAddress() + " and port number: " + serverSocket.getLocalPort() + "\n");
			
			//Providing each client an ID, starting with zero
			int clientID = 0;
			
			//Multithreaded server
			while(true){
				
				//Wait for a client to get connected
				Socket clientSocket = serverSocket.accept();
				
				//Details of a new client that has connected to this server
				System.out.println("[ SERVER - CLIENT"+clientID+" ]> Connection successfully established with the client at " + clientSocket.getInetAddress() + ":" + clientSocket.getPort());
				
				//Passing clientSocket and clientID to HttpRequestHandler object
				HttpRequestHandler rh = new HttpRequestHandler(clientSocket, clientID);
				
				//handover processing for the newly connected client to HttpRequestHandler in a separate thread
				new Thread(rh).start();
				
				//increment clientID for the next client;
				clientID++;
			}
			
		} catch (UnknownHostException e) {
			System.err.println("[ SERVER ]> UnknownHostException caught for the hostname: " + serverHost);
		} catch (IllegalArgumentException iae) {
			System.err.println("[ SERVER ]> EXCEPTION caught in starting the SERVER: " + iae.getMessage());
		}
		catch (IOException e) {
			System.err.println("[ SERVER ]> EXCEPTION caught in starting the SERVER: " + e.getMessage());
		}
		finally {
				try {
					if(serverSocket != null){
						serverSocket.close();
					}
				} catch (IOException e) {
					System.err.println("[ SERVER ]> EXCEPTION caught in closing the server socket." + e);
				}
		}
	}
}
