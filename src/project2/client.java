package project2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class client {
	public static void main(String[] args) throws IOException
	{
		
		if (args.length != 2) {
            System.err.println(
                "Usage: java EchoClient <host name> <port number>");
            System.exit(1);
        }	
	System.out.println("Client Started");
	String hostName = args[0];
    int portNumber = Integer.parseInt(args[1]);
    String userInput = null;
    
    BufferedReader stdIn =
            new BufferedReader(new InputStreamReader(System.in));
    while(true)
    {
		    try (
		    Socket kkSocket = new Socket(hostName, portNumber);
		    PrintWriter out = new PrintWriter(kkSocket.getOutputStream(), true);
		    BufferedReader in = new BufferedReader(
		        new InputStreamReader(kkSocket.getInputStream()));
		) {
		    
		    String fromServer;
		    String fromUser=null;
		    //System.out.println("Client Started");
		   
		        while ((fromServer = in.readLine()) != null&&!fromServer.equals("BYE")) {
		            System.out.println("Server: " + fromServer);
		            
		            fromUser = stdIn.readLine();
		            if (fromUser != null) {
		            	if(fromUser.equals("REGISTER"))
		            	{
		            		out.println(hostName + " " + portNumber);
		            	}
		            	else
		            		out.println(fromUser);
		            	if (fromServer.equals("BYE"))
			            {
			            	break;
			            }
		                //System.out.println("Client: " + fromUser);
		                
		            }
		            
		        }
		      
		} catch (UnknownHostException e) {
		    System.err.println("Don't know about host " + hostName);
		    System.exit(1);
		} catch (IOException e) {
		    System.err.println("Couldn't get I/O for the connection to " +
		        hostName);
		    System.exit(1);
		}
		
		    
		    System.out.println("Enter Server Name and port number to connect:");
		    userInput = stdIn.readLine();
		    String[] token = parseUserInput(userInput);
		    if(token.length == 2)
		    {
		    	hostName = token[0];
		    	portNumber = Integer.parseInt(token[1]);
		    }
		    if(token.length == 1)
		    {
		    	if(token[0].equals("SHUTDOWN"))
		    	{
		    		System.out.println("CLIENT SHUTDOWN");
		    		break;
		    	}
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
