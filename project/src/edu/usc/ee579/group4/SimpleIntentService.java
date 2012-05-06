package edu.usc.ee579.group4;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Date;
import android.app.IntentService;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Toast;

/*
 * This the Server on every Andriod phone that runs in Background
*/
public class SimpleIntentService extends IntentService{

    public static final String PARAM_IN_MSG = "imsg";
    public static final String PARAM_OUT_MSG = "omsg";
 

	private ServerSocket serverSocket; // = null;
	private Socket clientSocket; // = null;
	private int serverPort = 8080;
	InetAddress addr=null;
  	InputStream inFromBuffer;

	int count = 0;
	String TimeStamp = new Date().toString();
	
    //To start the Server service on the phone
    public SimpleIntentService() {
        super("SimpleIntentService");
    }
 
    //Copies data from ByteBuffer to Byte array
    public static byte[] ByteCopy(ByteBuffer b) {
    	byte[] output = new byte[b.limit()];
    	for(int i = 0; i < b.limit(); i++) {
    		output[i] = b.get(i);
    	}
    	return output;
    }
 
    @Override
    protected void onHandleIntent(Intent intent) 
    {
 
    	//   Toast.makeText(getApplicationContext(), "hello", Toast.LENGTH_SHORT).show();
    	//Log.v("abcd", "abcd");
  
          
		try
		{
				Log.v( "PhoneServer", "Waiting on an accept");
				serverSocket = new ServerSocket(serverPort);
	

				
				//Phone Server is passively open in the background of the application
				clientSocket = serverSocket.accept();
				Log.v("PhoneServer","Server has accepted the socket connection");   
				addr = clientSocket.getInetAddress();
				inFromBuffer = clientSocket.getInputStream();
				System.out.println(addr);
				
				
				System.out.println("after dialog");
				//Identify the type of message
				byte[] type = new byte[4];
				inFromBuffer.read(type, 0, 4);
				ByteBuffer apitype = ByteBuffer.wrap(type);
				int messageType = apitype.getInt();
				System.out.println(messageType);
				
				//Get the String name Length of the file
				byte[] fileName = new byte[4];
				inFromBuffer.read(fileName, 0, 4);
				ByteBuffer apifile = ByteBuffer.wrap(fileName);
				int fileNameSize = apifile.getInt();
				System.out.println(fileNameSize);

				//Get the actual filename
				byte[] fileActualname = new byte[fileNameSize];
				inFromBuffer.read(fileActualname,0,fileNameSize);
				String fileString = new String(fileActualname);
					
				

				if(messageType == 1)
				{
					//The Message type is send so the file is been send from other user
    				byte[] buffer = new byte[4];
					
					//Server first receives the length of the Music file to be received
					inFromBuffer.read(buffer, 0, 4);
				
					ByteBuffer api = ByteBuffer.wrap(buffer);
					int musicFileSize = api.getInt();
					System.out.println(musicFileSize);
					//	Toast.makeText(getApplicationContext(), "started", Toast.LENGTH_SHORT).show();
					byte[] totalFile = new byte[musicFileSize];
					byte[] limit = new byte[1024];
					int totalread = 0;
					//The file in which the incoming data is to be written
					String pathString = "/sdcard/My Downloads/" + fileString;
					File f2 = new File(pathString);
					while(totalread != musicFileSize)
					{
						//the music file is to be read 1024 bytes at a time
						int read = inFromBuffer.read(limit,0,1024);
						//	System.out.println("read " + read);
						
						for(int i = 0; i< read;i++)
						{
							totalFile[totalread] = limit[i];
							totalread++;
						}
						System.out.println("read "+read );
					}
					
					//finally the byte array is written into the file
					OutputStream out = new FileOutputStream(f2);
					out.write(totalFile, 0, musicFileSize);
					out.close();
				}
				else
				{
					//The Message type is request so a file is to be send to the other Server
					File sdCardRoot = Environment.getExternalStorageDirectory();
					File yourDir = new File(sdCardRoot, "/favorites");
					String[] names=null;
					names= yourDir.list();
					boolean match = false;
					String file1 = null;
					int i;
					//The file with the string name is searched on the phone
					for(i=0;i<names.length;i++)
		        	{
		        		
						if(fileString.contains(names[i]))
						{
							match = true;
							file1 = "/sdcard/favorites/" + names[i];
							break;
							
						}
					}
					
	                try 
	                {
	                     //Log.d("ClientActivity", "C: Sending command.");
	                     //This is where the file actually start sending to the phone
	                	 File f1;
	                	 InputStream in = null ;
	                	 int size = 0;
	                	 String sendtoServer;
        				 byte[] theStringByte = null; 
        				 byte[] result2 = null;
        				 ByteBuffer b2;
        				 //If the requested file is found on the phone. The file is accessed 
	                	 if(match)
	                	 {
	                		 f1 = new File(file1);
	                		 in = new FileInputStream(f1);
	                		 size = (int) f1.length();
	                		 sendtoServer = names[i];
	        				 theStringByte = sendtoServer.getBytes();
		        			 b2 = ByteBuffer.allocate(4);
		        			 //b.order(ByteOrder.BIG_ENDIAN); // optional, the initial order of a byte buffer is always BIG_ENDIAN.
		        			 b2.putInt(theStringByte.length);
		        			 result2 = new byte[4];
		        			 result2 = ByteCopy(b2);

	                	 }
	                	 else
	                	 {
	                		 size = -1;
	                	 }
	               		 DataOutputStream outToServer = null;
	            		 //ByteBuffer outServer;
	                    
        			 
	        			 System.out.println(size);
	        			 byte[] buf = new byte[1024];
	        			  
	        			 
	        			 ByteBuffer b = ByteBuffer.allocate(4);
	        			 //b.order(ByteOrder.BIG_ENDIAN); // optional, the initial order of a byte buffer is always BIG_ENDIAN.
	        			 b.putInt(size);
	        			 byte[] result = new byte[4];
	        			 result = ByteCopy(b);
	        			  
	 
	        			  
	        			 
	        			 outToServer = new DataOutputStream(clientSocket.getOutputStream());
	        			 //Length of the music file is send to the server
	        			 outToServer.write(result,0,4);
	        			 if(match)
	        			 {
	        				 outToServer.write(result2,0,4);
	        				 outToServer.write(theStringByte,0,theStringByte.length);
	        				 int len;
		        			 int count = 0;
		        			 //The the actual music file in read 1024 bytes at a time and send to the peer
		        			 while ((len = in.read(buf)) > 0){
		        				 count++;
		        				 outToServer.write(buf,0,len);
		        				 System.out.println(len);
		        				 System.out.println(count);
		        			 }
		        			 
		        			 in.close();
	        			  }
	        			 // Log.d("ClientActivity", "C: Sent.");
	                } catch (Exception e) {
	                    //Log.e("ClientActivity", "S: Error", e);
	                }

					
					
				}
				serverSocket.close();
				startService(intent);
		        
		}
		catch(Exception e)
		{
				e.getMessage();
		}
    }
       
}