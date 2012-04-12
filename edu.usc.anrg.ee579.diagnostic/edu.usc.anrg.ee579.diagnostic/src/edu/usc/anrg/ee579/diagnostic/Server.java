package edu.usc.anrg.ee579.diagnostic;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.Buffer;
import java.nio.CharBuffer;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;
import java.nio.charset.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.io.OutputStream;

//import com.omkar.testForEE579.MultiThreadedSocProg.Server;

import edu.usc.anrg.ee579.diagnostic.protocol.Packet;

public class Server implements Runnable {	
	
	private	OutputStream outToClient; //  = null;
	private InputStream inFromClient; // = null;
	private ServerSocket serverSocket; // = null;
	private Socket clientSocket; // = null;
	private int serverPort = 9777;
	List<String> records = new ArrayList<String>();
	List<String> records2 = new ArrayList<String>();
	InetAddress addr=null;
	
	//new variables -Omkar
	private int portID;
	int count = 0;
	String TimeStamp = new Date().toString();
	
/*	//Default constructor commented out here!!
	public Server() throws IOException {
		initializeServer(serverPort);
	}
	

	public Server(int serverPort) throws IOException {
		this.serverPort = serverPort;
		initializeServer(serverPort);
	}
*/
	  //Omkar's Server constructor!!
	  public Server(Socket s, int i) {
		  this.clientSocket = s;
		  this.portID = i;	  	  
	  }
	  
/*	//setup the  Server to to open the serverPort
	private void initializeServer(int serverPort) throws IOException{
		try {
		 serverSocket = new ServerSocket(serverPort);
		} catch (IOException e) {
		//	System.err.println("Could not listen on port : " + serverPort);
			System.exit(1);
		}
		
	}// end of initializeServer()
*/	//NOT required!! -OC
	  
		public void startServer() throws IOException {
			try {
				serverSocket = new ServerSocket(serverPort);
			    System.out.println("------------------------------------------");
			    System.out.println("Connection Established at: " +TimeStamp);
			    System.out.println();    
				//addr = clientSocket.getInetAddress();
				//System.out.println(addr);
				while(true) {
				    clientSocket = serverSocket.accept();
				    System.out.println("Server has accepted the socket connection");   
				    Runnable runnable = new Server(clientSocket, ++count);
				    System.out.println(clientSocket.toString() + " " +"Count: " +count);
				    Thread t = new Thread(runnable);
				    t.start();
					} 
				}
			    catch (IOException e) {
			    System.err.println("ERROR: Accept Failed!!");
			    System.exit(1);
			    }
			  }	//end of startServer() function			
			 
