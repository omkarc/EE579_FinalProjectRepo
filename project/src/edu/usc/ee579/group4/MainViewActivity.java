package edu.usc.ee579.group4;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.ParseException;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import android.widget.ArrayAdapter;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
//import edu.usc.anrg.a3final.R;

/*
 * This is the Main Screen that appears before the user when the application starts
*/
public class MainViewActivity extends ListActivity {
	public static Client1 client;
	private LayoutInflater mInflater;
	private Vector<RowData> data;
	RowData rd;
	CustomAdapter adapter;
	int pos=1,flag=0,flag1=1;
	static int alreadyLoggedIn=0;
	static String valueOfEmail;
	//The list view shows the following three options
	String[] fields = new String[] {
		"Register",
		"Go L!ve",
		"!nVis!ble"
	};
	static String LOGGED="FALSE";
	String LoginIp, currentIp;
	//The list view shows the following description of each option
	static final String[] detail = new String[] {
		"Click to Register your App",
		"Logon to Server@Euphony",
		"Work Offline",
	};
	
	private Integer[] imgid = {   
		//	R.drawable.register,
		//	R.drawable.server,
			R.drawable.pathing,
			R.drawable.music12,
			R.drawable.bbb,
    };
	RelativeLayout layr1;
	//Animation ar3;
	private ResponseReceiver receiver;
	
	//This is the first portion which is called first when the application starts
	@Override
    public void onCreate(Bundle savedInstanceState) {
	        
		IntentFilter filter = new IntentFilter(ResponseReceiver.ACTION_RESP);
	    filter.addCategory(Intent.CATEGORY_DEFAULT);
	    receiver = new ResponseReceiver();
	    registerReceiver(receiver, filter);
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
	     
	        
	}//end of onCreate
	    
	
	 
	    
	public void onListItemClick(ListView parent, View v, int position, long id) { 
		adapter = (CustomAdapter) parent.getAdapter();
//======================================================================================================
		
		if(position==0)
		{
			//When we click on Register the its asks user details or tells that you are already registered
			if(flag==0)
				register();
			else 
				Toast.makeText(getApplicationContext(), "User already Registered", Toast.LENGTH_SHORT).show();
		}
//======================================================================================================	
		if(position==1){
			//When login is clicked it asks to login the application when the registration details
			if(LOGGED=="FALSE")
			{
				login();
				LoginIp=getIpAddress();
			}
		
			else if (!(currentIp=getIpAddress()).equals(null) && !currentIp.equals(LoginIp))
			{
				//If the Ip address changes the user has to login again
			
				Toast.makeText(getApplicationContext(), "You have a new Ip and need to Login Again", Toast.LENGTH_SHORT).show();
				login();
	
			} 
			else
			{
				flag=0;
				Toast.makeText(getApplicationContext(), "You are already Online", Toast.LENGTH_SHORT).show();
				callMainView();
			}
		
			//When we login in the application the server and hello message service starts in background
			Intent msgIntent = new Intent(this, SimpleIntentService.class);
			startService(msgIntent);
			Intent receiveIntent = new Intent(this, ReceiverService.class);
			startService(receiveIntent);
	
		}
//======================================================================================================	   
		if(position==2){
			//When we click on invisible mode we enter the application to just to use it
			//Intent receiveIntent = new Intent(this, ReceiverService.class);
			//startService(receiveIntent);
			Intent msgIntent = new Intent(this, SimpleIntentService.class);
			startService(msgIntent);
			
			callMainView();
		 
		}
		 
    }//end of onItemClickListener
//=====================================================================================================================================
	
	//This functionality is use to get the IP address that phone has acquired in the private network
    public String getIpAddress(){
	    	
	    	
    	 try {
    	        for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
    	        	NetworkInterface intf = en.nextElement();
    	        	for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
    	                InetAddress inetAddress = enumIpAddr.nextElement();
    	                if (!inetAddress.isLoopbackAddress()) {
    	                    return inetAddress.getHostAddress().toString();
    	                }
    	        	}
    	         }
	     } catch (SocketException ex) {
	    	         
	     }
	
	     return null;
    }
