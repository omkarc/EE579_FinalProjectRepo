package edu.usc.ee579.group4;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ParseException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;



//import edu.usc.ee579.group4.MainViewActivity;

import edu.usc.ee579.group4.Client1;


/*
 * This Activiy opens up when the user have register itself with the Centralized Server
 * And has Logged In in online mode or invisble mode
 */
public class ProjectActivity extends ListActivity {
	private LayoutInflater mInflater;
	private Vector<RowData> data;
	static List<String> records = new ArrayList<String>();
	RowData rd;
	CustomAdapter adapter;
	String  Name1;
	String Name2;
	String Name3;
	static EditText ipAddress=null;
	static int RequestFromServer =0;
	int pos=1;static int flag=0;static int const1=0,const2=0;
	static String valueOfEmail=null;
	private Pattern pattern;
	private Matcher matcher;
	static int downloads=0;
	
	private static final String IPADDRESS_PATTERN = 
		"^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
		"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
		"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
		"([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
	
	//This is the list which shows up in this activity
	String[] fields = new String[] {
		"Friends Online",
		"My Favourites",
		"Request a File",
		"Search Online",
		"Wi-Fi Direct",
		"Add Friends",
		"My Downloads",
		"Log Off",
	};
	//This is description of the each of the fields
	static final String[] detail = new String[] {
		"Look Who's Online",  
		"Share your favourites",
		"Get what you want  ",
		"Search for an item Online",
		"Connect with your peers directly",
		"Gr(',')w your fRIEND Circle",
		"Shows all your downloads",
		"Log Off",
	};
	Button buttonStart;
	private Integer[] imgid = {   
			
		R.drawable.ff,
		R.drawable.fav1,
		R.drawable.req,	
		R.drawable.search,
		R.drawable.rss,
		//	R.drawable.key,
		R.drawable.add,
		R.drawable.activities,
		R.drawable.logoff,
	
    };
	RelativeLayout layr1;
	//Animation ar3;
	
	ProgressDialog progressBar;
	private int progressBarStatus = 0;
	private Handler progressBarHandler = new Handler();
	
	//This is first function called when this activity opens
	@Override
    public void onCreate(Bundle savedInstanceState) {
	      
			
	//	Bundle b = this.getIntent().getExtras();
		
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        mInflater = (LayoutInflater) getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        data = new Vector<RowData>();
        
	       
	        
	        
        for(int i=0;i<fields.length;i++){
        	try {
        		rd = new RowData(i,fields[i],detail[i]);
        	} catch (ParseException e) {
        		e.printStackTrace();
        	}
         	data.add(rd);
        }//end of for loop
	        
        adapter = new CustomAdapter(this, R.layout.list,R.id.title, data);
        setListAdapter(adapter);
        //  getListView().setBackgroundColor(R.drawable.wall);
        getListView().setBackgroundResource(R.drawable.wall1);
	      //  getListView().setTextFilterEnabled(true);
	}//end of onCreate
	    
	
	   
