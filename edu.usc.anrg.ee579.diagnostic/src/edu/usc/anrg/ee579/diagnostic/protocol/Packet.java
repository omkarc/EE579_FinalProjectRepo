package edu.usc.anrg.ee579.diagnostic.protocol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.Buffer;


public class Packet extends Protocol {


public ByteBuffer buffer;
public byte b;
public byte[] bufferToBytes;
public  String msgFromServer;
public String[] listOfFileNames;
public static int[] ArrayOfIndex = new int[20];

//the following method describes how the packet is composed before it is sent to thr server

public Packet(String msg) throws IOException {    
  

 
   //this method describes what packet is sent when client asks for certain file.
	/*if( msg.equalsIgnoreCase("GET"))
	  {
	   String fromUser;
	   System.out.println("Enter the Index Number of File :");
	   BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
	   fromUser = stdIn.readLine();
	   if (fromUser != null) {
	
		if (Integer.parseInt(fromUser)<=ArrayOfIndex.length)
		
	 msg = msg + "," + ArrayOfIndex[Integer.parseInt(fromUser)-1];
		else msg = msg + "," + "0";
	  
	   }
	  
	  } */
  this.message=msg;
  msgLength = message.length();
  
  totalLength = HEADERLENGTH + msgLength;
  //writing the data to bytebuffer format
  buffer =ByteBuffer.allocate(totalLength);
  buffer.putInt(APITYPE);
  buffer.putInt(totalLength);
  buffer.putInt(msgLength);
  
      byte[] bytes = message.getBytes("ASCII");
      buffer.put(bytes,0,msgLength);
      
     
      //converting bytebuffer to byte array format
       bufferToBytes = new byte[totalLength];
		for ( int index=0; index<buffer.limit(); index++ )
		   {
		   byte charAtIndex = buffer.get(index);
		   bufferToBytes[index] = charAtIndex;
		   
		   }
      
		
		  }
            
public Packet() {
	
}

@Override
//method to extract the message part from the bytes read from the stream
public void  readPacket(InputStream inFromBuffer ){
	
	 byte[] resultBuff = new byte[0];
	    byte[] buff = new byte[1024];
	    int bytesRead = 0;
	 
	    try {
	    	bytesRead = inFromBuffer.read(buff, 0, buff.length);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    ByteBuffer newBuffer = ByteBuffer.allocate(bytesRead);
	    newBuffer = ByteBuffer.wrap(buff); 
	  	
	       int f1 = newBuffer.getInt(0);
        if (f1!=12) {
        	
        	System.out.println("Sever :INVALID PACKET.API_TYPE INCORRECT");
        }
        else {
        int f2 = newBuffer.getInt(4);//System.out.println("total length :" +f2);
        int f3 =newBuffer.getInt(8);//System.out.println("msg length :"+f3);
        
         msgFromServer = Character.toString((char)newBuffer.get(12));
        for(int i = 13;i<f2;i++){
		   	byte charAtIndex =newBuffer.get(i);
		   	msgFromServer = msgFromServer + (char) charAtIndex;	
		   	
        }//end of for()
       // if(msgFromServer.contains("GET"))
       // {
       // 	String [] fileDescription = msgFromServer.split(",");
       // 	msgFromServer=fileDescription[4];
      //  }
        if (msgFromServer.contains("LIST"))
        {
        
         listOfFileNames =  msgFromServer.split(",");
int count=0;         
          for (int i=1;i<listOfFileNames.length;i=i+2)
        	 
         {
        	  ArrayOfIndex[count]=Integer.parseInt(listOfFileNames[i]);
        	//sendArrayOfIndex(ArrayOfIndex[count]); 
        	 count++;
         }
        //sendArrayOfIndex(ArrayOfIndex);
        }
     newBuffer = null;
       
        }//end of else

}//end of readPacket()
	 


public void getMessageType() {
  
}

public void getSomething(String text) {
  
}

}
