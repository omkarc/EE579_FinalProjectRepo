package edu.usc.ee579.group4;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;

import android.content.DialogInterface;
import android.content.Intent;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.util.Log;
import android.view.View;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.*;






public class MusicActivity extends ListActivity {
	 
	int count;
	List<String> records = new ArrayList<String>();
    
	List<String> records2 = new ArrayList<String>();
	List<String> records3 = new ArrayList<String>();
	List<String> records4 = new ArrayList<String>();
	File yourDir;
	static String filename;
    public String[] names=null;
    public String[] names1=null;
    ListView listView;
    ProgressDialog progressBar;
	private int progressBarStatus = 0;
	private Handler progressBarHandler = new Handler();
	 
	
    
    String[] ext={"mp3","wav","avi","mp4","mpeg","mpg","jpg","jpeg","png"};
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getListView().setBackgroundResource(R.drawable.wall1);
        File sdCardRoot = Environment.getExternalStorageDirectory();
        //This creates a favorites and My downloads folder for the user 
        if(ProjectActivity.downloads!=1)
        	yourDir = new File(sdCardRoot, "/favorites");
        else
        	yourDir = new File(sdCardRoot, "/My Downloads");
            
        names= yourDir.list();
        if(records.size()!=0)
        	records.clear();
  
        if(records2.size()!=0)
        	records2.clear();
  
   
        if(FriendList.flag==4 || ProjectActivity.const1==4 || ProjectActivity.downloads==1){
        	FriendList.flag=0;
        	for(int i=0;i<names.length;i++)
        	{
	        		
        		records3.add(names[i]);
    		}
	        	openAll(records3);
        }
        else
        {
        	records2= checkExtension(AccessActivity.flag);
        
    
        	OpenFile(records2,AccessActivity.flag);
    	}
    
    }
    //This functionality is used to open a particular file depending on its type
    public void OpenFile(final List<String> records4,final int flag){
	  
    
    	callView(records4);
	
    	listView.setOnItemClickListener(new OnItemClickListener() {
	      	@Override
	      	public void onItemClick(AdapterView<?> parent, View view,final int position, long id) {
	      		
	      		AlertDialog.Builder alert = new AlertDialog.Builder(MusicActivity.this); 
		      
	      		alert.setTitle(records4.get(position)); 
		      
	      		if(flag==1)
	      			alert.setIcon(R.drawable.play);
	      		if(flag==2)
	      			alert.setIcon(R.drawable.video);
	      		if(flag==3)
	      			alert.setIcon(R.drawable.pic);
	
	      		final String filename2 ="/mnt/sdcard/favorites"+"/"+records4.get(position); 
	      		//This allows a particular for the user to be send to the server
	      		alert.setPositiveButton("Send to Server", new DialogInterface.OnClickListener() {
		 			
		 			
		 			public void onClick(DialogInterface dialog, int whichButton) {
					
						//	Toast.makeText(getApplicationContext(), filename2, Toast.LENGTH_SHORT).show();
						if(MainViewActivity.valueOfEmail!=null){
						
								
							progressBar = new ProgressDialog(MusicActivity.this);
							progressBar.setCancelable(true);
							progressBar.setMessage("Sending " +records4.get(position)+" to Server...");
							progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
							progressBar.setProgress(0);
							progressBar.show();
							  
									
							new Thread(new Runnable() {
								public void run() {
									
								
								
									try { 
							 
								
										Client1 client =new Client1();
							
										client.sendMessage("STORE"+","+MainViewActivity.valueOfEmail+","+records4.get(position));
										client.storeFile(filename2,records4.get(position));
										client.closeConnection();
										//		 Toast.makeText(getApplicationContext(), "socket created", Toast.LENGTH_SHORT).show();
		 
									} catch (IOException e) {
										// TODO Auto-generated catch block
										Toast.makeText(getApplicationContext(), "socket not created", Toast.LENGTH_SHORT).show();
										e.printStackTrace();
									}
								
									if(Client1.fileSent=="TRUE")
									{
										Client1.fileSent="FALSE";
										progressBarStatus=1;
									}
									
									
									Looper.prepare(); 
								 
									// 		Update the progress bar
									progressBarHandler.post(new Runnable() {
										public void run() {
											// progressBar.setProgress(progressBarStatus);
										}
								  	});
							
									if (progressBarStatus == 1) {
										progressBar.dismiss();
								
									}
								
								  }//end of public void run
						       }).start();//end of runnable
						}//end of if 
						
						else 
							Toast.makeText(getApplicationContext(), "Please Login First", Toast.LENGTH_SHORT).show();
				 	}
			
	      		});
		 		
		 		//To view a file depending upon its type
		 		alert.setNegativeButton("Open",	new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						 //String filename3 ="/mnt/sdcard/favorites"+"/"+records4.get(position);
						Toast.makeText(getApplicationContext(), filename2, Toast.LENGTH_LONG).show();
		   	                
	                    Intent intent = new Intent();  
	                    intent.setAction(android.content.Intent.ACTION_VIEW);  
	                    File file = new File(filename2);  
	                    if(flag==1)
	                    	intent.setDataAndType(Uri.fromFile(file), "audio/*");
	                    if(flag==2)
	                    	intent.setDataAndType(Uri.fromFile(file), "video/*");
	                    if(flag==3)
	                    	intent.setDataAndType(Uri.fromFile(file), "image/*");
		   	                     //   intent.
	                    startActivity(intent);
					}
				});
		 		//Remove the file from the folder
		 		alert.setNeutralButton("REMOVE", new DialogInterface.OnClickListener(){
		        	
		        	public void onClick(DialogInterface dialog, int id) {
		        		
		        		 AlertDialog.Builder alert1 = new AlertDialog.Builder(MusicActivity.this);
		        		 alert1.setTitle(records4.get(position));
		        		 alert1.setMessage("Are you sure you want to delete it?");
		        		
		        		 
	     	 			 alert1.setPositiveButton("YES",new DialogInterface.OnClickListener() {
		 					public void onClick(DialogInterface dialog, int which) {
		 						records4.remove(position);
		 						new File(filename2).delete();
	 		 					callView(records4);
		 				        getListView().setTextFilterEnabled(true);
		 				        Toast.makeText(getApplicationContext(), "File Deleted", Toast.LENGTH_SHORT).show();
		 				        
		 				
		 		 						
		 		      		}
	     	 			 });
		 		 		
		        		 
	
	     	 			 alert1.setNegativeButton("NO",	new DialogInterface.OnClickListener() {
		 					public void onClick(DialogInterface dialog, int which) {
		 						Toast.m	akeText(getApplicationContext(), "CANCELED", Toast.LENGTH_SHORT).show(); 
		 	//write code for deleting the file	 						
		 		 						
	 		 		      	}
	     	 			 });
		 		 		
		        		 
		        		 alert1.show();
		        		 
		           		
		        	}
		 		});
		 		
		 		alert.show();
	      		
	       	}
    	});

    }//end of function OpenFile
	//To view the files to see it in list form
    public void callView(final List<String> records4){
		  

		  setListAdapter(new ArrayAdapter<String>(this, R.layout.music,records4));	  
		 
		  listView = getListView();	
    }
	//Update the list
    public void updateView(List<String> records5){
		  

		  setListAdapter(new ArrayAdapter<String>(this, R.layout.music,records5));
		  
		  listView = getListView();
    }
	  
	  
	  
	//TO see all the files at once
	public void openAll(final List<String> records5)     
	{
		
		
		setListAdapter(new ArrayAdapter<String>(this, R.layout.music,records5));
	  
	 
		ListView listView = getListView();
		//Toast.makeText(getApplicationContext(),ProjectActivity.const1, Toast.LENGTH_SHORT).show();

		listView.setOnItemClickListener(new OnItemClickListener() {
	  
		  	@Override
		  	public void onItemClick(AdapterView<?> parent, View view,final int position, long id) {
		  		
		  		AlertDialog.Builder alert = new AlertDialog.Builder(MusicActivity.this); 
			      
		  		alert.setTitle(records5.get(position)); 
			
			    
		  		if(ProjectActivity.downloads!=1)
		  		{
		  			//Send a the file to particular to other peer
		  			alert.setPositiveButton("Send", new DialogInterface.OnClickListener() {
		 			
		 			
			 			public void onClick(DialogInterface dialog, int whichButton) {
					         filename ="/mnt/sdcard/favorites"+"/"+records5.get(position);
				
						  
					         progressBar = new ProgressDialog(MusicActivity.this);
					         progressBar.setCancelable(true);
					         progressBar.setMessage("Sending " +records5.get(position)+" ...");
					         progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
					         progressBar.setProgress(0);
					         progressBar.show();
						  
								
					         new Thread(new Runnable() {
					        	 public void run() {
					        		 try {
							
					        			 Socket socket1;
					        			 if(ProjectActivity.const1==4)
					        			 {	
									
									 
					        				 socket1 = new Socket(ProjectActivity.ipAddress.getText().toString().trim(), 8080);
							
					        				 //  Toast.makeText(getApplicationContext(), "socket created", Toast.LENGTH_SHORT).show();
					        			 }
					        			 else
					        			 {	 
					        				 ProjectActivity.const1=0;
					        				 String[] temp1=FriendList.temp[2].split("/");	
					        				 socket1 = new Socket(temp1[1], 8080);
									 
					        			 }
																		
					        			 File f1 = new File(filename);
							   
					        			 DataOutputStream outToServer = null;
					        			 InputStream in = new FileInputStream(f1);
					        			 int size = (int) f1.length();
					        			 System.out.println(size);
					        			 byte[] buf = new byte[1024];
					        			 int len;
					        			 ByteBuffer b = ByteBuffer.allocate(4);
					        			 //b.order(ByteOrder.BIG_ENDIAN); // optional, the initial order of a byte buffer is always BIG_ENDIAN.
					        			 b.putInt(size);
					        			 byte[] result = new byte[4];
					        			 result = ByteCopy(b);
					        			 ByteBuffer b1 = ByteBuffer.allocate(4);
					        			 b1.putInt(1);
					        			 byte[] result1 = new byte[4];
					        			 result1 = ByteCopy(b1);
					        			 String sendtoServer = records5.get(position);
					        			 byte[] theStringByte = sendtoServer.getBytes();
					        			 ByteBuffer b2 = ByteBuffer.allocate(4);
					        			 //b.order(ByteOrder.BIG_ENDIAN); // optional, the initial order of a byte buffer is always BIG_ENDIAN.
					        			 b2.putInt(theStringByte.length);
					        			 byte[] result2 = new byte[4];
					        			 result2 = ByteCopy(b2);
					        			 outToServer = new DataOutputStream(socket1.getOutputStream());
					        			 //Send the message type intitally
					        			 outToServer.write(result1,0,4);
					        			 //Send the string length of the name of the file
					        			 outToServer.write(result2,0,4);
					        			 //Send the string name itself
					        			 outToServer.write(theStringByte,0,theStringByte.length);
					        			 //Length of the music file is send to the server
					        			 outToServer.write(result,0,4);
					        			 //The the actual music file in read 1024 bytes at a time and send to the server
					        			 while ((len = in.read(buf)) > 0){
					        				 count++;
					        				 outToServer.write(buf,0,len);
								
					        			 }
						 			  				  
					        			 in.close();
					        			 socket1.close();
					        			 progressBarStatus=1;
					        		 }//end of try 
								  		catch (IOException e) {
									// TODO Auto-generated catch block
							  			e.printStackTrace();
								  	}//end of catch
			 			
					        		 Looper.prepare(); 
								 
					        		 // Update the progress bar
									 progressBarHandler.post(new Runnable() {
										public void run() {
										 // progressBar.setProgress(progressBarStatus);
										}
									 });
								
									 if (progressBarStatus == 1) {
										try {
												
											Thread.sleep(500);
										}
									    catch (InterruptedException e) {
											// TODO Auto-generated catch block
									    	e.printStackTrace();
								    	}
										
										progressBar.dismiss();
										//	Toast.makeText(getApplicationContext(), "File Transfer Complete", Toast.LENGTH_SHORT).show();
									
				 					 }
								
					        	 }//end of public void run
					         }).start();//end of runnable
								  
			 			}
		
		  			});//end of Onclicklistener
						  
						  
		  		}//end of if downlaods=1
			      
		  		else {
		  			  //Move the download file to Favorites folder from the My Downloads
			    	  alert.setPositiveButton("Move to Favorites", new DialogInterface.OnClickListener() {
					
			    		  public void onClick(DialogInterface dialog, int whichButton) {
						    	  
			    			  File file = new File("/mnt/sdcard/My Downloads/"+records5.get(position));
			    			  
						  					// Destination directory
			    			  File dir = new File("mnt/sdcard/favorites");
			    			  
			    			  // Move file to new directory
			    			  boolean success = file.renameTo(new File(dir, records5.get(position)));
			    			  if (!success) {
			    				  // File was not successfully moved
			    				  Toast.makeText(getApplicationContext(), "File Move Unsuccessful", Toast.LENGTH_SHORT).show();
			    			  }
			    			  else 
			    			  {
			    				  file.delete();
			    				  Toast.makeText(getApplicationContext(), "File Move Successful", Toast.LENGTH_SHORT).show();
			    				  records5.remove(position);
			    			  }
			    			  updateView(records5);
						  					     
							}
						});
		  		}//end of else
		  		//To see the files in the format it is saved
		 		alert.setNegativeButton("Open",	new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						String  filename1;
						if(ProjectActivity.downloads==1)
							filename1 ="/mnt/sdcard/My Downloads"+"/"+records5.get(position);
						else 
							filename1 ="/mnt/sdcard/favorites"+"/"+records5.get(position);
				 		          		// Toast.makeText(getApplicationContext(), filename1, Toast.LENGTH_LONG).show();
				 		   	                
		                Intent intent = new Intent();  
		                intent.setAction(android.content.Intent.ACTION_VIEW);  
		                File file = new File(filename1);  
		                String temp=records5.get(position).substring((records5.get(position).lastIndexOf('.') +1));
				 		   	                        
		                if(temp.contains(ext[0])||temp.contains(ext[1])) 
		                	intent.setDataAndType(Uri.fromFile(file), "audio/*");
		                if(temp.contains(ext[2])||temp.contains(ext[3])||temp.contains(ext[4])||temp.contains(ext[5]))
		                	intent.setDataAndType(Uri.fromFile(file), "video/*");
		                if(temp.contains(ext[6])||temp.contains(ext[7])||temp.contains(ext[8])) 
		                	intent.setDataAndType(Uri.fromFile(file), "image/*");
				 		   	                     //   intent.
		                startActivity(intent);
					}
		 		});
	 			
		 		alert.setNeutralButton("Cancel", new DialogInterface.OnClickListener(){
				        	
		        	public void onClick(DialogInterface dialog, int id) {
				        		
		        		Toast.makeText(getApplicationContext(),	"Cancelled", Toast.LENGTH_SHORT).show();
				        		
		        	}
		 		});
			 		
		 		alert.show();
		  		
		  	}
		});
	
	}
