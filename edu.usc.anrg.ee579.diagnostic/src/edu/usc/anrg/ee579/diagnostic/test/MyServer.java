package edu.usc.anrg.ee579.diagnostic.test;

import java.io.IOException;

import edu.usc.anrg.ee579.diagnostic.Server;

public class MyServer {
  public static void main(String args[]) {
    try{
	  Server server = new Server();
    
    //server.startServer() creates a socket at the server side
	  server.startServer();

    }catch (IOException e) {
		System.err.println("Could not listen on port");
		System.exit(1);
	}
  }
}
