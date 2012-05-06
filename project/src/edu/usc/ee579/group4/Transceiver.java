package edu.usc.ee579.group4;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import android.app.Activity;
import android.net.ParseException;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;
    
//This Activity is used to send request to the peer to get a file
public class Transceiver  {

	static String Confirm2="FALSE";
	Socket socket;
	//Construct 
	public Transceiver() throws IOException {    	
		//  initializeUser();
	}
  
	//This functionality basically transfers the file peer to our phone
	void initializeUser(String IPaddress) throws IOException{
	
		System.out.println("requested");
		//  System.out.println(FriendList.temp[2].split("/")[1]);
		try {
		  
			socket = new Socket(IPaddress, 8080);
			System.out.println(socket);
	 
			Log.v("ClientActivity", "C: Connecting...");
            
            //connected = true;
            
            System.out.println(FriendList.temp[2]);
			//InetAddress serverAddr = InetAddress.getByName(ip[1]);
				
            System.out.println(socket);
             
            DataOutputStream outToServer = null;
            ByteBuffer outServer;
                    
            ByteBuffer b1 = ByteBuffer.allocate(4);
            //b.order(ByteOrder.BIG_ENDIAN); // optional, the initial order of a byte buffer is always BIG_ENDIAN.
            //Request message send
            b1.putInt(0);
            byte[] result1 = new byte[4];
            result1 = ByteCopy(b1);
            //  System.out.println("Posiiton is: " + position);
            String sendtoServer=null;
            //The fileName which is been requested
            if(GetFileActivity.const2==1)
            {
    				  
            	sendtoServer =GetFileActivity.Query;
            }
            else 
            	sendtoServer = FriendList.temp[0];
            
            byte[] theStringByte = sendtoServer.getBytes();
    			  
    			  
            ByteBuffer b2 = ByteBuffer.allocate(4);
            //b.order(ByteOrder.BIG_ENDIAN); // optional, the initial order of a byte buffer is always BIG_ENDIAN.
            //The String Length of the filename is sent
            b2.putInt(theStringByte.length);
            byte[] result2 = new byte[4];
            result2 = ByteCopy(b2);
    			  
            outToServer = new DataOutputStream(socket.getOutputStream());

            outToServer.write(result1,0,4);
            outToServer.write(result2,0,4);
            outToServer.write(theStringByte,0,theStringByte.length);
            
            InputStream inFromBuffer = null;
            byte[] buffer = new byte[4];
            inFromBuffer = socket.getInputStream();
            //first receives the length of the Music file to be received
            inFromBuffer.read(buffer, 0, 4);
            //  Log.v( "PhoneServer", "fff");
            ByteBuffer api = ByteBuffer.wrap(buffer);
            int musicFileSize = api.getInt();
            System.out.println(musicFileSize);
            //If the file size is greater than 0 then we are gone get file otherwise NO
            if(musicFileSize > 0)
            {
            	byte[] fileName = new byte[4];
            	inFromBuffer.read(fileName, 0, 4);
            	ByteBuffer apifile = ByteBuffer.wrap(fileName);
            	int fileNameSize = apifile.getInt();
            	System.out.println(fileNameSize);
            	
            	
            	byte[] fileActualname = new byte[fileNameSize];
            	inFromBuffer.read(fileActualname,0,fileNameSize);
				String fileString = new String(fileActualname);
				
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
				
				Confirm2="TRUE";
            }//end of if
    			  
		} catch (ParseException e) {
			e.printStackTrace();
	  	}
		    
            
	}//requestFile()
	
	//Bytes to be copied from byteBuffer to Byte array
	public static byte[] ByteCopy(ByteBuffer b) {
		byte[] output = new byte[b.limit()];
		for (int i = 0; i < b.limit(); i++) {
			output[i] = b.get(i);
		}
		return output;
	   
	}	
}
