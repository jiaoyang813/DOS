package project1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.Socket;
import java.net.UnknownHostException;


public class client {
	public static void main(String[] args) throws IOException
	{
	System.out.println("Client Initialized!");
	boolean isClientRunning = true;
    String userInput = "";
    String uploadFileName = "NULL";
    String clientIp = Inet4Address.getLocalHost().getHostAddress(); //client's ip address
    BufferedReader stdIn = 
            new BufferedReader(new InputStreamReader(System.in));
    while(isClientRunning)
    {
    	System.out.println("Usage: [ServerName] [Port] [Request]");
    	System.out.println("Or Enter SHUTDOWN to Terminate:");
    	System.out.print(">>");
	    userInput = stdIn.readLine();
	    String request ="";
	    String[] token = parseUserInput(userInput);
	    String serverName = null;// server name that client want to connect
	    int portNumber = -1;
	    if(token.length >=3)
	    {
	    	if(token[0] != null&& Integer.parseInt(token[1]) >0)
	    	{
		    	serverName = token[0];
		    	portNumber = Integer.parseInt(token[1]);
		    	for(int i = 2; i < token.length;i++)
		    		request += token[i] +" ";
		    	System.out.println("send request: "+request);
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
	    }
	    else
	    {
	    	System.err.println("Something Weird");
	    	isClientRunning = false;
	    }
    	
	    if(serverName.equals(null)||portNumber < 0)
	    {
	    	System.err.println("Invalid server name and port number");
	    	System.exit(1);
	    }
    	
	    try (
	    Socket clientSocket = new Socket(serverName, portNumber);
	    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
	    BufferedReader in = new BufferedReader(
	        new InputStreamReader(clientSocket.getInputStream()));
		) {
		    //boolean running = true;//current tcp connection is running
		   // boolean isConnected = false;
		    portNumber = clientSocket.getPort();
		    String fromServer;
		    //String fromUser;
		    // connect to server
        	fromServer = in.readLine();
        	System.out.println("FromServer: " + fromServer);
        	//register the client
            out.println("REGISTER "+ clientIp + " " + portNumber);
            fromServer = in.readLine();
    		System.out.println("FromServer: " + fromServer);
    		
    		//send request
            out.println(request);
    		fromServer = in.readLine();
    		System.out.println("FromServer: " + fromServer);
    		
    		if(fromServer.equals("UPLOAD START"))
        	{ 
    		 	//upload filename: localhost 50000 upload file.txt
            	if(token[2].equals("UPLOAD") && token.length == 4)
            	{	
            		out.println(token[2]+" "+token[3]);
            		uploadFileName = token[3];
            	}
        		uploadFile(uploadFileName,in, out);
        	}
    		//disconnect server
    		out.println("BYE");
    		serverName = null;
    		portNumber = -1;
    		clientSocket.close();           		

        	if(fromServer.equals("SERVER SHUTDOWN") )
        	{
        		//isConnected = false;
        		serverName = null;
        		portNumber = -1;
        		clientSocket.close();
        		//running = false;
        	}	
	
		} catch (UnknownHostException e) {
		    System.err.println("Don't know about server " + serverName);
		    System.exit(1);
		} catch (IOException e) {
		    System.err.println("Couldn't get I/O for the connection to " +
		        serverName);
		    System.exit(1);
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
	
	public static void uploadFile(String FileName, BufferedReader in,PrintWriter out)
	{
		String path = System.getProperty("user.dir");
		//System.out.println("Start Upload");
		System.out.println(path+"/file/"+FileName);
		boolean fileExist = new File(path+"/file/"+FileName).isFile();
		if(!fileExist)
		{
			System.err.println("No such file");
		}
    	//out.println("UPLOAD START");
		try (BufferedReader inputFile = 
				new BufferedReader(new FileReader(path+"/file/"+FileName)))
		{
			String inputLine;
			while ((inputLine = inputFile.readLine()) != null) {
				//System.out.println("From server:"+in.readLine());
				//System.out.println("send: " + inputLine);
				out.println(inputLine);
			}
			
			System.out.println(FileName + " UPLOADED TO SERVER!");
			out.println("UPLOADED");
			inputFile.close();
		} catch (IOException ex) {
			  // report
			}
	}

}