    public void onListItemClick(ListView parent, View v, int position, long id) { 
    	adapter = (CustomAdapter) parent.getAdapter();
//=================================================================================================================
    	if(position==0){
    		//To find friends online depending on the option selected
    		//Like It own gang, particular useror everyone of them
    		AlertDialog.Builder alert = new AlertDialog.Builder(this);//alertDialog is a subclass of dialog that can display 1/2/3 buttons
		
    		LinearLayout search= new LinearLayout(this);
	
	
    		search.setOrientation(1); //1 is for vertical orientation
			
    		final EditText input1 = new EditText(this);
    		final EditText input2 = new EditText(this);
			final EditText input3 = new EditText(this);
		
			search.addView(input1);
			search.addView(input2);
			search.addView(input3);
			alert.setView(search);
		 
			alert.setTitle("Look Who's Online");
			alert.setMessage("Enter upto 3 Names of your friends");
			//alert.setIcon(R.drawable.ic_launcher);
			alert.setPositiveButton("Search by Name",new DialogInterface.OnClickListener() {
	
				//Search if a particular user is online or not
				public void onClick(DialogInterface dialog,int whichButton) {
							
					  Name1 =    input1.getText().toString();
			          Name2 =    input2.getText().toString();
		              Name3 =    input3.getText().toString();
		              if(!((Name1.length())>0))
		            	  Name1="*";
		              if(!((Name2.length())>0))
		            	  Name2="*";
		              if(!((Name3.length())>0))
		            	  Name3="*";
		              callProgressBar(1);
		    	              
		    	           
				}
			});
	
			alert.setNegativeButton("Show My Gang",new DialogInterface.OnClickListener() {
				//Search if the members of the gang are online
				public void onClick(DialogInterface dialog, int which) {
				
					callProgressBar(2);
			
				}
			});
			
			alert.setNeutralButton("Show Me All", new DialogInterface.OnClickListener(){
	        	//To search all the people who are online
	        	public void onClick(DialogInterface dialog, int id) {
	        		
	        		callProgressBar(3);
	        		//searchFriends(0,1,null,null,null);
	        		
	        	}
		        	
		        	
		        	
			});
		
			alert.show();
	}

//==================================================================================================================
	if(position==1){
		//To look your files which are shared and send it to the server and also to lock your account
		Intent intentMain = new Intent(ProjectActivity.this , AccessActivity.class);
	    ProjectActivity.this.startActivity(intentMain);
	
	}//end of position==1
	
	//==================================================================================================================
	if(position==2){
	
		//This is to get a file either from the server or from other peer
		final AlertDialog.Builder alert = new AlertDialog.Builder(this);//alertDialog is a subclass of dialog that can display 1/2/3 buttons
		
		alert.setTitle("Get your file Now!");
		//alert.setMessage("");
		
		alert.setIcon(R.drawable.req1);
		
		alert.setPositiveButton("Request Peers",new DialogInterface.OnClickListener() {
			//Search a file on other people shared list
			public void onClick(DialogInterface dialog,int whichButton) {

				 AlertDialog.Builder alert1 = new AlertDialog.Builder(ProjectActivity.this);
        		 alert1.setTitle("Enter your Query");
        		 alert1.setIcon(R.drawable.search);
        		 final EditText input = new EditText(ProjectActivity.this);        	
        		 alert1.setView(input);
        		 
	 			 alert1.setPositiveButton("Search Now",	new DialogInterface.OnClickListener() {
	 					public void onClick(DialogInterface dialog, int which) {
	 						Client1 client;
	 						try {
	 							client = new Client1();
	 							String Query =    input.getText().toString();
	 							
	 							client.sendMessage("QUERY::"+","+Query);
	 					//		Toast.makeText(getApplicationContext(), "hello",Toast.LENGTH_SHORT).show();
	 						    client.closeConnection();
	 							
	 											
	 						
	 						} catch (IOException e) {
	 							// TODO Auto-generated catch block
	 							e.printStackTrace();
	 						}
	 						
	 						const2=1;
	 						Intent intentMain = new Intent(ProjectActivity.this , FriendList.class);
	 			    	    ProjectActivity.this.startActivity(intentMain);
	 						
	 										
	 		      		}
	 				});
		 		 		
		        		 
	
	 		 		alert1.setNegativeButton("Cancel",
	 		 				new DialogInterface.OnClickListener() {
	 		 					public void onClick(DialogInterface dialog, int which) {
	 		 						Toast.makeText(getApplicationContext(), "CANCELED", Toast.LENGTH_SHORT).show(); 
	 	//write code for deleting the file	 						
	 		 						
	 		 		      	}
	 				});
		 		 		
		        		 
	 		 		alert1.show();
		     				
				}
			});
		
			//Download a particular file from the server of a users
			alert.setNegativeButton("Download from Server",	new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
						
						
						
				LayoutInflater factory = LayoutInflater.from(ProjectActivity.this);            
		        final View textEntryView = factory.inflate(R.layout.login,null);

		        AlertDialog.Builder alert = new AlertDialog.Builder(ProjectActivity.this); 
		       
		        alert.setTitle("Download from Server"); 
		        alert.setMessage("Enter your Friend's Email and Password"); 
		        alert.setIcon(R.drawable.req1);
		        // Set an EditText view to get user input  
		        alert.setView(textEntryView); 
		        
		     
		        final EditText input1 = (EditText) textEntryView.findViewById(R.id.username);
		        final EditText input2 = (EditText) textEntryView.findViewById(R.id.password);
		        //final EditText input3 = (EditText) textEntryView.findViewById(R.id.work_offline);
		        
		        alert.setPositiveButton("Download Now", new DialogInterface.OnClickListener() { 
		        	public void onClick(DialogInterface dialog, int whichButton) {
		        	
		         
		        		valueOfEmail =    input1.getText().toString();
		        		final String valueOfPassword = input2.getText().toString();
		        
		        		progressBar = new ProgressDialog(ProjectActivity.this);
		        		progressBar.setCancelable(true);
		        		progressBar.setMessage("Requesting Server...");
		        		progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		        		progressBar.setProgress(0);
		        		progressBar.show();
			  
					
		        		new Thread(new Runnable() {
		        			public void run() {
		        				try {
										Client1 client2 =new Client1();
										client2.sendMessage("SEARCH"+","+valueOfEmail+","+valueOfPassword);
										client2.closeConnection();
									} catch (IOException e) {
										// 	TODO Auto-generated catch block
										e.printStackTrace();
									}
		        				if(Client1.Confirm2=="TRUE")
		        				{
		        					Client1.Confirm2="FALSE";
		        					progressBarStatus=1;
		        				}
								
								
		        				Looper.prepare(); 
							 
		        				// Update the progress bar
		        				progressBarHandler.post(new Runnable() {
		        					public void run() {
		        						// progressBar.setProgress(progressBarStatus);
		        					}
		        				});
						
			 
							// ok, file is downloaded,
		        				if (progressBarStatus == 1) {
			 
							
		        					progressBar.dismiss();
		        					Toast.makeText(getApplicationContext(),"List Updated", Toast.LENGTH_LONG).show();
								//flag=1;
		        					RequestFromServer=1;
		        					Intent intentMain = new Intent(ProjectActivity.this ,GetFileActivity.class);
		        					ProjectActivity.this.startActivity(intentMain);
						        
								
								
		        				}//end of if
		        			}//end of public void run
		        		}).start();//end of runnable
			        }  
		        }); 
	
		        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() { 
		          public void onClick(DialogInterface dialog, int whichButton) { 
		            // Canceled. 
		          
		          } 
		        }); 
	
		        alert.show(); 
			} 
		});
		
		alert.setNeutralButton("Cancel",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Toast.makeText(getApplicationContext(),"Cancelled", Toast.LENGTH_SHORT).show();

			} 
		});
	
		
	
	
		alert.show();
	}