//=====================================================================================================================================
	    
    //This functionality helps user to fill out its details like name, email Id and password to register
    //itself to the Centralized Server
    public void register(){	
    	final Pattern p = Pattern.compile(
    			"[a-zA-Z0-9+._%-+]{1,256}" +
    					"@" +
				"[a-zA-Z0-9][a-zA-Z0-9-]{0,64}" +
						"(" +
						"." +
				"[a-zA-Z0-9][a-zA-Z0-9-]{0,25}" +
						")+"
		);
	    		 
    	LayoutInflater factory = LayoutInflater.from(this);            
    	final View textEntryView = factory.inflate(R.layout.register,null);
	
    	AlertDialog.Builder alert = new AlertDialog.Builder(this); 
	        
    	alert.setTitle("Registration Details"); 
        // alert.setMessage("Enter Your Name, Email Address and Password"); 
    	alert.setIcon(R.drawable.user);
    	// Set an EditText view to get user input  
    	alert.setView(textEntryView); 
	    
	
    	final EditText input1 = (EditText) textEntryView.findViewById(R.id.name);
    	final EditText input2 = (EditText) textEntryView.findViewById(R.id.email);
    	final EditText input3 = (EditText) textEntryView.findViewById(R.id.password);
    	final EditText input4 = (EditText) textEntryView.findViewById(R.id.password1);
	
    	alert.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
	    			
    		//After entering the details and hitting confirm this information is send to the Server
			public void onClick(DialogInterface dialog, int whichButton) {
			//String Email = input.getText().toString().trim();
		        String valueOfName =    input1.getText().toString();
		        valueOfEmail =    input2.getText().toString();
	            String valueOfPassword = input3.getText().toString();
	            String valueOfPassword1 = input4.getText().toString();
		    			 
				Matcher m = p.matcher(valueOfEmail);
		
			    if(m.matches() && valueOfName.length()>0 && valueOfPassword.length()>0 && valueOfPassword.equals(valueOfPassword1)) {
		    		    	
			    	try{
		    		    
			    		client = new Client1();
		    		     
	    				client.sendMessage("REGISTER"+","+valueOfName+","+valueOfEmail+","+valueOfPassword);
		    			
	    				File downloads = new File(Environment.getExternalStorageDirectory() + "/My Downloads");
	    				if(!downloads.exists())
	    				{
	                        downloads.mkdir(); //directory is created;
		
	                    }
		                    	
	                   File favorites = new File(Environment.getExternalStorageDirectory() + "/favorites");
	                   if(!favorites.exists())
	                   {
	                	   favorites.mkdir(); //directory is created;
		
	                   } 	
	                   Toast.makeText(getApplicationContext(),"Registration Complete", Toast.LENGTH_SHORT).show();
	                   client.closeConnection();
	                   flag =1;  
			    	}
		    		      
			    	catch (IOException e) {
			    	  // TODO Auto-generated catch block
			    		Toast.makeText(getApplicationContext(),"client could not connect",Toast.LENGTH_SHORT).show();
			    		e.printStackTrace();
			    	}
		
		    		
			    }	//end of if m.matches
		    		    
			    else {
			    	//If some fields are missed out the user is asked to enter the details again 
			    	if(valueOfName.length()==0)
			    		Toast.makeText(getApplicationContext(),"Please enter Name", Toast.LENGTH_SHORT).show();
			    	else if(!m.matches())
			    		Toast.makeText(getApplicationContext(),"Invalid Email Addresss", Toast.LENGTH_SHORT).show();
			    	else 
			    		Toast.makeText(getApplicationContext(),"Passwords Don't Match", Toast.LENGTH_SHORT).show();
			    	
			    	
			 		register();
			    }
			}
    	});
	    		
    	//If you dont want to send any data we can click cancel and go back to previous menu
		alert.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Toast.makeText(getApplicationContext(),"Cancelled", Toast.LENGTH_SHORT).show();
			}
		});
	    		
	    		
	    		
		alert.show();
	
    }
	    	    
	//This functionality allows user to logon to show other user that he is online for sharing of files
    public void login(){
	    	
    	LayoutInflater factory = LayoutInflater.from(this);            
        final View textEntryView = factory.inflate(R.layout.login,null);

        AlertDialog.Builder alert = new AlertDialog.Builder(this); 
       
        alert.setTitle("Login Details"); 
        alert.setMessage("Enter Your Email and Password"); 
        alert.setIcon(R.drawable.music12);
        // Set an EditText view to get user input  
        alert.setView(textEntryView); 
        
     
        final EditText input1 = (EditText) textEntryView.findViewById(R.id.username);
        final EditText input2 = (EditText) textEntryView.findViewById(R.id.password);
        //final EditText input3 = (EditText) textEntryView.findViewById(R.id.work_offline);
        
        alert.setPositiveButton("Login", new DialogInterface.OnClickListener() { 
	        public void onClick(DialogInterface dialog, int whichButton) {
	        	
	        	//After clicking on Login the details are send to the server where they are matched
	        	//To check if the give information matches the registration info.
		        valueOfEmail =    input1.getText().toString();
		        String valueOfPassword = input2.getText().toString();
		        if(valueOfEmail.length()>0 && valueOfPassword.length()>0){
		        	try {
		    		
		    			Client1	client = new Client1();
		    			
		    			
		    			client.sendLoginDetails("LOGIN"+","+valueOfEmail+","+valueOfPassword);
		    			if (client.Confirm =="FALSE"){
		    				Toast.makeText(getApplicationContext(),"Incorrect Login Details:LOGIN AGAIN",Toast.LENGTH_SHORT).show();
		    				client.closeConnection();
		    				login();
		    			}
		    			else { 
		    				LOGGED="LOGGED";   
		    				//alreadyLoggedIn=1;
		    				Toast.makeText(getApplicationContext(),"Welcome to Euphony",Toast.LENGTH_SHORT).show();
		    			
		    				callMainView();
		    		       			
		    			}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						Toast.makeText(getApplicationContext(),"client could not connect",Toast.LENGTH_SHORT).show();
						e.printStackTrace();
					}
	            }
	            else 
	            	Toast.makeText(getApplicationContext(), "Incorrect Details", Toast.LENGTH_SHORT).show();
	            
            

	        } 
        }); 

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() { 
          public void onClick(DialogInterface dialog, int whichButton) { 
            // Canceled. 
          } 
        }); 

        alert.show(); 
        

    }
//===========================================================================================================
    //This functionality calls the main activity when user logs in or go in invisible mode
    public void callMainView(){
 
    	Intent intentMain = new Intent(MainViewActivity.this , ProjectActivity.class);
    	MainViewActivity.this.startActivity(intentMain);
        Log.i("Content "," Main layout ");
		
    }
	    
//===========================================================================================================
	    
	//This is used to start intent to send hello message
	public class ResponseReceiver extends BroadcastReceiver {
	   public static final String ACTION_RESP =
	      "com.mamlambo.intent.action.MESSAGE_PROCESSED";
		 
	   @Override
	   public void onReceive(Context context, Intent intent) {

		   Toast.makeText(getApplicationContext(), "hello",Toast.LENGTH_SHORT).show();
		   Intent msgIntent = new Intent();
		   startService(msgIntent);
			   
			   
			   
	   }
	}
//============================================================================================================ 
	
	//To write the data in the list view
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
    //To actually form the List and the contains in this activity
    private class CustomAdapter extends ArrayAdapter<RowData> {
        public CustomAdapter(Context context, int resource,int textViewResourceId, List<RowData> objects) {
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
        	    convertView = mInflater.inflate(R.layout.list, null);
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