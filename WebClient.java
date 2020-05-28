/*
Developed by : Ishita Kapur
UTA ID : 1001753123
CSE 5344, Fall 2019

WebClient Class - Represents single web client

References:
            http://www.oracle.com/technetwork/java/socket-140484.html - for socket communications
            https://www.journaldev.com/741/java-socket-programming-server-client - for client side socket program
*/

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;

public class WebClient {

	public static void main(String[] args) {
		final String CRLF = "\r\n"; //Carriage return line feed
		final String SP = " "; //Status line parts separator
		
		String serverHost = null;
		
		//Initialize serverPort with default port value
		int serverPort = 8080;
		
		//Initialize filePath with default file /
		String filePath = "/";
		
		//Check command line arguments for serverhost(required argument), port, and filePath
		if(args.length == 1)
		{
			//First argument is serverHost
			serverHost = args[0];
		}
		else if (args.length == 2){
			//First argument is serverHost
			serverHost = args[0];
			
			//Second argument can be either serverPort or filePath
			try {
				serverPort = Integer.parseInt(args[1]); //Check if port is an integer
			}
			catch (NumberFormatException nfe)
			{
				System.err.println("[ CLIENT ]> Integer Port is not provided! Default Server port used.");
				
				//Then assume this string is filePath
				filePath = args[1];
			}
		}
		else if (args.length == 3){
			//First argument - serverHost
			serverHost = args[0];
			
			//Second argument - serverPort
			try {
				serverPort = Integer.parseInt(args[1]); //Check if port is an integer
			}
			catch (NumberFormatException nfe)
			{
				System.err.println("[ CLIENT ]> Integer Port is not provided! Default Server port used.");
			}
			
			//Third argument - fileName
			filePath = args[2];
		}
		else
		{
			System.err.println("[ CLIENT ]> Not enough parameters provided. At least serverHost is required.");
			System.exit(-1);
		}
		
		System.out.println("[ CLIENT ]> Server Port in use: " + serverPort);
		System.out.println("[ CLIENT ]> FilePath: " + filePath);
		
		//Define a socket
		Socket socket = null;
		
		//Define input and output streams
		BufferedReader socketInStream = null; //Reads data received over the socket's inputStream
		DataOutputStream socketOutStream = null; //Writes data over the socket's outputStream
		
		FileOutputStream fos = null; //Writes content of the responded file in a file
		
		try {
			
			//Get inet address of the serverHost
			InetAddress serverInet = InetAddress.getByName(serverHost);
			
			//Connect to the server
			socket = new Socket(serverInet, serverPort);
			System.out.println("[ CLIENT ]> Server connected at " + serverHost + ":" + serverPort);
			
			//Reference to socket's inputStream
			socketInStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			//Reference to socket's outputStream
			socketOutStream = new DataOutputStream(socket.getOutputStream());

			//Send a HTTP GET request
			String requestLine = "GET" + SP + filePath + SP +"HTTP/1.0" + CRLF;
			System.out.println("[ CLIENT ]> Sending HTTP GET request: " + requestLine);
			
			//Send the requestLine
			socketOutStream.writeBytes(requestLine);
			
			//Send an empty line
			socketOutStream.writeBytes(CRLF);
			
			//Flush out output stream
			socketOutStream.flush();
			
			System.out.println("[ CLIENT ]> Server response awaited");
			//Extract response Code
			String responseLine = socketInStream.readLine();
			System.out.println("[ CLIENT ]> Received HTTP Response with status line: " + responseLine);

			//Extract content-type of the response
			String contentType = socketInStream.readLine();
			System.out.println("[ CLIENT ]> Received " + contentType);

			//Read a blank line i.e. CRLF
			socketInStream.readLine();

			System.out.println("[ CLIENT ]> Response Body Received:");
			//Reading content body
			StringBuilder content = new StringBuilder();
			String res;
			while((res = socketInStream.readLine()) != null)
			{
				//Saving content to a buffer
				content.append(res + "\n");
				
				//Printing the content
				System.out.println(res);
			}
			
			//Get a name of the file from the response
			String fileName = getFileName(content.toString());
			
			//Open a outputstream to the fileName. If the file doesn't exist, a new one is created
			fos = new FileOutputStream(fileName);
			
			fos.write(content.toString().getBytes());
			fos.flush();
			
			System.out.println("[ CLIENT ]> HTTP Response received. File Created: " + fileName);

		} catch (IllegalArgumentException iae) {
			System.err.println("[ CLIENT ]> EXCEPTION caught in connecting to the SERVER: " + iae.getMessage());
		} catch (IOException e) {
			System.err.println("[ CLIENT ]> ERROR caught " + e);
		}
		finally {
			try {
				//Close all resources
				if (socketInStream != null) {
					socketInStream.close();
				}
				if (socketOutStream != null) {
					socketOutStream.close();
				}
				if (fos != null) {
					fos.close();
				}
				if (socket != null) {
					socket.close();
					System.out.println("[ CLIENT ]> Connection closed successfully!.");
				}
			} catch (IOException e) {
				System.err.println("[ CLIENT ]> EXCEPTION caught in closing resource." + e);
			}
		}
	}

	/**
	 * Returns a file name from the html content.
	 * Generally it is the value of the <title> tag
	 * @param content
	 * @return fileName
	*/
	private static String getFileName(String content)
	{
		//Default filename if <title> tag is empty
		String filename = "";
		
		filename = content.substring(content.indexOf("<title>")+("<title>").length(), content.indexOf("</title>"));
		
		if(filename.equals(""))
		{
			filename = "index";
		}
		
		filename = filename+".html";
		
		return filename;
	}
}
