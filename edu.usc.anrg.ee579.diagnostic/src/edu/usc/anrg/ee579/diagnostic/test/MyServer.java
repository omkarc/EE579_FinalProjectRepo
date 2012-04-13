package edu.usc.anrg.ee579.diagnostic.test;

import java.io.IOException;

import edu.usc.anrg.ee579.diagnostic.Server;

public class MyServer {
	static boolean val=true;
  public static void main(String args[]) {
    try{
	  Server server = new Server(null,1);
    
    //server.startServer() creates a socket at the server side
	  for(int i =1;i<=10;i++){
	  server.startServer();
	//  System.out.println("running");
	  }

    }catch (IOException e) {
		System.err.println("Could not listen on port");
		System.exit(1);
	}
  }
}