//===================================================================================================================
	if(position==3){
		//final Button buttonStart = (Button)findViewById(R.id.buttonGoogle); 
		//Search online for a file via GOOGLE
		AlertDialog.Builder alert = new AlertDialog.Builder(this);//alertDialog is a subclass of dialog that can display 1/2/3 buttons
		final EditText input = new EditText(this);
		alert.setTitle("ENTER YOUR QUERY");
		//alert.setTitle(" Initialize Object");
		
		alert.setIcon(R.drawable.search);
		alert.setView(input);
		alert.setPositiveButton("Google it",
		new DialogInterface.OnClickListener() {
	
			public void onClick(DialogInterface dialog,int whichButton) {
	
						
				if (input.getText().toString().trim().equals("")) {
					Toast.makeText(getApplicationContext(),
							"No URL entered", Toast.LENGTH_SHORT)
							.show();
				} else {

					Intent search = new Intent(Intent.ACTION_WEB_SEARCH);  
					String valueOfQuery =    input.getText().toString();
					search.putExtra(SearchManager.QUERY,valueOfQuery );  
					startActivity(search); 
				}
			}
		});
		
		
		alert.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Toast.makeText(getApplicationContext(),
						"Cancelled", Toast.LENGTH_SHORT)
						.show();

			} 
		});
		
		alert.setNeutralButton(" Open Browser", new DialogInterface.OnClickListener(){
	     	
	     	public void onClick(DialogInterface dialog, int id) {
	     		
	     
	     		String url = "http://www.google.com";
	     		Intent i = new Intent(Intent.ACTION_VIEW);
	     		i.setData(Uri.parse(url));
	     		startActivity(i);
	     	}
	     });
		
		
		alert.show();	
	
	}