//=======================================================================================================



//======================================================================================================
	//Copies the data from ByteBuffer to Byte array
	public static byte[] ByteCopy(ByteBuffer b) {
    	byte[] output = new byte[b.limit()];
    	for (int i = 0; i < b.limit(); i++) {
    		output[i] = b.get(i);
    	}
    	return output;
    }
//=======================================================================================================
   public List<String> checkExtension(int flag){
	   //This is to check for the extension of the file like jpg, mp3 and mp4
	   String temp1=null;
	   List<String> records1 = new ArrayList<String>();
	   for (int i=0;i<names.length;i++){
      	 
       	// temp=names[i].split(".");
     
		   temp1=names[i].substring((names[i].lastIndexOf('.') +1));
       	
		   records.add(temp1);
		   if(flag==1){
			   if(records.get(i).contains(ext[0])||records.get(i).contains(ext[1]))
       	     	 records1.add(names[i]);
       	           		 
   	    	}
	   
       	 	else if(flag==2){
   	 			if(records.get(i).contains(ext[2])||records.get(i).contains(ext[3])
         			 ||records.get(i).contains(ext[4])||records.get(i).contains(ext[5]))
         	 
   	 				records1.add(names[i]);
         	           		 
     	    }
	   
       	 	else	if(flag==3){
       	 		if(records.get(i).contains(ext[6])||records.get(i).contains(ext[7])||records.get(i).contains(ext[8]))
       	 
       	 			records1.add(names[i]);
       	           		 
       	    }
	   
       	 
       	 
       	 
	   }   //end of for
      
      
	   return records1  ;   
      
   }   
      
      
      
}
