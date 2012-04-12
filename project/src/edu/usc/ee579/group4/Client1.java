package edu.usc.ee579.group4;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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

import edu.usc.ee579.Protocol.Packet;

//import edu.usc.anrg.ee579.diagnostic.protocol.Packet;


/**
 * @author Himanshu
 *
 */


public  class Client1  {
	  private DataOutputStream outToServer             = null;
	  private InputStream inFromServer            = null;
	  private InputStream inFromServer2            = null;
	  private InputStream ReceiveFriendList=null;
	  private Socket clientSocket          = null;
	 // private final String defaultServerIP = "192.168.0.10";
	  private final String defaultServerIP = "207.151.254.26";
	  static List<String> records = new ArrayList<String>();
	  private final int defaultServerPort  = 9777;
      String Confirm=null;
      String Confirm2=null;
      String[] buffer=null;
      PrintStream ps1;
      int count=0;
	  //public Client() throws IOException {
	    //initializeClient(defaultServerIP, defaultServerPort);
	  //}
	  
	  
	  public Client1(String serverIP,int portno) throws IOException {    
	    initializeClient(serverIP, portno);
	  }
	  private void initializeClient(String ServerIP, int serverPort) throws IOException{
		
		  try {
		   clientSocket = new Socket(defaultServerIP,serverPort);
		   
		  } catch (UnknownHostException e){
			 
			//  System.err.println("Don't know about host : " + serverIP );
			  System.exit(1);
		  }
	  
	  
	  }
	  
	  public void sendFile(String Buffer) throws IOException{
		  outToServer = new DataOutputStream(clientSocket.getOutputStream());
		  //int len1 = Buffer.length();
	      //byte[] bytes = Buffer.getBytes();
		  outToServer.writeUTF(Buffer);
	      
	  }
	  
	  public void sendLoginDetails(String msg) throws IOException{
		  
		   outToServer  = new DataOutputStream(clientSocket.getOutputStream());
		   Packet packet = new Packet(msg);
		   int len1 = packet.bufferToBytes.length;
	       outToServer.write(packet.bufferToBytes,0,len1);
           recvAuthentication();
	  
	  }
	  
	  public void sendMessage(String msg) throws IOException{
		  
			
		  outToServer  = new DataOutputStream(clientSocket.getOutputStream());
		   Packet packet = new Packet(msg);
		  int len1 = packet.bufferToBytes.length;
	      outToServer.write(packet.bufferToBytes,0,len1);
	     
	     
	   //  if(!msg.equalsIgnoreCase("HELLO") && !msg.equalsIgnoreCase("END"))
	  if(msg.contains("GET LIST OF FRIENDS")||msg.contains("REQUESTED") || msg.contains("END")) 
	      recvFriendList();
	//  else 
	  //   recvMessage();
	
	  }//end of sendMessage
	     
	  
	  public void recvFriendList() throws IOException{
		  
          inFromServer2 =  clientSocket.getInputStream();
		  Confirm2="True";
          if(records.size()!=0)
		  records.clear();
          
          byte[] resultBuff = new byte[0];
		    byte[] buff = new byte[10240];
		    int k = 0;
		    //Reading bytes from the socket stream
			//File f = new File("sdcard/Assignment1");	f.mkdirs();
			
		//	File outputFile1 = new File(f,"Friends.txt");
	
		  //  FileOutputStream fos1 = new FileOutputStream(outputFile1);
		   //  ps1 = new PrintStream(fos1);
		    String msgFromServer=null;
		    while((k = inFromServer2.read(buff, 0, buff.length)) > -1 ) {
		    
		    ByteBuffer cc = ByteBuffer.allocate(k);
		    cc=ByteBuffer.wrap(buff); 
		  	
		       int f1 = cc.getInt(0);
	           if (f1!=12) {System.out.println("Sever :INVALID PACKET.API_TYPE INCORRECT");
	           }else {
	           int f2 = cc.getInt(4);//System.out.println("total length :" +f2);
	           int f3 =cc.getInt(8);//System.out.println("msg length :"+f3);
	           
	            msgFromServer = Character.toString((char)cc.get(12));
	           for(int i = 13;i<f2;i++){
			   	byte b =cc.get(i);
			   	msgFromServer = msgFromServer + (char) b;
	           // buffer[i-13]=msgFromServer;
	            
               	           
	           }//end of for
	          
	          
	           }//end of else
	       
	           
	           if(msgFromServer.contains("END")){
	        	   //printlist();
	        	   closeConnection();
	           }
	           //if(msgFromServer.contains("NOT FOUND")){
	        	   //records.
	           //}
	           else{ records.add(msgFromServer);
	           msgFromServer=null;}
		    
		    }//end of while   
	      
			
	  }
	  
	  public void printlist(){
		  
		//Confirm2="confirmed";
		//  records.remove(records.size()-1);
		
		 closeConnection();
      
	 }
	  public void recvAuthentication() throws IOException {
	 	    
			 
	    	 inFromServer =  clientSocket.getInputStream();
			  
			  Packet packet = new Packet();      
		      packet.readPacket(inFromServer);    
		      
		     
		      if (packet.msgFromServer.contains("TRUE")) {
		    	  Confirm = "TRUE";
		     	      }
		      else Confirm ="FALSE";
		      
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
