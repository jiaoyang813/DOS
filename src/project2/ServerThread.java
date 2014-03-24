package project2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashSet;

public class ServerThread extends Thread {
	public int listenPort;
	public String serverName;
	//client being servicing
	public String client;
	public int clientPort;
	private Socket socket = null;
	public String IncomingServer;
    public ServerThread(Socket socket,String servername, int listenningPort) {
        super(socket.getLocalAddress().getHostName()+":"+listenningPort);
        listenPort = listenningPort;
        serverName = servername;
        this.socket = socket;
    }
    public void run() {
        try (
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
        ) {
            String inputLine, outputLine;
            ProcessRequest clientservice = new ProcessRequest();
            clientservice.db.setPath(System.getProperty("user.dir")+
		          "/data/"+serverName+listenPort);
            String serverName = InetAddress.getLocalHost().getHostName();
            HashSet<String> servers = new HashSet<String>();
            int port = socket.getPort();
            boolean hasFile = false; 
            //get peer server from config.txt
            try (BufferedReader inputFile = 
    				new BufferedReader(new FileReader(System.getProperty("user.dir")+
	        		          "/data/"+serverName+listenPort+"/config.txt")))// right path!!!!!!!!
    		{
    			String input;
    			while ((input = inputFile.readLine()) != null) 
    				servers.add(input);
    			inputFile.close();
    		} catch (IOException ex) {
    			  // report
    			}
            
            //auto register client
            out.println("Welcome"); 
            inputLine = in.readLine(); //register ip
            if(inputLine.split(" ")[0].equals("GETPEER"))
            {
            	String hostName = InetAddress.getByName(inputLine.split(" ")[1]).getHostName();
            	String temppeer = hostName+" "+inputLine.split(" ")[2];
            	
            	for(String s: servers)
            	{
            		out.println(s);
            		in.readLine();
            	}
            	
            	if( servers.size() < 3)
            	{
            		servers.add(temppeer);
            	}
            	out.println("ALLSEND");
            	//System.out.println("peers ALLSEND");
            	//update config.txt
            	try {
 				   BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
 				          new FileOutputStream(System.getProperty("user.dir")+
 		        		          "/data/"+serverName+listenPort+"/config.txt"), "utf-8"));
 					//write it to disc
 				   
 				   for(String s : servers)
 					{
 					  // if(peers.get(i).equals(" "))
 					    writer.write(s);
 					    writer.newLine();	
 					}	   
 				    writer.close();
 				} catch (IOException ex) {
 				  // report
 				}
            	socket.close();//finish getpeer job
            }
            else
            {
            outputLine = clientservice.processInputline(inputLine.split(" "));
            out.println(outputLine +" "+ port); // welcome client ip port
            System.out.println("Welcome client "+ clientservice.client+":" +port);
            while ((inputLine = in.readLine()) != null) {
                if (inputLine.equals("BYE"))
                {
                	System.out.println("");
                	out.println(this.getName() +":BYE");
                	System.out.println("Server is listenning on port "+listenPort);
                	//System.exit(1);
                    break;
                } 
                
                if(inputLine.split(" ")[0].equals("HELP"))
                {
                	IncomingServer = clientservice.client+" "+clientservice.clientlistenport;
                }
                outputLine = clientservice.processInputline(inputLine.split(" "));
                System.out.println(clientservice.client +":"+port+" "+outputLine);
                if(outputLine.equals("SERVER SHUTDOWN"))
                {
                	System.exit(1);	
                }
                
                if(inputLine.split(" ")[0].equals("TRANSFER"))
                {
                	//should out.println() to notify the server
                	String client = inputLine.split(" ")[1];
                	//the port which this client is listenning.
                	int clientPort = Integer.parseInt(inputLine.split(" ")[2]);
                	 try (
             			    Socket clientSocket = new Socket(client, clientPort);
             			    PrintWriter cout = new PrintWriter(clientSocket.getOutputStream(), true);
             			    BufferedReader cin = new BufferedReader(
             			        new InputStreamReader(clientSocket.getInputStream()));
             			) {
                		 outputLine = clientservice.processInputline(cin.readLine().split(" "));
                		 System.out.println(outputLine);
                		 cout.println(outputLine);
                		 break;
                	 }
                	 catch (IOException e) {
		                    System.err.println("Could not connect "+client+":"+clientPort);
		                    System.exit(-1);
		                }
                }
                else if(outputLine.equals("NOFILE"))
                {
                	//make request to other sever
                	String fromOtherServer;
                	int serverPort;
                	String otherServer;
                	//get servers from config.txt file
                	HashSet<String> badserver = new HashSet<String>();
                	
                	for(String s : servers)
                	{
                		if(!s.equals(IncomingServer))
                		{
	                		otherServer = s.split(" ")[0];
	                		serverPort = Integer.parseInt(s.split(" ")[1]);
	                	    try (
	            			    Socket other = new Socket(otherServer, serverPort);
	            			    PrintWriter o = new PrintWriter(other.getOutputStream(), true);
	            			    BufferedReader i = new BufferedReader(
	            			        new InputStreamReader(other.getInputStream()));
	            			) {
	                	    	// connect to other server to find file
		                		fromOtherServer = i.readLine();
		                		//ask other server to find FileName
		                		o.println("REGISTER "+ 
		                		    InetAddress.getByName(serverName).getHostAddress()+" "+listenPort);
		                		fromOtherServer = i.readLine();
		                		System.out.println("Otherserver: "+fromOtherServer);
		                		o.println("HELP "+clientservice.FileName);
		                		fromOtherServer = i.readLine();
		                		System.out.println("Otherserver: "+fromOtherServer);
		                		if(fromOtherServer.split(" ")[0].equals("HASFILE"))
		                		{
		                			hasFile = true;
		                			out.println("FOUND");
		                			// help localhost default port?????	
		                			o.println("TRANSFER "+ clientservice.client +" "
		                			          + clientservice.clientlistenport); 	
		                			break;
		                		}
		                		else if(fromOtherServer.split(" ")[0].equals("CANNOTHELP"))
		                		{
		                			
		                		}
		                		
	                				
							} catch (UnknownHostException e) {
								    System.err.println("Don't know about host");
								    badserver.add(s);
								    
								} catch (IOException e) {
								    System.err.println("Couldn't get I/O for the connection");
								    badserver.add(s);
								   //System.exit(1);
								}
                	}
                	}
                	//update config.txt
                	servers.removeAll(badserver);
                	try {
      				   BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
      				          new FileOutputStream(System.getProperty("user.dir")+
      		        		          "/data/"+serverName+listenPort+"/config.txt"), "utf-8"));
      					//write it to disc
      				   for(String s : servers){
      					    writer.write(s);
      					    writer.newLine();	
      					}	   
      				    writer.close();
      				} catch (IOException ex) {
      				  // report
      				}

                	if(!hasFile)
                		out.println("FILENOTEXIST");//if nobody has the file
                		
                }
                else
                	out.println(outputLine); 
                	// change later!!! different answer depends on hasFile
              }
          }
            socket.close();
        } catch (IOException e) {
            e.printStackTrace(); 
        }
        
    
    }
     
}
