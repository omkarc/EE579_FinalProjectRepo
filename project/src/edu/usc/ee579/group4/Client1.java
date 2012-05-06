package edu.usc.ee579.group4;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.io.*;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.Toast;

import edu.usc.ee579.Protocol.Packet;

//import edu.usc.anrg.ee579.diagnostic.protocol.Packet;


/**
 * This is the client class which is use to form various message to be send to the server
 *
 */


public  class Client1  {
	  private DataOutputStream outToServer = null;
	  private InputStream inFromServer = null;
	  private InputStream inFromServer2 = null;
	  private InputStream ReceiveFriendList=null;
	  public Socket clientSocket = null;
	  //final static String defaultServerIP = "192.168.0.21";
	  final static String defaultServerIP = "207.151.253.115";
	  static List<String> records = new ArrayList<String>();
	  static List<String> records2 = new ArrayList<String>();
	  static int flag=0;
	  private final int defaultServerPort  = 9777;
      String Confirm=null;
      static String Confirm2=null,Completed=null,fileSent="FALSE";
      String[] buffer=null;
      PrintStream ps1;
      int FLAG=0;
      int count=0;
      //Construnctor for the client connection
	  public Client1() throws IOException {    
	    initializeClient(defaultServerIP, defaultServerPort);
	  }
	  //This function creates a socket connection with the server for writing and reading of data
	  private void initializeClient(String ServerIP, int serverPort) throws IOException{
		
		  try {
		   clientSocket = new Socket(defaultServerIP,serverPort);
		   outToServer  = new DataOutputStream(clientSocket.getOutputStream());
		   
		  } catch (UnknownHostException e){
			 
			//  System.err.println("Don't know about host : " + serverIP );
			  System.exit(1);
		  }
	  
	  
	  }
	  //This function sends file to the server
	  public void sendFile(String Buffer) throws IOException{
		  //outToServer = new DataOutputStream(clientSocket.getOutputStream());
	      outToServer.writeUTF(Buffer);
	      
	  }
	  //This function sends Login details for the user to the server
	  public void sendLoginDetails(String msg) throws IOException{
		  
		   //outToServer  = new DataOutputStream(clientSocket.getOutputStream());
		   
		   //String type[] = msg.split(",");
		   
		   Packet packet = new Packet(msg);
		   int len1 = packet.bufferToBytes.length;
	       outToServer.write(packet.bufferToBytes,0,len1);
	       
	       /*if(type[0].contains("STORE"))
	       {
	    	   storeFile();
	       }*/
           recvAuthentication();
	  
	  }
	  //This function is used to request for a file from the server
	  public void request(String fileName) throws IOException
	  {
		  String sendtoServer = fileName;
		  byte[] theStringByte = sendtoServer.getBytes();
		  
		  System.out.println("the String name of the file is is: " + sendtoServer);
		  ByteBuffer b2 = ByteBuffer.allocate(4);
		  //b.order(ByteOrder.BIG_ENDIAN); // optional, the initial order of a byte buffer is always BIG_ENDIAN.
		  b2.putInt(sendtoServer.length());
		  byte[] result2 = new byte[4];
		  result2 = ByteCopy(b2);
		  System.out.println("the filename size is: " + sendtoServer.length());

		  
		  //outToServer = new DataOutputStream(clientSocket.getOutputStream());
		  //Length of the music file is send to the server
		  //outToServer.write(result1,0,4);
		  outToServer.write(result2,0,4);
		  outToServer.write(theStringByte,0,theStringByte.length);
		  
		  
		  InputStream inFromBuffer = null;
		  byte[] buffer = new byte[4];
		  inFromBuffer = clientSocket.getInputStream();
			//Server first receives the length of the Music file to be received
		  inFromBuffer.read(buffer, 0, 4);
		//  Log.v( "PhoneServer", "fff");
		  ByteBuffer api = ByteBuffer.wrap(buffer);
		  int musicFileSize = api.getInt();
		  System.out.println(musicFileSize);
		//	Toast.makeText(getApplicationContext(), musicFileSize, Toast.LENGTH_SHORT).show();
		  if(musicFileSize > 0)
		  {

		//	Toast.makeText(getApplicationContext(), "started", Toast.LENGTH_SHORT).show();
			  byte[] totalFile = new byte[musicFileSize];
			  byte[] limit = new byte[1024];
			  int totalread = 0;
			//The file in which the incoming data is to be written
			  String pathString = "/sdcard/My Downloads/" + sendtoServer;
			  File f2 = new File(pathString);
			  while(totalread != musicFileSize)
			  {
				  //the music file is to be read 1024 bytes at a time
				  int read = inFromBuffer.read(limit,0,1024);
			      //	System.out.println("read " + read);
				  System.out.println(read);
				  for(int i = 0; i< read;i++)
				  {
					  //System.out.println(totalread);						\				  
					  totalFile[totalread] = limit[i];
					  
					  totalread++;
				  }
				 
			  }
			  System.out.println("read "+totalFile );
			  //finally the byte array is written into the file
			  OutputStream out;
			
				out = new FileOutputStream(f2);
			
			
				out.write(totalFile, 0, musicFileSize);
			
			
				out.close();
				Completed="TRUE";
		  }


	  }
	  //This functionality is used to store file in the server to be shared with other users when offline

