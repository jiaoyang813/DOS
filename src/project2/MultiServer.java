package project2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;

public class MultiServer {
	public static void main(String[] args) throws IOException {
		String userInput;
		HashSet<String> peers = new HashSet<String>();//other servers
		BufferedReader stdIn =
	            new BufferedReader(new InputStreamReader(System.in));
		int portNumber=-1;
		System.out.println("Usage: Designate a port number the server will listen on");
		System.out.println("Please Enter Port Number(1025~65535):");
		System.out.print(">>");				
		portNumber = Integer.parseInt(stdIn.readLine());
		String serverName = InetAddress.getLocalHost().getHostName();
		//System.out.println(serverName);
        File dir = new File(System.getProperty("user.dir")+
        		          "/data/"+serverName+portNumber);
        if(!dir.isDirectory())
        	dir.mkdir();
		boolean hasConfigFile = new File(System.getProperty("user.dir")+
		          "/data/"+serverName+portNumber+"/config.txt").isFile();
		if(!hasConfigFile) //if don't have config, use cmd input
		{
		   System.out.println("Please Enter: [ServerName] [Port]");	//enter one server or a well known one
		   for(int i = 0; i < 1; i++)
		   {
				System.out.print(">>");	
			    userInput = stdIn.readLine();
			    String[] temp = userInput.split(" ");
			    String host;
			    if(temp[0].equals("localhost"))
			       host = InetAddress.getLocalHost().getHostName();	
			    else 
			       host =  InetAddress.getByName(temp[0]).getHostName();
			    peers.add(host+" "+temp[1]); 
		   }
		   peers.remove(serverName+" "+portNumber);
		
		}else //read from config.txt
		{
			try (BufferedReader inputFile = 
					new BufferedReader(new FileReader(System.getProperty("user.dir")+
	        		          "/data/"+serverName+portNumber+"/config.txt")))// right path!!!!!!!!
			{
				String inputLine;
				while ((inputLine = inputFile.readLine()) != null) {
					peers.add(inputLine);
				}
				inputFile.close();
			} catch (IOException ex) {
				  // report
				}
		}
		
		//contact the other server if has less than 3 peers
		if(peers.size()<3)
		{
			ArrayList<String> temp_peer = new ArrayList<String>();
			for(String s:peers)
			{
				String[] token = s.split(" ");
				String sname = token[0];
				int sport = Integer.parseInt(token[1]);
				if(!s.equals(serverName+" "+portNumber))
				{
				  String fromOtherServer;
				try (
        			    Socket other = new Socket(sname, sport);
        			    PrintWriter o = new PrintWriter(other.getOutputStream(), true);
        			    BufferedReader i = new BufferedReader(
        			        new InputStreamReader(other.getInputStream()));
        			) {
					
            		fromOtherServer = i.readLine();// connect to other server
            		System.out.println(fromOtherServer);
            		o.println("GETPEER " + serverName +" "+portNumber);//i want to get some peers from you
            		String instr;
            		while(!(instr =i.readLine()).equals("ALLSEND")&&peers.size() + temp_peer.size()<3)
            		{
            			System.out.println(instr);
            			temp_peer.add(instr);
            			o.println("GET ONE");
            		}
            		if(peers.size() >= 3)
            		{
            			other.close();//close socket to peer server
            			break;
            		}
            		
				} catch (UnknownHostException e) {
						    System.err.println("Don't know about host");
						    //delete the server if it's down
						    System.exit(1);
			    } catch (IOException e) {
						    System.err.println("Couldn't get I/O for the connection");
						    System.exit(1);
				}
				
				}

			}
			
			peers.addAll(temp_peer);
			try {
				   BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
				          new FileOutputStream(System.getProperty("user.dir")+
		        		          "/data/"+serverName+portNumber+"/config.txt"), "utf-8"));
					//write it to disc
				   for(String s : peers)
					{
					  // if(peers.get(i).equals(" "))
					    writer.write(s);
					    writer.newLine();	
					}	   
				    writer.close();
				} catch (IOException ex) {
				  // report
				}
		}
		
		System.out.println("Server is listenning on port "+portNumber);
        boolean listening = true;
        
        try (ServerSocket serverSocket = new ServerSocket(portNumber)) { 
            while (listening) {
                new ServerThread(serverSocket.accept(),serverName,portNumber).start();
            }
        } catch (IOException e) {
            System.err.println("Could not listen on port " + portNumber);
            System.exit(-1);
        }
		
	}

}
