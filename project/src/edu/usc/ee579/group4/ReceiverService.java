package edu.usc.ee579.group4;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.List;

import edu.usc.ee579.Protocol.Packet;
import edu.usc.ee579.group4.ReceiverActivity.Receiver;
import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

/*
 * This is another service which continously sends Hello Message in the interval of 1 min to the Centralized Server
 * to inform its online status 
*/
public class ReceiverService extends IntentService{
	

	private	OutputStream outToClient; //  = null;
	private InputStream inFromClient; // = null;
	private ServerSocket serverSocket; // = null;
	private Socket clientSocket; // = null;
	private int serverPort = 8080;
	InetAddress addr=null;
  	InputStream inFromBuffer;
	private int portID;
	int count = 0;
	String TimeStamp = new Date().toString();
	
	 

	//The Service Starts
	public ReceiverService() {
		super("ReceiverService");
	}

 
	  /**
	   * The IntentService calls this method from the default worker thread with
	   * the intent that started the service. When this method returns, IntentService
	   * stops the service, as appropriate.
	   */
	  @Override
	  protected void onHandleIntent(Intent intent) {
	
		  Log.v( "KeepAlive", "abcd");
		  
			
          try {
        	  
        	  
        	  while(true)
        	  {
        		  //This check whether the user is online by checking its email id field
        		  if(MainViewActivity.valueOfEmail!=null){
        			  //Hello Message are being send to the Centralized Server
        			  clientSocket = new Socket(Client1.defaultServerIP,9777);
	   			
        			  String msg = "HELLO"+","+MainViewActivity.valueOfEmail;
        			  System.out.println("Message sent is: " +msg);
        			  DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
        			  Packet packet = new Packet(msg);
        			  int len1 = packet.bufferToBytes.length;
	 		 
        			  outToServer.write(packet.bufferToBytes,0,len1);
            
        			  clientSocket.close();
        		  
	 	       	
        			  try {
        				  //This background the goes to sleep for 1 min
        				  Thread.sleep(60000);
        			  } catch (InterruptedException e) {
			
        				  e.printStackTrace();
        			  }
		          
        		  }
			 	       	
    	  		}
	 	       
   			} catch (IOException e) {
   				// TODO Auto-generated catch block
   				e.printStackTrace();
   			}
          	 
	  
	  }		
}
	