//=========================================================================================================================
	if(position==4)
	{
		//Connect to a peer directly without the help of the server if you know the IP address of other peer
		 AlertDialog.Builder alert1 = new AlertDialog.Builder(ProjectActivity.this);
		 ipAddress = new EditText(this);
		 alert1.setTitle("Enter Ip Address of Your Friend");
		 alert1.setMessage("Connect Instantly with your friends ");
		 alert1.setView(ipAddress);
		 
		 alert1.setPositiveButton("Confirm",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					
					
					pattern = Pattern.compile(IPADDRESS_PATTERN);
					 matcher = pattern.matcher(ProjectActivity.ipAddress.getText().toString().trim());
					 
					if(matcher.matches()){
					
						const1=4;downloads=0;
						Toast.makeText(getApplicationContext(),ipAddress.getText().toString().trim(), Toast.LENGTH_SHORT).show();//}
						
						Intent intentMain = new Intent(ProjectActivity.this , MusicActivity.class);
						ProjectActivity.this.startActivity(intentMain);
					}
				   		
					else 
						Toast.makeText(getApplicationContext(), "Incorrect Ip Address", Toast.LENGTH_SHORT).show();		
					
				}
		 });
			
		 
	
		 alert1.setNeutralButton("My IP Address",	new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			    try {
			        for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
			            NetworkInterface intf = en.nextElement();
			            for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
			                InetAddress inetAddress = enumIpAddr.nextElement();
			                if (!inetAddress.isLoopbackAddress()) {
			                   
			                    Toast.makeText(getApplicationContext(),inetAddress.getHostAddress().toString(), Toast.LENGTH_SHORT).show();//}
			                }
			             //   else Toast.makeText(getApplicationContext(), "Wi-Fi Not Available ", Toast.LENGTH_SHORT).show();
			            }
			        }
			    } catch (SocketException ex) {
			    	Toast.makeText(getApplicationContext(), "ERROR", Toast.LENGTH_SHORT).show();
			    	
			    }


			
										
							
	      	}
		 });
			
	
		alert1.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				 Toast.makeText(getApplicationContext(), "Canceled", Toast.LENGTH_SHORT).show();
			}
		});
			
			
		 
		alert1.show();
		 
	}
	
//===========================================================================================================================
	
	if(position==5){
		//Add friends to form your own gang of members or to remove some members
		
		AlertDialog.Builder alert = new AlertDialog.Builder(this);//alertDialog is a subclass of dialog that can display 1/2/3 buttons
		
		LayoutInflater factory = LayoutInflater.from(this);            
	    final View textEntryView = factory.inflate(R.layout.search,null);
	
	    //  AlertDialog loginPrompt = alert.create();
	    
	    final EditText input1 = (EditText) textEntryView.findViewById(R.id.name);
	    final EditText input2 = (EditText) textEntryView.findViewById(R.id.email); 
	     
	    alert.setTitle("My Gang"); 
	    //alert.setMessage("Enter Your Email and Password"); 
	    alert.setIcon(R.drawable.gang);
	    // Set an EditText view to get user input  
        alert.setView(textEntryView); 
        alert.setPositiveButton("Add Friend", new DialogInterface.OnClickListener() { 
        	public void onClick(DialogInterface dialog, int whichButton) {
	        	
	        	 String valueOfName = input1.getText().toString();
	        	 String valueOfEmail = input2.getText().toString();
	        	  try {
					if(!valueOfName.equals("") && !valueOfEmail.equals(""))
	        		  addFriends(valueOfName,valueOfEmail,1,0);
	        	  } catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
	        	  }
	        }
	    });
	    alert.setNegativeButton("Show Friends", new DialogInterface.OnClickListener() { 
	        public void onClick(DialogInterface dialog, int whichButton) { 
	
	        	try {
					addFriends(null,null,0,0);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        	// Canceled. 
	        } 
	    });
	    
	    alert.setNeutralButton("Cancel", new DialogInterface.OnClickListener() { 
	        public void onClick(DialogInterface dialog, int whichButton) { 
	
	        	//addFriends(null,null);
	        	// Canceled. 
	        } 
	    }); 
	
	    
	    
	
	    alert.show(); 
		
	}//end of if(position==6)
//===========================================================================================================================
	if(position==6){
		//Check for which files have been downloaded by me
		downloads=1;
		
		Intent intentMain = new Intent(ProjectActivity.this ,MusicActivity.class);
		ProjectActivity.this.startActivity(intentMain);
	}
	
