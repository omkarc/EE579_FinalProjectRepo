package edu.usc.ee579.Protocol;

import java.io.BufferedReader;
import java.io.InputStream;


public abstract class Protocol {
  protected final int APITYPE = 12;  
  protected final int APITYPELENGTH = 4; //length of the APITYPE field in bytes 
  protected final int TOTALLENLENGTH = 4;  //length of the TOTALLEN field in bytes
  protected final int MSGLEN_LENGTH = 4; //length of the MSG_LEN field in bytes
  protected final int HEADERLENGTH = APITYPELENGTH + TOTALLENLENGTH + MSGLEN_LENGTH; 
  protected int totalLength;
  protected int msgLength;
  protected String message;
  protected String errorMessage;
  
   
  public String getMessage() {
    return message;
  }
  
  public int getMessageLength() {
    return msgLength;
  }
  
  public abstract void readPacket(InputStream inFromBuffer);
}
