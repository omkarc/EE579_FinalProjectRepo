package edu.usc.anrg.ee579.diagnostic;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.Buffer;
import java.nio.CharBuffer;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;
import java.nio.charset.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.io.OutputStream;



//import com.omkar.testForEE579.MultiThreadedSocProg.Server;

import edu.usc.anrg.ee579.diagnostic.protocol.Packet;



import edu.usc.anrg.ee579.*;

public class Receiver {	
	
	private	OutputStream outToClient; //  = null;
	private InputStream inFromClient; // = null;
	private ServerSocket serverSocket; // = null;
	private Socket clientSocket; // = null;
	private int serverPort = 8080;
	InetAddress addr=null;
  	InputStream inFromBuffer;
	String UserLocation=null;
	//new variables -Omkar
	private int portID;
	int count = 0;   static String Confirm2="FALSE";
	String TimeStamp = new Date().toString();

	//Omkar's Server constructor!!
	  public Receiver(Socket s, int i) {
		  this.clientSocket = s;
		  this.portID = i;	  	  
	  }
	  
		public void startServer() throws IOException {
			try {
		//		UserLocation=email;
		//		System.out.println(UserLocation);
				serverSocket = new ServerSocket(serverPort);
			    System.out.println("------------------------------------------");
			    System.out.println("Connection Established at: " +TimeStamp);
			    System.out.println();    
				
				    clientSocket = serverSocket.accept();
				    System.out.println("Server has accepted the second socket connection");   
				    addr = clientSocket.getInetAddress();
					System.out.println(addr);
					
				    Runnable runnable = new ReceiverThread(clientSocket, ++count);
				    System.out.println(clientSocket.toString() + " " +"Count: " +count);
				    
				    Thread t = new Thread(runnable);
				    Thread fst = new Thread(new SecondServer());
				    fst.start();
				    t.start();
				    
					} 
				
			    catch (IOException e) {
			    System.err.println("ERROR: Accept Failed!!");
			    System.exit(1);
			    }
			  }	//end of startServer() function			
			 
		//@Override
		public class SecondServer implements Runnable {
		public void run() {
			// TODO Auto-generated method stub	

      	  DataOutputStream outToServer = null;
  		  ByteBuffer outServer;
          


			 try{
				  InputStream inFromBuffer = null;
				  byte[] buffer = new byte[4];
				  inFromBuffer = clientSocket.getInputStream();
				  //Server first receives the length of the Music file to be received
				  //inFromBuffer.read(buffer, 0, 4);
				  //Log.v( "PhoneServer", "fff");
				  ByteBuffer api = ByteBuffer.wrap(buffer);
				  int message = api.getInt();
				  System.out.println("Message type is" +  message);
				  //Toast.makeText(getApplicationContext(), musicFileSize, Toast.LENGTH_SHORT).show();
				  
				  
				  byte[] fileName = new byte[4];
				  inFromBuffer.read(fileName, 0, 4);
				  ByteBuffer apifile = ByteBuffer.wrap(fileName);
				  int fileNameSize = apifile.getInt();
				  System.out.println("the filename size is " + fileNameSize);

				
				  byte[] fileActualname = new byte[fileNameSize];
				  inFromBuffer.read(fileActualname,0,fileNameSize);
				  System.out.println("inFromBuffer: " +new String(inFromBuffer.toString()));
				  String fileString = new String(fileActualname);
				  System.out.println("The actual file name is :" + fileActualname);
				  File f2 = new File("c:/EE579/workspace/project/Directory/"+Server.email[1]+"/Files/"+fileActualname);
			//	T	oast.makeText(getApplicationContext(), "started", Toast.LENGTH_SHORT).show();
				  
				  
				  
				  byte[] fileName1 = new byte[4];
				  inFromBuffer.read(fileName1, 0, 4);
				  ByteBuffer apifile1 = ByteBuffer.wrap(fileName1);
				  int musicFileSize = apifile1.getInt();
				  System.out.println("the music file size is " + musicFileSize);
				  
				  
				  byte[] totalFile = new byte[musicFileSize];
				  byte[] limit = new byte[1024];
				  int totalread = 0;
				  //The file in which the incoming data is to be written
				  //String pathString = "/sdcard/fav/" + fileString;
				  //File f2 = new File(pathString);
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
				  clientSocket.close();
			  
			 }   catch (IOException e) {
				    System.err.println("ERROR: Accept Failed!!");
				    System.exit(1);
				    }

		
		} // end of run() method
		}
			
public static byte[] ByteCopy(ByteBuffer b) {
	byte[] output = new byte[b.limit()];
	for (int i = 0; i < b.limit(); i++) {
		output[i] = b.get(i);
	}
	return output;

}		
		
		
		public void closeConnection() throws IOException {
			
			System.out.println("connection closed successfully");
			outToClient.close();
			inFromBuffer.close();
	        clientSocket.close();
	        serverSocket.close();		
		}
	}	//end of class Server

	
