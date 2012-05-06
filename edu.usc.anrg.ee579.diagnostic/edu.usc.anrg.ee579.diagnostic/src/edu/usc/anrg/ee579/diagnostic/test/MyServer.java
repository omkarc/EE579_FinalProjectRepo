package edu.usc.anrg.ee579.diagnostic.test;

import java.io.IOException;

import edu.usc.anrg.ee579.diagnostic.Server;

public class MyServer {
	static boolean val=true;
  public static void main(String args[]) {
    try{
	  Server server = new Server(null,1);
	  server.startServer();
	 
    }catch (IOException e) {
		System.err.println("Could not listen on port");
		System.exit(1);
	}
  }
}
