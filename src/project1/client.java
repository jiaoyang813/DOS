package project1;

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
		    Socket clientSocket = new Socket(hostName, portNumber);
		    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
		    BufferedReader in = new BufferedReader(
		        new InputStreamReader(clientSocket.getInputStream()));
		) {
		    boolean isConnected = false;
		    String fromServer;
		    String fromUser;
		    //System.out.println("Client Started");
		        while ((fromServer = in.readLine()) != null&&
		        		!fromServer.equals("SERVER SHUTDOWN")&&!fromServer.equals("BYE")) {//
		        	
		        	System.out.println("Server: " + fromServer);
		        	if(!isConnected)
		        	{
		        		out.println("REGISTER "+ hostName + " " + portNumber);
		        		isConnected = true;
		        	}
		        	else
		        	{
			             fromUser = stdIn.readLine();
			             if (fromUser != null)
			             {
			            	out.println(fromUser);
			            	if(fromUser.equals("SERVER SHUTDOWN"))
			            	{	
			            		isConnected = false;
			            		hostName = null;
			            		portNumber = -1;
			            		
			            	}
			            	if (fromUser.equals("BYE"))
				            {
			            		isConnected = false;
			            		hostName = null;
			            		portNumber = -1;
				            }
			               
			            }
		             
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
		  
		    System.out.println("Enter Server Name and Port Number to Connect or Enter SHUTDOWN:");
		    userInput = stdIn.readLine();
		    String[] token = parseUserInput(userInput);
		    if(token.length == 2)
		    {
		    	if(token[0] != null&& Integer.parseInt(token[1]) >0)
		    	{
			    	hostName = token[0];
			    	portNumber = Integer.parseInt(token[1]);
		    	}
		    }
		    else if(token.length == 1)
		    {
		    	if(token[0].equals("SHUTDOWN"))
		    	{
		    		System.out.println("CLIENT SHUTDOWN");
		    		break;
		    	}
		    }
		    else
		    {
		    	System.err.println("Something Weird");
		    	break;
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