//===========================================================================================================================	
	if(position==7){
		//To log yourself off from the centralized server if you are going offline
		if(MainViewActivity.LOGGED=="LOGGED")
		{
		
			MainViewActivity.LOGGED="FALSE";
			try {
				Client1 client = new Client1();
				
				
				client.sendMessage("LOG OFF"+","+MainViewActivity.valueOfEmail);
				MainViewActivity.LOGGED="FALSE";
				Toast.makeText(getApplicationContext(),	"Signed Out", Toast.LENGTH_SHORT).show();
			}
		
		
			catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			}
		}
		
		else 
			Toast.makeText(getApplicationContext(),"You are already Logged Off", Toast.LENGTH_SHORT).show();
		
	
	}
//===================================================================================================================
//======================================================================================================================================
	
	
	
	
	
	
//======================================================================================================================================    
   }//end of onListItemClick
    //==============================================================================================================  

	  //  ===============================================================================================================
    
    
    //Search for friends depending of the three different values entered into the file
    public int searchFriends(int i,int ii, String Name1,String Name2,String Name3){
	    int flag=0;
	    	      	  
	    try {
	    	// Client1 getListOfFriends = new Client1("192.168.0.14",9777);//(Client1)getIntent().getSerializableExtra("abcd");
	    	Client1 getListOfFriends = new Client1();
			if(ii==1)
			{
	    	 getListOfFriends.sendMessage("GET LIST OF FRIENDS");
			}
			else if(i==1){
				getListOfFriends.sendMessage("REQUESTED "+","+Name1+","+Name2+","+Name3);
			}
		//	Toast.makeText(getApplicationContext(),"List Updated", Toast.LENGTH_LONG).show();
			flag=1;
		    Log.i("Content "," Main layout ");
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    return flag;
    }//end of searchFriends()
//========================================================================================================
    
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	  super.onConfigurationChanged(newConfig);
	  setContentView(R.layout.main);
	}
	
//========================================================================================================
	//To show to the user that the requseted operation is been performed
	public  void callProgressBar(final int count){
	    		
		 progressBar = new ProgressDialog(ProjectActivity.this);
		 progressBar.setCancelable(true);
		 progressBar.setMessage("Requesting Server...");
		 progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		 progressBar.setProgress(0);
		//	progressBar.setMax(100);
		 progressBar.show();
	   
			
				  
		 new Thread(new Runnable() {
			 public void run() {
				 //	while (progressBarStatus < 100) {
	 
				 // process some tasks
				 if(count==1)
					 progressBarStatus= searchFriends(1,0,Name1,Name2,Name3);
				 if(count==2)
					 try {
						 progressBarStatus= addFriends(null,null,0,1);
					 } catch (IOException e) {
							// TODO Auto-generated catch block
						 e.printStackTrace();
					 }
				 if(count==3)
					 progressBarStatus=searchFriends(0,1,null,null,null);
					
					
				 Looper.prepare(); 
				 
				  // Update the progress bar
		 		  progressBarHandler.post(new Runnable() {
		 			  public void run() {
		 				  // progressBar.setProgress(progressBarStatus);
		 			  }
				  });
			//	}
	 
					// ok, file is downloaded,
		 		  if (progressBarStatus == 1) {
	 
						// sleep 2 seconds, so that you can see the 100%
					
	 
						// close the progress bar dialog
					progressBar.dismiss();
					Toast.makeText(getApplicationContext(),"List Updated", Toast.LENGTH_LONG).show();
					//flag=1;
					const2=0;
					Intent intentMain = new Intent(ProjectActivity.this , FriendList.class);
			    	ProjectActivity.this.startActivity(intentMain);
			        
					
					
		 		  }//end of if
			 }//end of public void run
		 }).start();//end of runnable
				 
	}//end of callProgressBar
				
	    	
