package edu.usc.anrg.ee579.diagnostic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.io.*;

import edu.usc.anrg.ee579.diagnostic.protocol.Packet;


/**
 * @author Himanshu
 *
 */


public class Client {
	  private DataOutputStream outToServer             = null;
	  private InputStream inFromServer            = null;
	  private Socket clientSocket          = null;
	  private final String defaultServerIP = "127.0.0.1";
	  private final int defaultServerPort  = 9777;

	  public Client() throws IOException {
	    initializeClient(defaultServerIP, defaultServerPort);
	  }
	  
	  public Client(String serverIP, int serverPort) throws IOException {    
	    initializeClient(serverIP, serverPort);
	  }
	  private void initializeClient(String serverIP, int serverPort) throws IOException{
		
		  try {
		   clientSocket = new Socket(serverIP,serverPort);
		
		  } catch (UnknownHostException e){
			  System.err.println("Don't know about host : " + serverIP );
			  System.exit(1);
		  }
	  
	  
	  }
	  

	  public void sendMessage(String msg) throws IOException{
		  
	
		  outToServer  = new DataOutputStream(clientSocket.getOutputStream());
		  
		 
		  
		  
		  Packet packet = new Packet(msg);
		  
		  int len1 = packet.bufferToBytes.length;
	      outToServer.write(packet.bufferToBytes,0,len1);
	     
	     
	     if(!msg.equalsIgnoreCase("HELLO") && !msg.equalsIgnoreCase("END"))
	    
	     recvMessage();
	     
	     
		
	  }
	  //method for the client to read packet bytes from the socket stream

	  public void recvMessage() throws IOException {
	    
		  inFromServer =  clientSocket.getInputStream();
		  
		  Packet packet = new Packet();      
	      packet.readPacket(inFromServer);    
	      
	     
	      if (packet.msgFromServer.contains("LIST")) {
		    	 int count =0;
		    	 for(int i=1;i<packet.listOfFileNames.length;i=i+2)
		    	 {
		    		 if (i%2!=0){count++; System.out.println(count+"."+packet.listOfFileNames[i+1]);}
		    		
		    		 
		    	 }
		     }
	   
	      else   System.out.println("Server :" +packet.msgFromServer);
	   if (packet.msgFromServer == "END"){ closeConnection();}   
	  
	  
	  }
	  
	  
//method to close the socket when "END" is received from the client 
	  public boolean closeConnection() {
	    try {
	      clientSocket.close();
	      return true;
	    }catch(IOException e) {
	      e.printStackTrace();
	      return false;
	    }
	  }
	}