		//@Override
		public void run() {
			// TODO Auto-generated method stub	
		try{		
	        inFromClient = clientSocket.getInputStream();				                                            
	        byte[] resultBuff = new byte[0];
		    byte[] buff = new byte[1024];
		    int k = 0;
		    //Reading bytes from the socket stream
		    //MAIN WHILE
		    while((k = inFromClient.read(buff, 0, buff.length)) > -1 ) {
		    
		    ByteBuffer cc = ByteBuffer.allocate(k);
		    cc=ByteBuffer.wrap(buff); 
	  	
	       int f1 = cc.getInt(0);
           if (f1!=12) { System.out.println("Sever :INVALID PACKET.API_TYPE INCORRECT"); }
           else {
           int f2 = cc.getInt(4);//System.out.println("total length :" +f2);
           int f3 =cc.getInt(8);//System.out.println("msg length :"+f3);
           
           String msgFromClient = Character.toString((char)cc.get(12));
           for(int i = 13;i<f2;i++) {
		   	byte b =cc.get(i);
		   	msgFromClient = msgFromClient + (char) b;
           }
           
			System.out.println("Client :" +msgFromClient);
			
			if(msgFromClient.contains("REGISTER")) {
				
				String [] ClientDetails=msgFromClient.split(",");
				System.out.println("Client NAME :"+ClientDetails[1]);
				System.out.println("Client EMAIL ADDRESS:"+ClientDetails[2]);
				System.out.println("Client PASSWORD:"+ClientDetails[3]);
				
				FileOutputStream fos = new FileOutputStream("C:/EE579/workspace/project/Registration.txt",true);
				
				PrintStream ps = new PrintStream(fos);
				
				fos.write(ClientDetails[1].getBytes());
				fos.write(",".getBytes());
			    fos.write(ClientDetails[2].getBytes());
			    fos.write(",".getBytes());
			    fos.write(ClientDetails[3].getBytes());
			    ps.println();	//write a new line character
			    fos.close();
			    replyToClient("END");			  												
			}
			
			
			//=================================================================================================================
			else if(msgFromClient.contains("LOGIN")){
				String[] ClientLogin=msgFromClient.split(",");
				BufferedReader reader = new BufferedReader(new FileReader(
						"C:/EE579/workspace/project/Registration.txt"));//reader-an object of public class bufferedreader,stores the input data read from the file
				String login;
				
				boolean confirm=false;
				while ((login = reader.readLine()) != null)

				{		
					String[] LoginDetails=login.split(",");
					if(LoginDetails[1].equalsIgnoreCase(ClientLogin[1]) && LoginDetails[2].equalsIgnoreCase(ClientLogin[2]))
					{
						//System.out.println(login);
					  System.out.println("Client NAME:"+LoginDetails[0]);  
					  System.out.println("Client EMAIL ADDRESS:"+LoginDetails[1]);
						
                      replyToClient("TRUE");
                        //Adding login details of clients who have requested login
                      FileOutputStream fos1 = new FileOutputStream("C:/EE579/workspace/project/LoginDetails.txt",true);
                      PrintStream ps1 = new PrintStream(fos1);
                      ps1.println("*");
                      ps1.println(LoginDetails[0]+","+LoginDetails[1]+","+addr);                      				    
  					  ps1.close();
					  confirm = true;
					  closeConnection();
					}	
				}
				reader.close();				
				if (confirm == false){ replyToClient("FALSE"); }							
			}	//LOGIN end

			boolean value=true;	
	//=====================================================================================================================================

			//Making the server respond to queries other than "HELLO and "END"
			 if(msgFromClient.contains("GET LIST OF FRIENDS")){
				 System.out.println("Recieved Request for list of friends");   
			
	         
			BufferedReader reader = new BufferedReader(new FileReader(
					"C:/EE579/workspace/project/LoginDetails.txt"));	//reader-an object of public class bufferedreader,stores the input data read from the file
			String line;
			String[] temp= null;
			while ((line = reader.readLine()) != null) {	
				//System.out.println(line);
				if(!line.startsWith("*"))
				{								
				records.add(line);
				temp=line.split(",");
				System.out.println("Sent:: Name::"+temp[0]+" Email:: "+temp[1]+"  IP Address : "+temp[2]);
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				replyToClient(temp[0]+","+temp[1]+", "+temp[2]);
                temp=null;
			                }
			}
			reader.close();
			
				
			
			
		 // replyToClient("END");
          closeConnection();
			
		    }	//END OF GET LIST OF FRIENDS
//===============================================================================================================================           
			 else if(msgFromClient.contains("REQUESTED")){
					
				 
					BufferedReader reader = new BufferedReader(new FileReader(
							"C:/EE579/workspace/project/LoginDetails.txt"));	//reader-an object of public class bufferedreader,stores the input data read from the file
					String line;
					String[] temp= null;
					String[] temp1= msgFromClient.split(",");
					System.out.println(temp1[1]+" "+temp1[2]+" "+temp1[3]);
					//System.out.println(temp1[1].length());
					int match=0;
					while ((line = reader.readLine()) != null) {	
						//System.out.println(line);
						if(!line.startsWith("*"))
						{								
						
						temp=line.split(",");
						//System.out.println(line);
						if(temp[0].contains(temp1[1])||temp[0].contains(temp1[2])||temp[0].contains(temp1[3])){
					
							match=1;
							System.out.println("Sent:: Name::"+temp[0]+" Email:: "+temp[1]+"  IP Address : "+temp[2]);
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					
						replyToClient(temp[0]+","+temp[1]+", "+temp[2]);
						
						records.add(line);
						}
					
						temp=null;
					                }//end of if(!line.contains("*"))
					//}//end of if
					}//end of while
					if(match==0){
						System.out.println("zero");replyToClient("MATCH NOT FOUND"+","+"OR USER IS OFFLINE"+","+null);
						}
					
					reader.close();
					closeConnection();	
						
					
				}//end of REQUESTED
			
  //=================================================================================================================
			 
			  if (msgFromClient.contains("GANG")){
				 
				 String[] temp=msgFromClient.split(",");
				 records2.add(temp[1]+","+temp[2]);
				//  System.out.println("hello");
				 System.out.println(msgFromClient);
//=================================================================================================================
			 }//END OF msgFromClient.contains("GANG")
			  
			  if (msgFromClient.contains("END")){
			 
			 
			 
				 BufferedReader reader = new BufferedReader(new FileReader(
							"C:/EE579/workspace/project/LoginDetails.txt"));	//reader-an object of public class bufferedreader,stores the input data read from the file
					String line;
					String[] temp= null;
					
					int match=0;
					while ((line = reader.readLine()) != null) {	
						//System.out.println(line);
						if(!line.startsWith("*"))
						{								
						
						temp=line.split(",");
						//System.out.println(line);
						for(int i=0;i<records2.size();i++)
						{
							String temp2 = records2.get(i);
							String[] temp1=temp2.split(",");
							
						if(temp[0].equals(temp1[0]) && temp[1].equals(temp1[1]))
						  {
					
							match=1;
							System.out.println("Sent:: Name::"+temp[0]+" Email:: "+temp[1]+"  IP Address : "+temp[2]);
						  try {
							Thread.sleep(150);}
						    catch (InterruptedException e) {
							// TODO Auto-generated catch block
							  e.printStackTrace();}
						
           					replyToClient(temp[0]+","+temp[1]+", "+temp[2]);
						
					//	records.add(line);
						}//end of if
						}//end of for
					
						temp=null;
					  }//end of if(!line.contains("*"))

					}//end of while
					if(match==0){
						System.out.println("zero");replyToClient("MATCH NOT FOUND"+","+"OR USER IS OFFLINE"+","+null);
						}
					
					reader.close();
					closeConnection();	
						
					
				}//end of END
 //=======================================================================================================================
			  if(msgFromClient.contains("LOG OFF")){
				  
				  BufferedReader reader = new BufferedReader(new FileReader(
							"C:/EE579/workspace/project/LoginDetails.txt"));	//reader-an object of public class bufferedreader,stores the input data read from the file
					String line;
				  
				  records.clear();
					
				 
                 String[] temp = msgFromClient.split(",");
                
                
                 while ((line = reader.readLine()) != null) {	
						
						if(!line.startsWith("*"))
						{							
						if(!line.contains(temp[1]))
							{records.add(line);
						System.out.println(line);}
						}
						}
                 
                 FileOutputStream fos1 = new FileOutputStream("C:/EE579/workspace/project/LoginDetails.txt"); 
                 PrintStream ps1 = new PrintStream(fos1);
                 
                 
                 for(int i=0;i<records.size();i++)
                 {
                	 
				      ps1.println("*");
                	  ps1.println(records.get(i));
                	  //System.out.println("Match found");
				      
                	  
                 }
			  ps1.close();
			  closeConnection();
			  
			  }
			  
           
           
           
           
           
           
           }	//END OF MAIN ELSE
	           }	//END OF MAIN WHILE
		    
           
		} catch(Exception e){
			e.getMessage();
		}
		
		} // end of run() method
			
		//Function describes what the server should respond to client's queries
		public void replyToClient(String msgFromServer) throws IOException{
			
			   
				  
				  outToClient = new DataOutputStream(clientSocket.getOutputStream());
				  Packet packet = new Packet(msgFromServer);
				  int lengthOfPacket = packet.bufferToBytes.length;
				  
				  //writing bytes to the output socket stream
				  outToClient.write(packet.bufferToBytes,0,lengthOfPacket);//}
				  //if (msgFromClient.equalsIgnoreCase("END")) closeConnection();
			
		
		}//end of replyToClient()
		
		
		
		public void closeConnection() throws IOException {
			outToClient.close();
	        inFromClient.close();
	        clientSocket.close();
	        serverSocket.close();		
		}
	}	//end of class Server

	