	  public void storeFile(String filename2,String actualFileName) throws IOException
	  {
			
			  //Toast.makeText(getApplicationContext(), "socket created", Toast.LENGTH_SHORT).show();
		   	  File f1 = new File(filename2);
     		  //DataOutputStream outToServer = null;
     		  
			  InputStream in = new FileInputStream(f1);
			  
			  int size = (int) f1.length();
			  System.out.println(size);
			  byte[] buf = new byte[1024];
			  
			  int len;
			  int count = 0;
			  ByteBuffer b = ByteBuffer.allocate(4);
			  //b.order(ByteOrder.BIG_ENDIAN); // optional, the initial order of a byte buffer is always BIG_ENDIAN.
			  b.putInt(size);
			  byte[] result = new byte[4];
			  result = ByteCopy(b);
			  

			  ByteBuffer b1 = ByteBuffer.allocate(4);
			  //b.order(ByteOrder.BIG_ENDIAN); // optional, the initial order of a byte buffer is always BIG_ENDIAN.
			  b1.putInt(1);
			  byte[] result1 = new byte[4];
			  result1 = ByteCopy(b1);

			  String sendtoServer = actualFileName;
			  byte[] theStringByte = sendtoServer.getBytes();
			  
			  System.out.println("the String name of the file is is: " + sendtoServer);
			  ByteBuffer b2 = ByteBuffer.allocate(4);
			  //b.order(ByteOrder.BIG_ENDIAN); // optional, the initial order of a byte buffer is always BIG_ENDIAN.
			  b2.putInt(sendtoServer.length());
			  byte[] result2 = new byte[4];
			  result2 = ByteCopy(b2);
			  System.out.println("the filename size is: " + sendtoServer.length());

			  
			  //outToServer = new DataOutputStream(clientSocket.getOutputStream());
			  //Length of the music file is send to the server
			  //outToServer.write(result1,0,4);
			  outToServer.write(result2,0,4);
			  outToServer.write(theStringByte,0,theStringByte.length);
			  outToServer.write(result,0,4);										  //The the actual music file in read 1024 bytes at a time and send to the server
			  while ((len = in.read(buf)) > 0){
				  count++;
				  outToServer.write(buf,0,len);
				 // Log.d("ClientActivity", "sent");
				 			  				  
			  }
			  //Toast.makeText(getApplicationContext(), "finished", Toast.LENGTH_SHORT).show();
			  fileSent="TRUE";
			  in.close();
			  //socket1.close();
			 

	  }
	  //This functionality is used to send various kind of message to the server and wait for the reply
	  public void sendMessage(String msg) throws IOException{
		  
			
		  outToServer  = new DataOutputStream(clientSocket.getOutputStream());
		  Packet packet = new Packet(msg);
		  int len1 = packet.bufferToBytes.length;
	      outToServer.write(packet.bufferToBytes,0,len1);
	     
	     
	   //  if(!msg.equalsIgnoreCase("HELLO") && !msg.equalsIgnoreCase("END"))
		  if(msg.contains("GET LIST OF FRIENDS")||msg.contains("REQUESTED") 
				  || msg.contains("END")||msg.contains("QUERY") ) 
		      recvFriendList(1);
		  else if (msg.contains("FILES")|| msg.contains("SEARCH"))
			  recvFriendList(2);
		  else
		  {
		   
		  }
	
	  }//end of sendMessage
	  //	This functionality copies data of byte buffer into byte array
	  public static byte[] ByteCopy(ByteBuffer b) {
	    	byte[] output = new byte[b.limit()];
	    	for (int i = 0; i < b.limit(); i++) {
	    		output[i] = b.get(i);
	    	}
	    	return output;
	  }	
	  //This function is used to store the response for the friends in to list
	  public void recvFriendList(int flag) throws IOException{
		  
          	inFromServer2 =  clientSocket.getInputStream();
          	
          
		  	if(flag==1 && !records.isEmpty())
		  		records.clear();

		  	if(flag==2 && !records2.isEmpty())
		  		records2.clear();
	          
      		byte[] resultBuff = new byte[0];
		    byte[] buff = new byte[10240];
		    int k = 0;
		    String msgFromServer=null;
		    while((k = inFromServer2.read(buff, 0, buff.length)) > -1 ) {
		    
		    	ByteBuffer cc = ByteBuffer.allocate(k);
	    		cc=ByteBuffer.wrap(buff); 
		  	
	    		int f1 = cc.getInt(0);
	    		if (f1!=12) {
	        	   System.out.println("Sever :INVALID PACKET.API_TYPE INCORRECT");
	    		}
	    		else {
	        	   int f2 = cc.getInt(4);//System.out.println("total length :" +f2);
	        	   int f3 =cc.getInt(8);//System.out.println("msg length :"+f3);
	           
	        	   msgFromServer = Character.toString((char)cc.get(12));
	        	   for(int i = 13;i<f2;i++){
			   			byte b =cc.get(i);
			   			msgFromServer = msgFromServer + (char) b;
	           	           
	        	   }//end of for
	          
	          
	    		}//end of else
	       
	           
	    		if(msgFromServer.contains("END")|| msgFromServer.contains("QUIT")){
	    			//printlist();
	    			// records2=records;
	    			Confirm2="TRUE";
	    			closeConnection();
	    		}
	    		else{ 
	        	   
	    			if(flag==1)
	    				records.add(msgFromServer);
	    			else if(flag==2)
	    				records2.add(msgFromServer);
	    			//Toast.makeText(getContext, msgFromServer, Toast.LENGTH_SHORT).show();
	           
	    			msgFromServer=null;
	           
	    		}
		    
		    }//end of while   
	      
			
	  }
	  
   
	  public void printlist(){
		  
		
		 closeConnection();
      
	  }
	  //This is used to check if the data is recieved properly or not
	  public void recvAuthentication() throws IOException {
	 	    
			 
		  inFromServer =  clientSocket.getInputStream();
		  
		  Packet packet = new Packet();      
	      packet.readPacket(inFromServer);    
	      
	     
	      if (packet.msgFromServer.contains("TRUE"))
	    	  Confirm = "TRUE";
	      else 
	    	  Confirm ="FALSE";
		      
	  }
	  //method for the client to read packet bytes from the socket stream

	  public void recvMessage() throws IOException {
	    
		  inFromServer =  clientSocket.getInputStream();
		  
		  Packet packet = new Packet();      
	      packet.readPacket(inFromServer);    
	
	      if (packet.msgFromServer == "END")
	    	  closeConnection();   
	  
	  
	  }
	  //This is used to close connection after getting the data from the server
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
