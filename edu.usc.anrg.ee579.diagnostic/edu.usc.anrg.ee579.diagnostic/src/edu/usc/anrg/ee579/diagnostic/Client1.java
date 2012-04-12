package edu.usc.anrg.ee579.diagnostic;

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

//import android.os.Parcel;
//import android.os.Parcelable;

import edu.usc.anrg.ee579.diagnostic.*;
import edu.usc.anrg.ee579.diagnostic.protocol.Packet;

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
	  private final String defaultServerIP = "127.0.0.1";
	  List<String> records = new ArrayList<String>();
	 // private final String defaultServerIP = "207.151.253.110";
	  private final int defaultServerPort  = 9777;
      String Confirm=null;
      String Confirm2=null;
      String[] buffer=null;
	  //public Client() throws IOException {
	    //initializeClient(defaultServerIP, defaultServerPort);
	  //}
      OutputStreamWriter osw1;
      PrintStream ps1;
	  
	  public Client1() throws IOException {    
	    initializeClient(defaultServerIP,  defaultServerPort);
	  }
	  private void initializeClient(String serverIP, int serverPort) throws IOException{
		
		  try {
		   clientSocket = new Socket(serverIP,serverPort);
		   
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
	  
	
	  public void sendMessage(String msg) throws IOException{
		  
			
		  outToServer  = new DataOutputStream(clientSocket.getOutputStream());
		  Packet packet = new Packet(msg);
		  int len1 = packet.bufferToBytes.length;
	      outToServer.write(packet.bufferToBytes,0,len1);
	    
	     
	   //  if(!msg.equalsIgnoreCase("HELLO") && !msg.equalsIgnoreCase("END"))
	  if(msg.contains("GET LIST OF FRIENDS")){ 
		  recvFriendList();
	//  printlists();
	  }
	 
	
	  }
	     
	  
	  public void recvFriendList() throws IOException{
		  
          inFromServer2 =  clientSocket.getInputStream();
		  Confirm2="True";
          
          byte[] resultBuff = new byte[0];
		    byte[] buff = new byte[1024];
		    int k = 0;
		    //Reading bytes from the socket stream
			File f = new File("c:/EE579");	f.mkdirs();
			
			File outputFile1 = new File(f,"Friends.txt");
	
		    FileOutputStream fos1 = new FileOutputStream(outputFile1,true);
		  //  OutputStreamWriter osw1;
		     ps1 = new PrintStream(fos1);
		    osw1 = new OutputStreamWriter(fos1);
		    String msgFromServer=null;
		    String[] buffer=null;
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
	       //     buffer[i-13]=msgFromServer;
	            
               	           
	           }//end of for
	           System.out.println("Sever :"+msgFromServer);
	           
	           records.add(msgFromServer);
	           if(msgFromServer.contains("END")){
	        	   printlists();
	           }
	           
	           //osw1.write(msgFromServer);
	        //   osw1.write(buff.length);
	           }//end of else
	           }//end of while   
	         //  records.
	        // 
	        	
	       //  } 
	        // osw1.close();
		    }//end of recvFriendList()
			
			
	  
	
	  //method for the client to read packet bytes from the socket stream

	 public void printlists(){
		  
		 for(int i=0;i<records.size();i++)
         {
        	 String itemName = records.get(i);
 	         System.out.println(itemName);  
 	        ps1.println(records.get(i));
 	        
        
         }
         
        
	 }
	  
	  
	  //method for the client to read packet bytes from the socket stream


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