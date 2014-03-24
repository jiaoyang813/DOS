package project2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class client {
	public static void main(String[] args) throws IOException
	{
		System.out.println("Client Started");
		int listenPort;
	    boolean isClientRunning = true;
	    String userInput = null;
	    BufferedReader stdIn =
	            new BufferedReader(new InputStreamReader(System.in));
	    String clientIp = Inet4Address.getLocalHost().getHostAddress();
	    while(isClientRunning)
	    {
	    	System.out.println("Usage: [ServerName] [Port] to connect");
	    	System.out.println("Or Enter SHUTDOWN to Terminate:");
	    	System.out.print(">>");
		    userInput = stdIn.readLine();
		    String[] token = userInput.split(" ");
		    String serverName = null;// server to connect
		    int portNumber = -1;
		    if(token.length == 2)
		    {
		    	if(token[0] != null&& Integer.parseInt(token[1]) >0)
		    	{
			    	serverName = token[0];
			    	portNumber = Integer.parseInt(token[1]);
			    	//System.out.println("send request: "+request);
		    	}
		    }
		    else if(token.length == 1)
		    {
		    	if(token[0].equals("SHUTDOWN"))
		    	{
		    		System.out.println("CLIENT SHUTDOWN");
		    		stdIn.close();
		    		isClientRunning = false;
		    		System.exit(1);
		    	}
		    	else
		    	{	
		    		System.err.println("Something Weird");
		    		System.exit(0);
		    	}
		    		
		    }else
		    {
		    	System.err.println("Something Weird");
		    	System.exit(0);
		    }
		    System.out.println("Enter Listenning Port: ");
		    System.out.print(">>");
		    listenPort =Integer.parseInt( stdIn.readLine());
			    try (
			    Socket clientSocket = new Socket(serverName, portNumber);
			    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
			    BufferedReader in = new BufferedReader(
			        new InputStreamReader(clientSocket.getInputStream()));
			) { 
			    String fromServer;
			    fromServer = in.readLine();
			    String fromUser="";
			    out.println("REGISTER "+ clientIp+" "+listenPort);
		        while ((fromServer = in.readLine()) != null) {
		        	if (fromUser.equals("BYE"))
		            {
		        		out.println(fromUser);
		            	break;
		            }
		        	//if(fromServer.equals("NOFILE"))
		        	//{
		        		if(fromServer.equals("FILENOTEXIST"))
		        		{	
		        			System.out.println("File Not Exist");
			        		System.out.print(">>");
				            fromUser = stdIn.readLine();
				            if (fromUser != null) {
				            	out.println(fromUser);
				            }else{
				            	System.out.println("NULL COMMAND");
				            	out.println("NULL CMD");
				            }
		        		}
		        		else
			        	if(fromServer.equals("FOUND"))
			        	{
			        		//use a default port number, change later???!!!!
			        		try (  ServerSocket socket2 = new ServerSocket(listenPort);
			        	            Socket othersocket  = socket2.accept();
			        	            PrintWriter sout =
			        	                new PrintWriter(othersocket.getOutputStream(), true);
			        	            BufferedReader sin = new BufferedReader(
			        	                new InputStreamReader(othersocket.getInputStream()));
			        	        ) { 
			        		  
			        		  //System.out.println(sin.readLine());
			                  sout.println(fromUser);
			                  System.out.println("Incoming info: "+sin.readLine());
			                  sout.println("BYE");
			                  socket2.close();
			                    
			                } catch (IOException e) {
			                    System.err.println("Could not listen on port "+listenPort);
			                    System.exit(-1);
			                }
			        		
			        		System.out.print(">>");
				            fromUser = stdIn.readLine();
				            if (fromUser != null) {
				            	out.println(fromUser);
				                //System.out.println("Client: " + fromUser);
				            }else{
				            	System.out.println("NULL COMMAND");
				            	out.println("NULL CMD");
				            }
			        		
			        	}
		        	//}
		        	else
		        	{	
		        		System.out.println(fromServer);
			        	System.out.print(">>");
			            fromUser = stdIn.readLine();
			            if (fromUser != null) {
			            	out.println(fromUser);
			                //System.out.println("Client: " + fromUser);
			            }else{
			            	System.out.println("NULL COMMAND");
			            	out.println("NULL CMD");
			            }
		            
		        	}
			            
			    }
			      
			} catch (UnknownHostException e) {
			    System.err.println("Don't know about host " +serverName);
			    System.exit(1);
			} catch (IOException e) {
			    System.err.println("Couldn't get I/O for the connection to " +
			        serverName);
			    System.exit(1);
			}
	    
		
		}
    
	}

}
