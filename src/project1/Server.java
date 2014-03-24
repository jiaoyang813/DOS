package project1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	public static void main(String[] args) throws IOException
	{
		int portNumber = -1;
		boolean serverRunning = true;
		//boolean isRunning = true;
		String fileName = null;
		boolean isUploading = false;
		BufferedReader stdIn =
	            new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Server Initialized!");
		while(serverRunning){
				
		if(portNumber < 0)
		{
			System.out.println("Usage: Designate a port number the server will listen on");
			System.out.println("Please Enter Port Number(1025~65535):");
			System.out.print(">>");				
			portNumber = Integer.parseInt(stdIn.readLine());
			System.out.println("Server is listenning on port "+portNumber);
		}
		else
		{
			System.out.println("Server is listenning on port "+portNumber);
		}
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
            out.println("TCP Connection Established with Server!");
            //create a new parsing service
            ProcessRequest clientservice = new ProcessRequest();
            //real port used for communication
            int realPort = clientSocket.getPort();
    
            inputLine = in.readLine();
            outputLine = clientservice.processInputline(parseUserInput(inputLine));
            clientName = clientservice.client;
            System.out.println("Welcome Client "+clientName + " "+ realPort);
            out.println("Welcome "+clientName + " "+ realPort);
            
        	while((inputLine = in.readLine()) != null)//
        	{
        		//inputLine = in.readLine();
        		if(isUploading)
        		{
        			String firstLine = inputLine;
        			String lastLine;
        			lastLine = uploadFile(fileName, firstLine, in, out);
        			isUploading = false;
        			inputLine = lastLine;
        			//out.println(fileName + " RECEIVED");
        		}
        		else
        		{
	        		String[] token = parseUserInput(inputLine);
	        		
	        		if(token[0].equals("UPLOAD"))
	        		{
	        			fileName = token[1];
	        			System.out.println(clientName +" "+realPort+ ": "+inputLine);
	        			clientservice.uploadFile(fileName);;
	        			out.println("UPLOAD START");
	        			isUploading = true;
	        			//out.println("UPLOAD DONE");
	        		}
	        		else
	        		{
		            	System.out.println(clientName +" "+realPort+ ": "+inputLine);
		            	outputLine = clientservice.processInputline(token);
		            	
		                if(outputLine.equals("BYE"))
		                {		                	
		                	out.println(outputLine);
		                	break;
		                }
		                else if(outputLine.equals("SERVER SHUTDOWN"))
		                {	
		                	out.println(outputLine);
		                	serverRunning = false;
		                	in.close();
		                	out.close();
		                	stdIn.close();
		                	clientSocket.close();
		                	System.exit(1);
		                }
		                else 
		                	out.println(outputLine);
		                out.flush();
	        		}
        		}
                
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
	
	public static String uploadFile(String FileName,String firstLine, BufferedReader in, PrintWriter out)
	{
	
		String input = null;
		String path = System.getProperty("user.dir");
		try {
			   BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
			          new FileOutputStream(path+"/data/"+FileName), "utf-8"));//+".txt"???????
			 
				//write it to disc 
			   //writer.write(firstLine);
			   //writer.newLine();
			    while(!(input = in.readLine()).equals("UPLOADED"))
			    {	 
			    	writer.write(input);
			    	System.out.println("receive " + input);
			    	 //may add a new line at file bottom 
			    	writer.newLine();	 
			    }
			    System.out.println("UPLOAD DONE!");
			    //System.out.println("Last Input " + input);
			    writer.close();
			    
			} catch (IOException ex) {
			  // report
			}
		
		return input;
				
	}

}