//================================================================================================================	    	
	    	
    	
	//To add friends to the list of gang members
	public int addFriends(String valueOfName,String valueOfEmail,int action,int action2) throws IOException{
	
		int flag=0;
		if(records.size()!=0)
			records.clear();

		File f = new File("sdcard/MyFriends/MyFriends.txt");
			if (f.exists()) {
		
			if(action==0||action2==1){
			
			
			
			BufferedReader reader = null;
	
			reader = new BufferedReader(new FileReader("/sdcard/MyFriends/MyFriends.txt"));
	    	String line;
		
			int i=-1;
			while ((line = reader.readLine()) != null)
			{
				
				//  Toast.makeText(getApplicationContext(), line, Toast.LENGTH_SHORT).show();
					
				i=i+1;
                records.add(line);
                        
					
			}
				
			reader.close();
			if(action==0 && action2!=1){
				flag=1;
				Intent intentMain = new Intent(ProjectActivity.this , AddedFriends.class);
	    	    ProjectActivity.this.startActivity(intentMain);}
			
			  
				if(action2==1){
				
					Client1 getOnlineFriends = new Client1();
				
		
					for(i=0;i<records.size();i++){
						String[] temp= records.get(i).split(",");
		
			     
						getOnlineFriends.sendMessage("GANG"+","+temp[0]+","+temp[1]);
						try {
						
							Thread.sleep(2000);
						} catch (InterruptedException e) {
		
						  e.printStackTrace();
						}
					
			        
			        
						temp=null;
					}	//end of for
			
					getOnlineFriends.sendMessage("END");
					try {
								
			        	Thread.sleep(500);
		        	} catch (InterruptedException e) {
								// TODO Auto-generated catch block
		        		e.printStackTrace();
	        		}
					flag=1;const2=0;    					
					Intent intentMain = new Intent(ProjectActivity.this , FriendList.class);
			    	ProjectActivity.this.startActivity(intentMain);
	    					
	    					
	    					
	    					
				}//end of if (action2==1)
		
		
			}//end of action==0 || action2==0

			if(action==1){
		
			
				FileOutputStream fos1 = new FileOutputStream("/sdcard/MyFriends/MyFriends.txt",true);
				PrintStream ps1 = new PrintStream(fos1);
				ps1.println(valueOfName+","+valueOfEmail);
				ps1.close();
		        Toast.makeText(getApplicationContext(),"FRIEND ADDED", Toast.LENGTH_SHORT).show();
		
			}
	
	  				
		
		
		
		}//end of if(f.exists)
		else if(!f.exists() && action==1){
			File ff = new File("sdcard/MyFriends/");
			ff.mkdirs();
		
			
			FileOutputStream fos1 = new FileOutputStream("/sdcard/MyFriends/MyFriends.txt",true);
			PrintStream ps1 = new PrintStream(fos1);
			ps1.println(valueOfName+","+valueOfEmail);
			ps1.close();
			Toast.makeText(getApplicationContext(),"FRIEND ADDED", Toast.LENGTH_SHORT).show();
		}
		

	 
			return flag; 
	//}//end of else
	
    }//end of addFriends() 
	    	
//============================================================================================================
	    
	//Form the row detials of the list view
	private class RowData {
	        
		protected int mId;
        protected String mTitle;
        protected String mDetail;
        RowData(int id,String title, String detail){
		    mId=id;
		    mTitle = title;
		    mDetail= detail;
        }
        @Override
        public String toString() {
        	return mId+" "+mTitle+" "+mDetail;
        }
	}
//============================================================================================================
	//Form the List view with the details in it 
	private class CustomAdapter extends ArrayAdapter<RowData> {
        public CustomAdapter(Context context, int resource,
                        int textViewResourceId, List<RowData> objects) {
                super(context, resource, textViewResourceId, objects);
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            TextView title = null;
            TextView detail = null;
            ImageView i11=null;
            
            RowData rowData= getItem(position);
            if(null == convertView){                     
            	    convertView = mInflater.inflate(R.layout.mainlist, null);
                    holder = new ViewHolder(convertView);
                    convertView.setTag(holder);                   
            }
            holder = (ViewHolder) convertView.getTag();    
                            
            title = holder.gettitle();     
            title.setText(rowData.mTitle);
            detail = holder.getdetail();
            detail.setText(rowData.mDetail); 
            i11=holder.getImage();
            i11.setImageResource(imgid[rowData.mId]);
            return convertView;
        }
        private class ViewHolder {      
            private View mRow;
            private TextView title = null;
            private TextView detail = null;
            private ImageView i11=null;            
            
            public ViewHolder(View row) {
            	mRow = row;
            }
            public TextView gettitle() {
                if(null == title){
                    title = (TextView) mRow.findViewById(R.id.title);
                }
                return title;
            }
                     

            public TextView getdetail() {
            	if(null == detail){
            		detail = (TextView) mRow.findViewById(R.id.detail);
            	}
            	return detail;
            }
                
                
            public ImageView getImage() {
            	if(null == i11){
            		i11 = (ImageView) mRow.findViewById(R.id.img);                        
                }
                return i11;
            }
	             
	    }
    }
}