/*
Developed by : Ishita Kapur
UTA ID : 1001753123
CSE 5344, Fall 2019

HttpRequestHandler Class - Handles processing for each of the client in a separate thread. This class 
                           implements Runnable interface and overrides the public void run() method.

References:
            http://docs.oracle.com/javase/tutorial/essential/concurrency/runthread.html - for thread tutorial
            http://www.oracle.com/technetwork/java/socket-140484.html - for socket communications
            https://javarevisited.blogspot.com/2015/06/how-to-create-http-server-in-java-serversocket-example.html - for request handling
*/

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.net.Socket;

public class HttpRequestHandler implements Runnable {

	private Socket clientSocket; //Reference to the clientSocket passed by the WebServer after client is connected
	private int clientID; //Unique clientID passed by WebServer for logging purpose

	private final String CRLF = "\r\n"; //Carriage return line feed
	private final String SP = " "; //Status line parts separator
	
	/**
	 * Constructor for HttpRequestHandler to set clientSocket and clientID
	 * @param cs
	 * @param cID
	*/
	public HttpRequestHandler(Socket cs, int cID) {
		this.clientSocket = cs;
		this.clientID = cID;
	}

	@Override
	public void run() {
		
		//Define input and output streams
		BufferedReader socketInStream = null; //Reads data received over the socket's inputStream
		DataOutputStream socketOutStream = null; //Writes data over the socket's outputStream
		
		FileInputStream fis = null; //Reads file from the local file system
		
		try {
			//Get a reference to clientSocket's inputStream
			socketInStream = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			
			//Get a reference to clientSocket's outputStream
			socketOutStream = new DataOutputStream(clientSocket.getOutputStream());

			//Read a request from socket inputStream
			String packet = socketInStream.readLine();
			
			//Check if request is not null
			if(packet != null)
			{
				System.out.println("[ SERVER - CLIENT"+clientID+" ]> Request received: " + packet);

				/* HTTP Request Format:
				                GET index.html HTTP/1.0 CRLF
				*/
				
				//Split request line based on single whitespace into three parts
				String[] msgParts = packet.split(SP);
				
				//Check if the request type is GET
				if (msgParts[0].equals("GET") && msgParts.length == 3) {
					
					//Get the path of the requested file from the request
					String filePath = msgParts[1];
					
					//Check if filePath starts with a forward slash "/". If not, add a forward slash and make it relative to the current file path
					if(filePath.indexOf("/") != 0)
					{	
                        //FilePath does not start with a forward slash, add a forward slash
						filePath = "/" + filePath;
					}
					
					
					System.out.println("[ SERVER - CLIENT"+clientID+" ]> Requested filePath: " + filePath);
					
					//If requested filePath is not specified or requesting a default index file
					if(filePath.equals("/"))
					{
						System.out.println("[ SERVER - CLIENT"+clientID+" ]> Default /index.html file as response");
						
						//Set filePath to the default index.html file
						filePath = filePath + "index.html";
					}
					
					//Make the filePath relative to the current location
					filePath = "." + filePath;

					//Initialize a File object using filePath
					File file = new File(filePath);
					try {
						//Check if file with filePath exists on this server
						if (file.isFile() && file.exists()) {
							
							//Create a HTTP response and send it back to the client
							
                            /*
                            HTTP Response Format:
							HTTP/1.0 200 OK CRLF
							Content-type: text/html CRLF
							CRLF
							FILE_CONTENT....
							FILE_CONTENT....
							FILE_CONTENT....
							*/
							
							//Write a status line on the response, since the requested file exists, we will send a 200 OK response
							String responseLine = "HTTP/1.0" + SP + "200" + SP + "OK" + CRLF;
							socketOutStream.writeBytes(responseLine);

							//Write the content type header line
							socketOutStream.writeBytes("Content-type: " + getContentType(filePath) + CRLF);
							
							//Write a blank line representing end of response header
							socketOutStream.writeBytes(CRLF);
							
							//Open the requested file
							fis = new FileInputStream(file);

							//Initialize a buffer of size 1K.
							byte[] buffer = new byte[1024];
							int bytes = 0;
							
							//Start writing content of the requested file into the socket's output stream.
							while((bytes = fis.read(buffer)) != -1 ) {
								socketOutStream.write(buffer, 0, bytes);
							}
							
							System.out.println("[ SERVER - CLIENT"+clientID+" ]> Sending Response with status line: " + responseLine);
							//flush outputstream
							socketOutStream.flush();
							System.out.println("[ SERVER - CLIENT"+clientID+" ]> HTTP Response sent");
							
						} else {
							//The requested file does not exist on this server
							System.out.println("[ SERVER - CLIENT"+clientID+" ]> ERROR caught: Requested filePath " + filePath + " does not exist");

							//write a status line on the response with 404 Not Found response
							String responseLine = "HTTP/1.0" + SP + "404" + SP + "Not Found" + CRLF;
							socketOutStream.writeBytes(responseLine);

							//write content type header line
							socketOutStream.writeBytes("Content-type: text/html" + CRLF);
							
							//write a blank line representing end of response header
							socketOutStream.writeBytes(CRLF);
							
							//Send content of the errorFile
							socketOutStream.writeBytes(getErrorFile());
							
							System.out.println("[ SERVER - CLIENT"+clientID+" ]> Sending Response with status line: " + responseLine);
							
							//Flush outputstream
							socketOutStream.flush();
							System.out.println("[ SERVER - CLIENT"+clientID+" ]> HTTP Response sent");
						}
						
					} catch (FileNotFoundException e) {
						System.err.println("[ SERVER - CLIENT"+clientID+" ]> EXCEPTION caught: Requested filePath " + filePath + " does not exist");
					} catch (IOException e) {
						System.err.println("[ SERVER - CLIENT"+clientID+" ]> EXCEPTION caught in processing request." + e.getMessage());
					}
				} else {
					System.err.println("[ SERVER - CLIENT"+clientID+" ]> Invalid HTTP GET Request. " + msgParts[0]);
				}
			}
			else
			{
				//Therefore I discard those unknown requests, since sometimes browser send other request like favicon etc.
				System.err.println("[ SERVER - CLIENT"+clientID+" ]> Discarding a NULL/unknown HTTP request.");
			}

		} catch (IOException e) 
		{
			System.err.println("[ SERVER - CLIENT"+clientID+" ]> EXCEPTION caught in processing request." + e.getMessage());
			
		} finally {
			//Close the resources
			try {
				if (fis != null) {
					fis.close();
				}
				if (socketInStream != null) {
					socketInStream.close();
				}
				if (socketOutStream != null) {
					socketOutStream.close();
				}
				if (clientSocket != null) {
					clientSocket.close();
					System.out.println("[ SERVER - CLIENT"+clientID+" ]> Closing the connection.\n");
				}
			} catch (IOException e) {
				System.err.println("[ SERVER - CLIENT"+clientID+" ]> EXCEPTION caught in closing resource." + e);
			}
		}
	}
	
	/**
	 * Get Content-type of the file using its extension
	 * @param filePath
	 * @return content type
	*/
	private String getContentType(String filePath)
	{
		//check if file type is html
		if(filePath.endsWith(".html") || filePath.endsWith(".htm"))
		{
			return "text/html";
		}
		//otherwise, a binary file
		return "application/octet-stream";
	}
	
	/**
	 * Get content of a general 404 error file
	 * @return errorFile content
	*/
	private String getErrorFile ()
	{
		String errorFileContent = 	"<!doctype html>" + "\n" +
									"<html lang=\"en\">" + "\n" +
									"<head>" + "\n" +
									"    <meta charset=\"UTF-8\">" + "\n" +
									"    <title>Error 404</title>" + "\n" +
									"</head>" + "\n" +
									"<body>" + "\n" +
									"    <b>ErrorCode:</b> 404" + "\n" +
									"    <br>" + "\n" +
									"    <b>Error Message:</b> The requested file does not exist on this server." + "\n" +
									"</body>" + "\n" +
									"</html>";
		return errorFileContent;
	}
}
