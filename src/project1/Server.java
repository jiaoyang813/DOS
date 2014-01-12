package project1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	public static void main(String[] args) throws IOException
	{
		if (args.length != 1) {
            System.err.println("Usage: java EchoServer <port number>");
            System.exit(1);
        }
		int portNumber = Integer.parseInt(args[0]);
		boolean serverRunning = true;
		System.out.println("Server starts listenning port "+portNumber);
		while(serverRunning){
		try ( 
	            ServerSocket serverSocket = new ServerSocket(portNumber);
	            Socket clientSocket = serverSocket.accept();
	            PrintWriter out =
	                new PrintWriter(clientSocket.getOutputStream(), true);
	            BufferedReader in = new BufferedReader(
	                new InputStreamReader(clientSocket.getInputStream()));
	        ) {
	         
			    //System.out.println("Client:");
	            String inputLine, outputLine;
	            String clientName;
	            out.println("Connection Established!");
	          //create a new parsing service
	            ProcessRequest clientservice = new ProcessRequest();
	            inputLine = in.readLine();
	            outputLine = clientservice.processInputline(parseUserInput(inputLine));
	            clientName = clientservice.client;
	            System.out.println("Welcome Client "+clientName + " "+ portNumber);
	            out.println("Welcome "+clientName + " "+ portNumber);
	            
            	while((inputLine = in.readLine()) != null)
            	{
            		
	            	System.out.println(clientName +" "+portNumber+ ": "+inputLine);
	            	outputLine = clientservice.processInputline(parseUserInput(inputLine));
	            	
	                if(outputLine.equals("BYE"))
	                {
	                	out.println(outputLine);
	                	break;
	                }
	                else if(outputLine.equals("SERVER SHUTDOWN"))
	                {	
	                	out.println(outputLine);
	                	serverRunning = false;
	                }
	                else 
	                	out.println(outputLine);
	                
            	}  
	        } catch (IOException e) {
	            System.out.println("Exception caught when trying to listen on port "
	                + portNumber + " or listening for a connection");
	            System.out.println(e.getMessage());
	        }
		}
		
		}
	
	public static String[] parseUserInput(String in)
	{
		String[] result=null;
		String delim=" ";
		result = in.split(delim);
		return result;
	}

}
