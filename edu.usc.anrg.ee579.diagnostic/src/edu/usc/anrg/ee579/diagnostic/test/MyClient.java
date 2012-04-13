package edu.usc.anrg.ee579.diagnostic.test;


import java.io.*;
import edu.usc.anrg.ee579.diagnostic.Client;
import edu.usc.anrg.ee579.diagnostic.Client1;

public class MyClient {
  public static void main(String args[]) throws Exception{
	  
	  try {
      Client1 client1 = new Client1();
     
      client1.sendMessage("HELLO");
      try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
      client1.sendMessage("ABCD");//FOR INVALID MESSAGE TYPE
      try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
      
      client1.sendMessage("GET LIST OF FRIENDS");
      client1.sendMessage("END");   
      boolean isDisconnected = client1.closeConnection();
      if(isDisconnected)
        System.out.println("Connection with server closed successfully");
      else
        System.out.println("Something went wrong while closing the connection");
    } catch(IOException e) {
      e.printStackTrace();
    }
  }
}

