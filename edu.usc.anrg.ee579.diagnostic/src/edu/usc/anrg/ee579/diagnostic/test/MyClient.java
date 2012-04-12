package edu.usc.anrg.ee579.diagnostic.test;


import java.io.IOException;
import java.io.*;
import edu.usc.anrg.ee579.diagnostic.Client;

public class MyClient {
  public static void main(String args[]) throws Exception{
	  
	  try {
      Client client = new Client();
     
      client.sendMessage("HELLO");
      client.sendMessage("ABCD");//FOR INVALID MESSAGE TYPE
 
      client.sendMessage("LIST");
      client.sendMessage("GET");
 
      client.sendMessage("LIST453");//FOR INVALID MESSAGE TYPE
      
      client.sendMessage("GET123");//FOR INVALID MESSAGE TYPE
      
      client.sendMessage("END");
      
      boolean isDisconnected = client.closeConnection();
      if(isDisconnected)
        System.out.println("Connection with server closed successfully");
      else
        System.out.println("Something went wrong while closing the connection");
    } catch(IOException e) {
      e.printStackTrace();
    }
  }
}

