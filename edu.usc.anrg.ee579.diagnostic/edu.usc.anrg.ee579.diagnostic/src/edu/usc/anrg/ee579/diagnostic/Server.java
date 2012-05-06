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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.io.OutputStream;



//import com.omkar.testForEE579.MultiThreadedSocProg.Server;

import edu.usc.anrg.ee579.diagnostic.protocol.Packet;


public class Server implements Runnable {	
	
	private	OutputStream outToClient; //  = null;
	private InputStream inFromClient; // = null;
	private ServerSocket serverSocket; // = null;
	private Socket clientSocket; // = null;
	private int serverPort = 9777;
	
	List<String> records = new ArrayList<String>();
	List<String> records2 = new ArrayList<String>();
	List<String> fileNames = new ArrayList<String>();
	List<String> recordsMatch = new ArrayList<String>();
	static Map<String,Long> keepAliveMap = new ConcurrentHashMap<String,Long>();//map to maintain mapping of users online to their time to live
	
	InetAddress addr=null;
	static String[] email;
	static String fileName;
	
	private int portID;
	int count = 0;
	String TimeStamp = new Date().toString();
	long timeToLive = 300000; //keepAlive time for clients
	boolean timeflag = true; //unused as of now!!
	
	//Constructor for the Server
	public Server(Socket s, int i) {
		  this.clientSocket = s;
		  this.portID = i;	  	  
	}
	//Passive open server. Ready to take new connection with multiThreading enviornment
	public void startServer() throws IOException {
		try {
			serverSocket = new ServerSocket(serverPort);
		    System.out.println("------------------------------------------");
		    System.out.println("Connection Established at: " +TimeStamp);
		    System.out.println();    
		    Thread timer = new Thread(new timeCheck());
		    timer.start();
			while(true) {
			    clientSocket = serverSocket.accept();
			    System.out.println("Server has accepted the socket connection");   
			    addr = clientSocket.getInetAddress();
				System.out.println(addr);
				
			    Runnable runnable = new Server(clientSocket, ++count);
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
			 
	//Timer Thread which keeps track of the online status of each user
	//if the user doesnot sends the Hello Messaege. that user status is removed from the Map
	public class timeCheck implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			/*Set<Entry<String, Long>> hashSet = keepAliveMap.entrySet();
			Iterator i = hashSet.iterator();*/
			//System.out.println("Enter run of timeCheck Method");
			while(true) {
				//System.out.println("Inside first while of timeCheck");					
				Set<Entry<String, Long>> hashSet = Server.keepAliveMap.entrySet();
				
				//System.out.println("HashMap inside of first while: "+Server.keepAliveMap);					
				//System.out.println("Set inside first while is: " +hashSet);
				
				Iterator<Entry<String, Long>> i = hashSet.iterator();
				while(i.hasNext()) {
					
					Map.Entry m = (Map.Entry)i.next();						
					//System.out.println("Map now is: " +m);
					String k = (String) (m.getKey());
					long val = (Long) m.getValue();
					if (val > 0) {							
						Server.keepAliveMap.put(k, val-10000);
					}
					else {
						Server.keepAliveMap.remove(k);
					}
						
				}
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}
		
		
	//@Override
	public void run() {
			// TODO Auto-generated method stub	
		try{		
	        inFromClient = clientSocket.getInputStream();				                                            
	        byte[] resultBuff = new byte[0];
		    byte[] buff = new byte[1024];
		    int k = 0;
		    //Reading bytes from the socket stream
		    //MAIN WHILE
		    while((k = inFromClient.read(buff, 0, buff.length)) > -1 ) {
		    
		    ByteBuffer cc = ByteBuffer.allocate(k);
		    cc=ByteBuffer.wrap(buff); 
	  	
		    int f1 = cc.getInt(0);
		    if (f1!=12) 
		    	System.out.println("Sever :INVALID PACKET.API_TYPE INCORRECT"); 
		    else 
		    {
		    	int f2 = cc.getInt(4);//System.out.println("total length :" +f2);
		    	int f3 =cc.getInt(8);//System.out.println("msg length :"+f3);
           
		    	String msgFromClient = Character.toString((char)cc.get(12));
		    	for(int i = 13;i<f2;i++) {
		    		byte b =cc.get(i);
		    		msgFromClient = msgFromClient + (char) b;
		    	}
		    	
		    	System.out.println("Client :" +msgFromClient);
			
		    	if(msgFromClient.contains("REGISTER")) {
					//This portions logs the registeration information of the user in the Registration.txt file
					String [] ClientDetails=msgFromClient.split(",");
					System.out.println("Client NAME :"+ClientDetails[1]);
					System.out.println("Client EMAIL ADDRESS:"+ClientDetails[2]);
					System.out.println("Client PASSWORD:"+ClientDetails[3]);
					
					FileOutputStream fos = new FileOutputStream("C:/EE579/workspace/project/Registration.txt",true);
					
					PrintStream ps = new PrintStream(fos);
					
					fos.write(ClientDetails[1].getBytes());
					fos.write(",".getBytes());
				    fos.write(ClientDetails[2].getBytes());
				    fos.write(",".getBytes());
				    fos.write(ClientDetails[3].getBytes());
				    ps.println();	//write a new line character
				    fos.close();
				    File f = new File("c:/EE579/workspace/project/Directory/"+ClientDetails[2]);
					f.mkdirs();
					File ff = new File("c:/EE579/workspace/project/Directory/"+ClientDetails[2]+"/Files");
					ff.mkdirs();
					new File("c:/EE579/workspace/project/Directory/"+ClientDetails[2]+"/Username.txt");
				    replyToClient("END");			  												
				}
				
				
				//=================================================================================================================
				else if(msgFromClient.contains("LOGIN")){
					//This portions helps the user to come online with info given in Registration
					//This allows the 
					String[] ClientLogin=msgFromClient.split(",");
					
					keepAliveMap.put(ClientLogin[1],timeToLive);
					System.out.println("Hash Map in LOGIN is: " +keepAliveMap);
					BufferedReader reader = new BufferedReader(new FileReader(
							"C:/EE579/workspace/project/Registration.txt"));//reader-an object of public class bufferedreader,stores the input data read from the file
					String login;
					
					boolean confirm=false;
					while ((login = reader.readLine()) != null)
	
					{		
						String[] LoginDetails=login.split(",");
						if(LoginDetails[1].equalsIgnoreCase(ClientLogin[1]) && LoginDetails[2].equalsIgnoreCase(ClientLogin[2]))
						{
							//System.out.println(login);
						  System.out.println("Client NAME:"+LoginDetails[0]);  
						  System.out.println("Client EMAIL ADDRESS:"+LoginDetails[1]);
							
	                      
	                        //Adding login details of clients who have requested login
	                      FileOutputStream fos1 = new FileOutputStream("C:/EE579/workspace/project/LoginDetails.txt",true);
	                      PrintStream ps1 = new PrintStream(fos1);
	                      ps1.println("*");
	                      ps1.println(LoginDetails[0]+","+LoginDetails[1]+","+clientSocket.getInetAddress());                      				    
	  					  ps1.close();
						  confirm = true;
						  replyToClient("TRUE");
						  closeConnection();
						}	
					}
					reader.close();				
					if (confirm == false){ replyToClient("FALSE"); }							
				}	//LOGIN end
	
				boolean value=true;	
		//=====================================================================================================================================
	
				//Making the server respond to queries other than "HELLO and "END"
				 if(msgFromClient.contains("GET LIST OF FRIENDS")){
					 //This is used to get the list of friends online 
					System.out.println("Recieved Request for list of friends");   
				
		         
					BufferedReader reader = new BufferedReader(new FileReader(
							"C:/EE579/workspace/project/LoginDetails.txt"));	//reader-an object of public class bufferedreader,stores the input data read from the file
					String line;
					String[] temp= null;
					while ((line = reader.readLine()) != null) {	
						//System.out.println(line);
						if(!line.startsWith("*"))
						{								
						records.add(line);
						temp=line.split(",");
						System.out.println("Sent:: Name::"+temp[0]+" Email:: "+temp[1]+"  IP Address : "+temp[2]);
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						replyToClient(temp[0]+","+temp[1]+", "+temp[2]);
		                temp=null;
					                }
					}
					reader.close();
					
						
					
					
					// replyToClient("END");
					closeConnection();
				
			    }	//END OF GET LIST OF FRIENDS
	//===============================================================================================================================           
				 else if(msgFromClient.contains("REQUESTED")){
						
            //This option is used to search for a specific user from the list of online users. Max of three users can be searched for.
						BufferedReader reader = new BufferedReader(new FileReader(
								"C:/EE579/workspace/project/LoginDetails.txt"));	//reader-an object of public class bufferedreader,stores the input data read from the file
						String line;
						String[] temp= null;
						String[] temp1= msgFromClient.split(",");
						System.out.println(temp1[1]+" "+temp1[2]+" "+temp1[3]);
						//System.out.println(temp1[1].length());
						int match=0;
						while ((line = reader.readLine()) != null) {	
							//System.out.println(line);
							if(!line.startsWith("*"))
							{								
							
							temp=line.split(",");
							//System.out.println(line);
							if(temp[0].contains(temp1[1])||temp[0].contains(temp1[2])||temp[0].contains(temp1[3])){
						
								match=1;
								System.out.println("Sent:: Name::"+temp[0]+" Email:: "+temp[1]+"  IP Address : "+temp[2]);
							try {
								Thread.sleep(500);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						
							replyToClient(temp[0]+","+temp[1]+", "+temp[2]);
							
							records.add(line);
							}
						
							temp=null;
						                }//end of if(!line.contains("*"))
						//}//end of if
						}//end of while
						if(match==0){
							System.out.println("zero");replyToClient("MATCH NOT FOUND"+","+"OR USER IS OFFLINE"+","+null);
							}
						
						reader.close();
						closeConnection();	
							
						
					}//end of REQUESTED
				
	  //=================================================================================================================
				 
				  if (msgFromClient.contains("GANG")){
					 //Get the List of Friends in the My Gang who are online
					 String[] temp=msgFromClient.split(",");
					 records2.add(temp[1]+","+temp[2]);
					//  System.out.println("hello");
					 System.out.println(msgFromClient);
	
				 }//END OF msgFromClient.contains("GANG")
	//=================================================================================================================			  
				 
				  if (msgFromClient.contains("END")){
				 
					  	//To terminate the connection with the other end 
				 
					  	BufferedReader reader = new BufferedReader(new FileReader(
								"C:/EE579/workspace/project/LoginDetails.txt"));	//reader-an object of public class bufferedreader,stores the input data read from the file
						String line;
						String[] temp= null;
						
						int match=0;
						while ((line = reader.readLine()) != null) {	
							//System.out.println(line);
							if(!line.startsWith("*"))
							{								
							
							temp=line.split(",");
							//System.out.println(line);
							for(int i=0;i<records2.size();i++)
							{
								String temp2 = records2.get(i);
								String[] temp1=temp2.split(",");
								
							if(temp[0].equals(temp1[0]) && temp[1].equals(temp1[1]))
							  {
						
								match=1;
								System.out.println("Sent:: Name::"+temp[0]+" Email:: "+temp[1]+"  IP Address : "+temp[2]);
							  try {
								Thread.sleep(150);}
							    catch (InterruptedException e) {
								// TODO Auto-generated catch block
								  e.printStackTrace();}
							
	           					replyToClient(temp[0]+","+temp[1]+", "+temp[2]);
							
						//	records.add(line);
							}//end of if
							}//end of for
						
							temp=null;
						  }//end of if(!line.contains("*"))
	
						}//end of while
						if(match==0){
							System.out.println("zero");replyToClient("MATCH NOT FOUND"+","+"OR USER IS OFFLINE"+","+null);
							}
						
						reader.close();
						closeConnection();	
							
						
					}//end of END
	 //=======================================================================================================================
				  if(msgFromClient.contains("LOG OFF")){
					  
					  //To go offline for the user
					  BufferedReader reader = new BufferedReader(new FileReader(
								"C:/EE579/workspace/project/LoginDetails.txt"));	//reader-an object of public class bufferedreader,stores the input data read from the file
						String line;
					  
					  records.clear();
						
					 
	                 String[] temp = msgFromClient.split(",");
	                 keepAliveMap.remove(temp[1]);
	                 System.out.println("Hash Map in LOG OFF is: " +keepAliveMap);
	                 while ((line = reader.readLine()) != null) {	
							
							if(!line.startsWith("*"))
							{							
							if(!line.contains(temp[1]))
								{records.add(line);
							System.out.println(line);}
							}
							}
	                 
	                 FileOutputStream fos1 = new FileOutputStream("C:/EE579/workspace/project/LoginDetails.txt"); 
	                 PrintStream ps1 = new PrintStream(fos1);
	                 
	                 
	                 for(int i=0;i<records.size();i++)
	                 {
	                	 
					      ps1.println("*");
	                	  ps1.println(records.get(i));
	                	  //System.out.println("Match found");
					      
	                	  
	                 }
				  ps1.close();
				  closeConnection();
				  
				  }
				  
	//=================================================================================================================
				  
				  if(msgFromClient.contains("CALL"))	{
					  
					//To send a requested file to the user  
					String[] temp = msgFromClient.split(",");
					
					sendFile(temp[1]);
				    closeConnection();
				  
				  }  
	//=================================================================================================================
				  
				  if(msgFromClient.contains("LOCK"))	{
					  
					 //TO Lock your account so that no one can use it 
					 String[] temp = msgFromClient.split(",");
					
					 FileOutputStream fos1 = new FileOutputStream("C:/EE579/workspace/project/Directory/"+temp[1]+"/Username.txt");  
					 PrintStream ps1 = new PrintStream(fos1);
					 ps1.print(temp[2]); 
					 ps1.close(); 
				     closeConnection();
				  
				  }  
	//==================================================================================================================
				   if(msgFromClient.contains("Share")){
					  
					  //To add fileNames in the list of files shared
					  String[] temp=msgFromClient.split(",");
					  fileNames.add(temp[1]);
				  }
				  
				  else if(msgFromClient.contains("COMPLETED"))
				  {
					  //To send a list of shared files of a particular user
					  String[] temp=msgFromClient.split(",");
					  FileOutputStream fos1 = new FileOutputStream("C:/EE579/workspace/project/Directory/"+temp[1]+"/FileNames.txt");  
					  PrintStream ps1 = new PrintStream(fos1);
					  
					  for(int i=0;i<fileNames.size();i++){
						  ps1.println(fileNames.get(i)); 
					  
					  }
					  ps1.close(); 
					  closeConnection();
					   
				  }  
	//============================================================================================================================================
				   if(msgFromClient.contains("QUERY")){
					   	//To search for a particular file on server
					    String[] temp=msgFromClient.split(",");
					    FileInputStream fstream1 = new FileInputStream("c:/EE579/workspace/project/LoginDetails.txt");
						// Get the object of DataInputStream
						DataInputStream in1 = new DataInputStream(fstream1);
						BufferedReader br1 = new BufferedReader(new InputStreamReader(in1));
						String strLine1;String[] temp1=null;
						while ((strLine1 = br1.readLine()) != null)
						{
							if(!strLine1.contains("*")){
							    temp1=strLine1.split(",");
								
								String UserPath ="C:/EE579/workspace/project/Directory/"+ temp1[1]+"/FileNames.txt";
							
							
								File folder = new File(UserPath);
								if(folder.exists())
								{	  
								    FileInputStream fstream = new FileInputStream(UserPath);
								    
								    DataInputStream in = new DataInputStream(fstream);
								    BufferedReader br = new BufferedReader(new InputStreamReader(in));
								    String strLine;
								  
								    while ((strLine = br.readLine()) != null)   {
								  
									  	if(strLine.contains(temp[1]))
									  	{
									  		recordsMatch.add( strLine+","+temp1[1]+","+temp1[2]);
									  		System.out.println(strLine+","+temp1[1]+","+temp1[2]);
									  	}
								    }  //end of while
								    in.close();
								}
							}//end of if
						}//end of while
						in1.close();
					System.out.println(recordsMatch.size());
					if(recordsMatch.size()>0){
						
						for(int i=0;i<recordsMatch.size();i++){
						replyToClient(recordsMatch.get(i));
						System.out.println("sent  "+recordsMatch.get(i));
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}	
						}//end of for
						replyToClient("END");
					
					}
					else {replyToClient("Sorry,No Matches Found,null");
					      replyToClient("END");
					      
					}
					closeConnection();
				}//end of msgFromClient.contains(QUERY)
	//=================================================================================================================				
				   if(msgFromClient.contains("FILES")){
					   	//Get list of files shared by a particular user
					   
				   		String[] temp=msgFromClient.split(",");
				   		String UserPath ="C:/EE579/workspace/project/Directory/"+ temp[1]+"/FileNames.txt";
						
						
						File User = new File(UserPath);
						if(User.exists()){	  
						    FileInputStream fstream = new FileInputStream(UserPath);
						    DataInputStream in = new DataInputStream(fstream);
						    BufferedReader br = new BufferedReader(new InputStreamReader(in));
						    String strLine;
						  
						    while ((strLine = br.readLine()) != null)   {
						  
							  
							  		System.out.println("sent to client"+","+strLine);
							  		replyToClient(strLine);
						    	try {
									Thread.sleep(500);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}	
						    	
						    }//END OF WHILE
						    in.close();br.close();
						    replyToClient("QUIT");	
						    System.out.println("QUIT");
						
						}//end of if  User exists
						else 
							replyToClient("QUIT");
						//System.out.println("END again");
						
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}	
						
						closeConnection();
							
						
				   }//end of msgFromClient.contains("FILES")
	//================================================================================================================
				   
				   if(msgFromClient.contains("STORE")){
					   //To save a user's file on the server
					   email=msgFromClient.split(",");
					   fileName=email[2];
					   String response = saveFile();
					   System.out.println("Response Received is: " +response);
					   /*Receiver server2 = new  Receiver(null,1);
					   	 try {
					   		server2.startServer();
					   		System.out.println("SECOND SERVER STARTED");
					   	} catch (IOException e) {
					   		// TODO Auto-generated catch block
					   		e.printStackTrace();
					   	}*/
					
					   
					   replyToClient("QUIT");
					   
					   closeConnection();
						
						 
					   
				   }
				   
	//=====================================================================================================================
				   if(msgFromClient.contains("SEARCH")){
					   //Search for File on the server for other user
					   String[] temp=msgFromClient.split(",");
					 	String UserPath ="C:/EE579/workspace/project/Directory/"+ temp[1]+"/Files";
						String password = temp[2];
						File Username=new File("C:/EE579/workspace/project/Directory/"+temp[1]+"/Username.txt");
						    int isPasswordCorrect=0;
						
							File yourDir;
							File User = new File(UserPath);
							
							if(User.exists()){	
								System.out.println("User Exists");
								yourDir = new File(UserPath);
								String[]  names= yourDir.list();
								
								if(Username.exists())
								{
									   FileInputStream fstream = new FileInputStream(Username);
									    
									    DataInputStream in = new DataInputStream(fstream);
									    BufferedReader br = new BufferedReader(new InputStreamReader(in));
									    String strLine;
									  
									    while ((strLine = br.readLine()) != null)   {
									  
										 //   System.out.println("password is "+strLine);
									    	if(strLine.equals(password)){
									    		 System.out.println("password is "+strLine);isPasswordCorrect=1;
									    	}
									    }
								}
								 if(!Username.exists()){
									System.out.println("Incorrect Password");
									isPasswordCorrect=1;}
									    		
								
								if(isPasswordCorrect==1)
								{
								
									for(int i=0;i<names.length;i++)	  
									{
											  
										        System.out.println("sent to client"+","+names[i]);
										    	replyToClient(names[i]);
										        
										        try {
													Thread.sleep(800);
												
												} catch (InterruptedException e) {
													// TODO Auto-generated catch block
													e.printStackTrace();
												}    	
										    
									}	       ///END OF for 
						    	
								}	//end of if isPasswordCorrect==1
						 if(isPasswordCorrect==0){
							 	System.out.println("incorrect");
							 	replyToClient("Incorrect Password Email mp3");}
						 		try {
						 			Thread.sleep(800);
							
						 		} catch (InterruptedException e) {
						 			// TODO Auto-generated catch block
						 			e.printStackTrace();
						 		}    	
								
						 		replyToClient("QUIT");	
						 		System.out.println("QUIT");
							
						}//end of if  User exists
									
						else {
								System.out.println("User Does Not Exist");
								replyToClient("QUIT");
						}
						closeConnection();
							
						  
					   
					   
				   }//end of if msgFromClient.contains()
	//================================================================================================================
				   //KeepAlive Handling
				   if(msgFromClient.contains("HELLO")) {
					   //Handle the hello message from the user to update the timer
					   
					   String[] temp= msgFromClient.split(",");
					   System.out.println("The first index in Hello is: " +temp[0]);
					   Long timeCheck = (Long) keepAliveMap.get(temp[1]);	//gets values from the given key(email address);
					   
					   if(timeCheck > 0) {
						   keepAliveMap.put(temp[1],timeToLive);
					   }
					   else {
						   keepAliveMap.remove(temp[1]);
					   }
					   System.out.println("Hash Map in HELLO is: " +keepAliveMap);				   				   				   
				   }
				   
				  /* if(msgFromClient.contains("REQUEST")) {
					   
					   String[] temp=msgFromClient.split(",");
					   String getPath ="C:/EE579/workspace/project/Directory/"+temp[1]+"/Files";
					   String donor = 
					   File toSend = new File(getPath);
					    
				   }*/
				   
				   
				   
	//=================================================================================================================
				   }	//END OF MAIN ELSE
	           }	//END OF MAIN WHILE
		    
           
			} catch(Exception e){
				e.getMessage();
			}
		
		} // end of run() method
			
		//Function describes what the server should respond to client's queries
		public void replyToClient(String msgFromServer) throws IOException{
			
			   
				  
				  outToClient = new DataOutputStream(clientSocket.getOutputStream());
				  Packet packet = new Packet(msgFromServer);
				  int lengthOfPacket = packet.bufferToBytes.length;
				  
				  //writing bytes to the output socket stream
				  outToClient.write(packet.bufferToBytes,0,lengthOfPacket);//}
				  //if (msgFromClient.equalsIgnoreCase("END")) closeConnection();
			
		
		}//end of replyToClient()
		
		//Save the incoming file in the corresponding database of the user
		public String saveFile()
		{
	      	  DataOutputStream outToServer = null;
	  		  ByteBuffer outServer;
	          String Confirm = "";


				 try {
					  //InputStream inFromBuffer = null;
					  byte[] buffer = new byte[4];
					  //inFromBuffer = clientSocket.getInputStream();
					  //Server first receives the length of the Music file to be received
					  //inFromBuffer.read(buffer, 0, 4);
					  //Log.v( "PhoneServer", "fff");
					  ByteBuffer api = ByteBuffer.wrap(buffer);
					  int message = api.getInt();
					  System.out.println("Message type is" +  message);
					  //Toast.makeText(getApplicationContext(), musicFileSize, Toast.LENGTH_SHORT).show();
					  
					  
					  byte[] fileName = new byte[4];
					  inFromClient.read(fileName, 0, 4);
					  ByteBuffer apifile = ByteBuffer.wrap(fileName);
					  int fileNameSize = apifile.getInt();
					  System.out.println("the filename size is " + fileNameSize);

					
					  byte[] fileActualname = new byte[fileNameSize];
					  inFromClient.read(fileActualname,0,fileNameSize);
					  System.out.println("inFromBuffer: " +new String(inFromClient.toString()));
					  String fileString = new String(fileActualname);
					  System.out.println("The actual file name is :" + fileString);
					  File f2 = new File("c:/EE579/workspace/project/Directory/"+Server.email[1]+"/Files/"+fileString);
				//	T	oast.makeText(getApplicationContext(), "started", Toast.LENGTH_SHORT).show();
					  
					  
					  
					  byte[] fileName1 = new byte[4];
					  inFromClient.read(fileName1, 0, 4);
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
						  int read = inFromClient.read(limit,0,1024);
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
				
					  Confirm ="TRUE";
					  //clientSocket.close();
				  
				 }   catch (IOException e) {
					    System.err.println("ERROR: Accept Failed!!");
					    System.exit(1);
					    e.printStackTrace();
				 }
				 
				 return Confirm;

		}
		
		//Copies bytebuffer data in byte array
	    public static byte[] ByteCopy(ByteBuffer b) {
	    	byte[] output = new byte[b.limit()];
	    	for (int i = 0; i < b.limit(); i++) {
	    		output[i] = b.get(i);
	    	}
	    	return output;
	    }

	    //Send a requested file for a particular user to the requested user
		public void sendFile(String temp)
		{
			
			
			String file1 = "C:/EE579/workspace/project/Directory/"+temp+"/Files";
			int i;
			
            try 
            {
                 //Log.d("ClientActivity", "C: Sending command.");
                 //This is where the file actually start sending to the server
            	
				inFromClient = clientSocket.getInputStream();
				//System.out.println(addr);
				
				
				
				
				byte[] fileName = new byte[4];
				inFromClient.read(fileName, 0, 4);
				ByteBuffer apifile = ByteBuffer.wrap(fileName);
				int fileNameSize = apifile.getInt();
				System.out.println(fileNameSize);

				
				byte[] fileActualname = new byte[fileNameSize];
				inFromClient.read(fileActualname,0,fileNameSize);
				String fileString = new String(fileActualname);

				
            	 File f1;
            	 InputStream in = null ;
            	 int size = 0;
            	 String sendtoServer;
				 byte[] theStringByte = null; 
				 byte[] result2 = null;
				 ByteBuffer b2;
            	
        		 f1 = new File(file1+"/"+fileString);
        		 in = new FileInputStream(f1);
        		 size = (int) f1.length();
            	
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

				 int len;
    			 int count = 0;
    			 //The the actual music file in read 1024 bytes at a time and send to the server
    			 while ((len = in.read(buf)) > 0){
    				 count++;
    				 outToServer.write(buf,0,len);
    				 System.out.println(len);
    				 System.out.println(count);
    			 }
        			 
        			 in.close();
    			  
    			 // Log.d("ClientActivity", "C: Sent.");
            } catch (Exception e) {
                //Log.e("ClientActivity", "S: Error", e);
            }


		}
		//Close the connection
		public void closeConnection() throws IOException {
			
			System.out.println("connection closed successfully");
			outToClient.close();
			inFromClient.close();
	        clientSocket.close();
	        serverSocket.close();		
		}
	}	//end of class Server

	
