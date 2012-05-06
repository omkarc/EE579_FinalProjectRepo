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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.io.OutputStream;

//import com.omkar.testForEE579.MultiThreadedSocProg.Server;

import edu.usc.anrg.ee579.diagnostic.protocol.Packet;



import edu.usc.anrg.ee579.*;
public class ReceiverThread implements Runnable {	
	
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
	int count = 0;
	String TimeStamp = new Date().toString();

	//Omkar's Server constructor!!
	  public ReceiverThread(Socket s, int i) {
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
				while(true) {
				    clientSocket = serverSocket.accept();
				    System.out.println("Server has accepted the socket connection");   
				    addr = clientSocket.getInetAddress();
					System.out.println(addr);
					
				    Runnable runnable = new ReceiverThread(clientSocket, ++count);
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
		//	Socket client = serverSocket.accept();
	      //In our code it will be client actually will be receiving the file
      
			byte[] buffer = new byte[4];
			inFromBuffer = clientSocket.getInputStream();
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
			System.out.println(Server.email[1]);
			File f2 = new File("c:/EE579/workspace/project/Directory/"+Server.email[1]+"/"+Server.fileName);
			while(totalread != musicFileSize)
			{
				//the music file is to be read 1024 bytes at a time
				System.out.println(musicFileSize);
				int read = inFromBuffer.read(limit,0,1024);
				
				
				for(int i = 0; i< read;i++)
				{
					totalFile[totalread] = limit[i];
					totalread++;
					
				}
				System.out.println("read  "+read);
				
			}
			
			//finally the byte array is written into the file
			OutputStream out = new FileOutputStream(f2);
			out.write(totalFile, 0, musicFileSize);
			out.close();
		//	Toast.makeText(getApplicationContext(), "finished", Toast.LENGTH_SHORT).show();
			serverSocket.close();
			System.out.println("file transfer complete");
        //    break;
                
		} catch(Exception e){
			e.getMessage();
		}
		
		} // end of run() method
			
		
		
		
		public void closeConnection() throws IOException {
			
			System.out.println("connection closed successfully");
			outToClient.close();
			inFromBuffer.close();
	        clientSocket.close();
	        serverSocket.close();		
		}
	}	//end of class Server

	
