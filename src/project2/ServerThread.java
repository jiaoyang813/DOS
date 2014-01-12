package project2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerThread extends Thread {
	private Socket socket = null;
	 
    public ServerThread(Socket socket) {
        super("ServerThread");
        this.socket = socket;
    }
    
    public void run() {
    	 
        try (
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                new InputStreamReader(
                    socket.getInputStream()));
        ) {
            String inputLine, outputLine;
            out.println("Dummy");
 
            while ((inputLine = in.readLine()) != null) {
               // outputLine = kkp.processInput(inputLine);
                out.println("Dummy");
                if (inputLine.equals("BYE"))
                {
                	out.println("BYE");
                    break;
                }
            }
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }  
    
    }
     
}
