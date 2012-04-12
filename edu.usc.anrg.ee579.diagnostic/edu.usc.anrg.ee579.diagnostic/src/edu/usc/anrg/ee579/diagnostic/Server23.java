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
import java.util.List;
import java.io.OutputStream;
import edu.usc.anrg.ee579.diagnostic.protocol.Packet;

public class Server23 {
	
	
	private	OutputStream outToClient  = null;
	private InputStream inFromClient = null;
	private ServerSocket   serverSocket = null;
	private Socket         clientSocket = null;
	private int             serverPort  = 9777;
	List<String> records = new ArrayList<String>();
	InetAddress addr;
	//constructor
	public Server23() throws IOException {
		initializeServer(serverPort);
	}
	

	public Server23(int serverPort) throws IOException {
		this.serverPort = serverPort;
		initializeServer(serverPort);
	}
	
	//setup the  Server to to open the serverPort
	private void initializeServer(int serverPort) throws IOException{
		try {
		 serverSocket = new ServerSocket(serverPort);
		} catch (IOException e) {
		//	System.err.println("Could not listen on port : " + serverPort);
			System.exit(1);
		}
		
	}// end of initializeServer()
	
		public void startServer() throws IOException {
			try {
				
				clientSocket = serverSocket.accept();
				 addr =clientSocket.getInetAddress();
				System.out.println(addr);
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
			
			if(msgFromClient.contains("REGISTER")){
				
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
			    ps.println();//write a new line character
			    fos.close();
			    replyToClient("END");
			  
						
							
			}
			
			else if(msgFromClient.contains("LOGIN")){
				String [] ClientLogin=msgFromClient.split(",");
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
					    confirm= true;
					closeConnection();
					}
	
				}
				reader.close();
				
				if (confirm==false){replyToClient("FALSE");
				}
			
			}

			boolean value=true;
			
			
			//Making the server respond to queries other than "HELLO and "END"
			 if
			 (msgFromClient.contains("GET LIST OF FRIENDS")){
				System.out.println("Recived Request for list of friends");   
			
	         
			BufferedReader reader = new BufferedReader(new FileReader(
					"C:/EE579/workspace/project/LoginDetails.txt"));//reader-an object of public class bufferedreader,stores the input data read from the file
			String line;
			String[] temp= null;
			while ((line = reader.readLine()) != null)

			{	
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
			
		    }//END OF GET LIST OF FRIENDS
		
	           }//END OF MAIN ELSE
	           }//END OF MAIN WHILE		
		
		} // end of startServer()
			
		//Function describes what the server should respond to client's queries
		public void replyToClient(String msgFromServer) throws IOException{
			
			   
				  
				  outToClient          = new DataOutputStream(clientSocket.getOutputStream());
				  Packet packet = new Packet(msgFromServer);
				  int lengthOfPacket = packet.bufferToBytes.length;
				  
				  //writing bytes to the output socket stream
				  outToClient.write(packet.bufferToBytes,0,lengthOfPacket);//}
				  //if (msgFromClient.equalsIgnoreCase("END")) closeConnection();
			
		
		}//end of replyToClient()
		
		
		
		public void closeConnection() throws IOException{
			outToClient.close();
	        inFromClient.close();
	        //clientSocket.close();
	        //serverSocket.close();		
			
			
		}
		
		
		
		
		
		
		
	}//end of class Server

	
