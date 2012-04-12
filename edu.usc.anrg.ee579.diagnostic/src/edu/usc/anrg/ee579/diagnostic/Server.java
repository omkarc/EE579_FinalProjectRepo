package edu.usc.anrg.ee579.diagnostic;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.Buffer;
import java.nio.CharBuffer;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;
import java.nio.charset.*;
import java.io.OutputStream;
import edu.usc.anrg.ee579.diagnostic.protocol.Packet;

public class Server {
	
	
	private	OutputStream outToClient  = null;
	private InputStream inFromClient = null;
	private ServerSocket   serverSocket = null;
	private Socket         clientSocket = null;
	private int             serverPort  = 9777;
	
	//constructor
	public Server() throws IOException {
		initializeServer(serverPort);
	}
	

	public Server(int serverPort) throws IOException {
		this.serverPort = serverPort;
		initializeServer(serverPort);
	}
	
	//setup the  Server to to open the serverPort
	private void initializeServer(int serverPort) throws IOException{
		try {
		 serverSocket = new ServerSocket(serverPort);
		} catch (IOException e) {
			System.err.println("Could not listen on port : " + serverPort);
			System.exit(1);
		}
		
	}// end of initializeServer()
	
		public void startServer() throws IOException {
			try {
				
				clientSocket = serverSocket.accept();
			} catch (IOException e){
				System.err.println("Accept failed");
				System.exit(1);
			}
				
			
			 
	         inFromClient         = clientSocket.getInputStream();
				                                            
	        byte[] resultBuff = new byte[0];
		    byte[] buff = new byte[1024];
		    int k = 0;
		    //Reading bytes from the socket stream
		    while((k = inFromClient.read(buff, 0, buff.length)) > -1 ) {
		    
		    ByteBuffer cc = ByteBuffer.allocate(k);
		    cc=ByteBuffer.wrap(buff); 
		  	
		       int f1 = cc.getInt(0);
	           if (f1!=12) {System.out.println("Sever :INVALID PACKET.API_TYPE INCORRECT");
	           }else {
	           int f2 = cc.getInt(4);//System.out.println("total length :" +f2);
	           int f3 =cc.getInt(8);//System.out.println("msg length :"+f3);
	           
	           String msgFromClient = Character.toString((char)cc.get(12));
	           for(int i = 13;i<f2;i++){
			   	byte b =cc.get(i);
			   	msgFromClient = msgFromClient + (char) b;
	           }
	           
			System.out.println("Client :"+msgFromClient);
			
			//Making the server respond to queries other than "HELLO and "END"
	       if(msgFromClient.equalsIgnoreCase("LIST") ||( msgFromClient.contains("GET") && msgFromClient.charAt(3)==','))
			replyToClient(msgFromClient);
	       else if(!msgFromClient.equalsIgnoreCase("HELLO") && !msgFromClient.equalsIgnoreCase("END")) 
	    	   {msgFromClient = "INVALID MESSAGE TYPE";
	    	   replyToClient(msgFromClient);}
	           }

		    
		    }
		
		
		
		} // end of startServer()
			
		//Function describes what the server should respond to client's queries
		public void replyToClient(String msgFromClient) throws IOException{
			
			
			int[] fileIndex ={54,23,65,101,4,32,97,18};
			   String[] fileName ={ "NameOfClient.txt",    "AddressOfClient.txt",
					                "SalaryOfClient",       "ClientId.txt",
					                "LocationOfClient.txt", "BirthYearOfClient.txt",
					                "AchievementsOfClient.txt","EducationOfClient.txt"};
			   
			   
			   
			   
			   String[] fileDescription = {"Client Name: HIMANSHU" ,   "Client Address : LA",
			  		                       "Salary of Client: 800$",   "ClientID : 4052544906",
			  		                       "Client Location : USC",    "BirthYear : 1987 ",
			  		                       "Client Achievements :None","Client Education :MSEE"};
			   
			   String msgToClient =null;//= "INVLAID INPUT"; 
				  //if(msgFromClient.equalsIgnoreCase("HELLO")){
					//  msgToClient = "HELLO";
				   if ( msgFromClient.equalsIgnoreCase("LIST")){
					  msgToClient = "LIST";
					  for (int i =0;i<fileIndex.length;i++)
				      msgToClient =msgToClient+"," + fileIndex[i]+ "," + fileName[i] ;
					  
					  
					  
					  
				  }else if (msgFromClient.contains("GET") )
				  {
					  String index = Character.toString(msgFromClient.charAt(4));
					  for (int i=5;i<msgFromClient.length();i++) 
					  index = index + Character.toString(msgFromClient.charAt(i));
                      int fileNumber = Integer.parseInt(index);
                      if (fileNumber == 0)msgToClient = "INVALID INDEX NUMBER ENTERED";
                      else{
	                  for (int j=0;j<fileIndex.length;j++)
	                  {
	                	  if(fileNumber==fileIndex[j])
	                		  msgToClient = "GET"+","+fileNumber+","+","+fileName[j]+","+ fileDescription[j];    
	                	  
                         
	                  } 	
                      }
				  }
				  
				
				  //else if (msgFromClient.equalsIgnoreCase("END"))
					//  msgToClient ="END";
				   
				  
				  outToClient          = new DataOutputStream(clientSocket.getOutputStream());
				 if (msgFromClient.equalsIgnoreCase("INVALID MESSAGE TYPE"))
					 msgToClient =msgFromClient;
				  Packet packet = new Packet(msgToClient);
				  int lengthOfPacket = packet.bufferToBytes.length;
				  
				  //writing bytes to the output socket stream
				  outToClient.write(packet.bufferToBytes,0,lengthOfPacket);//}
				  if (msgFromClient.equalsIgnoreCase("END")) closeConnection();
			
		
		}//end of replyToClient()
		
		
		
		public void closeConnection() throws IOException{
			outToClient.close();
	        inFromClient.close();
	        clientSocket.close();
	        serverSocket.close();		
			
			
		}
		
		
		
		
		
		
		
	}//end of class Server

	
