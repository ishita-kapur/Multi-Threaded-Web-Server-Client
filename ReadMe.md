Multi_Threaded_Web_Server_Client

Developed by : Ishita Kapur
UTA ID : 1001753123
CSE 5344, Fall 2019

-----------
An application in Java to build a Simple Web Client and a Multithreaded Web Server.

Tools used for Development:    
1. Programming Language: Java (jdk 1.13)
2. Text Editors: Notepad++, Visual Studio Code
3. Web Server Environment: Apache Tomcat 
4. External Packages: No dependencies
5. Command Line Interface: Windows command prompt(to execute the program)

Project Structure:
Multi_Threaded_Web_Server_Client	
	Source files for the application.	
    - `InitializeServer.java`: Code to initialize the WebServer at a user provided port or a default port(8080).
    - `WebServer.java`: Code to implement a multhreaded server to listen to multiple client requests. Once a client is connected, the processing is handed over to a separate RequestHandler thread.
    - `HttpRequestHandler.java`: Code to processes client's HTTP request in separate threads.
    - `WebClient.java`: Code for a web client which communicates with the server on a specific ip:port address and requests a file on the server.
	- `index.html`: Default html file sent to the client for a GET request containing "/" filepath.

Project Execution:
Perform the following steps inside the `Multi_Threaded_Web_Server_Client` folder sequentially on Command Prompt:
Step 1: Delete the previous class files (if existing from previous compilation the source codes).    
			Command : `del *.class`

Step 2: Compile all the source codes in the folder.       
			Command : `javac *.java`            
			
		Note: In case the command results in an error stating ''javac' is not recognized as an internal or external command, operable program or batch file.' set the path of the jdk bin to the present folder.
				Command : `set path=<path/to/jdk-bin>`

Step 3: Start the Tomcat server to view outputs on the browser.
		Command: i. `cd <\path\where\tomcat\is\installed>`
		         ii.`startup.bat`

Step 4: Execute the server by defining the port number on which the server has to run (Example : Port 6789). Command takes one optional argument(port number).
		Command format : `java <filename> <port_number>`
				filename : `InitializeServer`
				Command : `java InitializeServer 6789`
		If no port number is specified, server runs on default port number (8080)
				Command : `java InitializeServer`
		
		Note: If the port number is already in use message is displayed accordingly.

Step 5: Open another window of the command prompt. Navigate to the same folder. Set the path for `javac` if required

Step 6: Execute the web client. Command takes one mandatory argument(hostname/IP address) and two optional arguments(port number and path of the file).
		Command format : `java <filename> <hostname/IP_address> <port_number> <path/to/file>`
		filename : `WebClient`
		If port number is specified but path to file is not specified
				Command : `java WebClient localhost 6789`
		If no port number and path of the file is specified the default values are used. Default port number (8080) and Default file path "/"
				Command : `java WebClient localhost`
		If port number and file path both are specified
				Command : `java WebClient localhost 6789 /path/to/file`
				
		Note: If incorrect port number is specified message is displayed accordingly.
				
		If the requested file `/path/to/file` (relative to the `HttpRequestHandler.java` class) exists on the server the following steps take place:
		- Server returns a `HTTP/1.0 200 OK` response with appropriate content-type and file content. 
		- Web client extracts status line and displays it on the command prompt.
		- The content of the response body of the requested file is extracted and written to an html file in client source code folder. HTML file takes the name from the value of `<title>` tag of the html returned. Default filename `index.html` is used if `<title>` does not have a value.

		Server returns a `HTTP/1.0 404 Not Found` response if the requested file doesn't exist on the server.
		
Outputs:
		Screenshot of the outputs have been shown in the Outputs.pdf in this folder.
		
References:
1. Project Specification document provided for this project on Canvas UTA.
2. Text Book: Computer Networking. A Top Down Approach. Fifth Edition by James F. Kurose, Keith W. Ross. Chapter 2.
3. https://www.youtube.com/watch?v=RQ2v0CSV4tY - for implementing multithreading in servers
4. http://docs.oracle.com/javase/tutorial/essential/concurrency/runthread.html - for thread tutorials
5. http://www.oracle.com/technetwork/java/socket-140484.html - for socket communications
6. https://www.geeksforgeeks.org/introducing-threads-socket-programming-java/ - for implementing threads
7. https://www.javatpoint.com/socket-programming - for socket programming
