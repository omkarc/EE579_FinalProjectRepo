package edu.usc.ee579.group4;


import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Vector;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ParseException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;

import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import edu.usc.ee579.group4.Client1;

/*
 * This activity is used to display the files when the user wants to see of a particular type like
 * songs,videos or image and also to send file to the user
*/
public class GetFileActivity extends ListActivity {

	private LayoutInflater mInflater;
	private Vector<RowData> data;
	RowData rd;
	CustomAdapter adapter;
	static int const1=0,const2=0;
	static String Confirm2="FALSE";static String Query=null;
	static String[] ip;
	private Integer[] imgid = {   
			
		R.drawable.song,R.drawable.video,
		R.drawable.pic,
	};
	RelativeLayout layr1;
	//Animation ar3;
	ProgressDialog progressBar;
	private int progressBarStatus = 0;
	private Handler progressBarHandler = new Handler();
	
	//This is the first function called when the activity starts
	@Override
    public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
	        
        mInflater = (LayoutInflater) getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        data = new Vector<RowData>();
        //Depending on the file you want to see does file are selected and displayed to the user
        for(int i=0;i<Client1.records2.size();i++){
        	try {
        		String item=Client1.records2.get(i);
        		String ext =item.substring((item.lastIndexOf('.') +1));
        		if(item.contains("mp3")||item.contains("wav"))
        			rd = new RowData(0,item);
        		else if(item.contains("avi")||item.contains("mp4")||item.contains("mpeg"))
        			rd = new RowData(1,item);
        		else if(item.contains("jpeg")||item.contains("jpg")||item.contains("png")||item.contains("mpg"))
        			rd = new RowData(2,item);
        		
						  
					  
        	} catch (ParseException e) {
    			e.printStackTrace();
        	}
        	data.add(rd);
        }//end of for loop
				          
        adapter = new CustomAdapter(this, R.layout.list1,R.id.title, data);
        setListAdapter(adapter);
        //   getListView().setTextFilterEnabled(true);
        getListView().setBackgroundResource(R.drawable.wall1);
        
						
        
	}//end of onCreate
	    	  
	  
	
	public void onListItemClick(ListView parent, View v, final int position, long id) { 
		adapter = (CustomAdapter) parent.getAdapter();
		
		//	if(Client1.records2.get(position)!="Incorrect Email/Password.mp3"){ 
		final AlertDialog.Builder alert = new AlertDialog.Builder(this);//alertDialog is a subclass of dialog that can display 1/2/3 buttons
		
		alert.setMessage("Select your option");
		//Request for a file from the other peers
		alert.setPositiveButton("Request",new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog,	int whichButton) {
        	    progressBar = new ProgressDialog(GetFileActivity.this);
				progressBar.setCancelable(true);
					
				progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				progressBar.setProgress(0);
				progressBar.show();
		        Query=Client1.records2.get(position);
						
							  
		        new Thread(new Runnable() {
		        	public void run() {
							
									
						try {
										
								//Files to be requested are shown to user
								if(ProjectActivity.RequestFromServer==1)
								{		progressBar.setMessage("Requesting "+Client1.records2.get(position)+" from "+ProjectActivity.valueOfEmail+" @Server");
										//user.initializeUser(Client1.defaultServerIP);
										Client1 client = new Client1();
										client.sendMessage("CALL"+","+ProjectActivity.valueOfEmail);
										System.out.println(Query);
										client.request(Query);
										client.closeConnection();
								}
								else
								{
									Transceiver user= new Transceiver();
									const2=1;
									progressBar.setMessage("Requesting "+Client1.records2.get(position)+" from "+FriendList.temp[1]);
									ProjectActivity.RequestFromServer=0;
									user.initializeUser(FriendList.temp[2].split("/")[1]);
						
								}
								if(Transceiver.Confirm2=="TRUE")
								{
									Transceiver.Confirm2="FALSE";
									progressBarStatus = 1;
								}
									 
										
								if(Client1.Completed=="TRUE")	
								{
									Client1.Completed="FALSE";
									progressBarStatus = 1;
								}
								Looper.prepare();
									 
								// Update the progress bar
								progressBarHandler.post(new Runnable() {
									public void run() {
									 // progressBar.setProgress(progressBarStatus);
									}
								});
								if (progressBarStatus == 1) {
				 
									
									progressBar.dismiss();
										
									progressBarStatus=1;
									Toast.makeText(getApplicationContext(),"File Received", Toast.LENGTH_SHORT).show();
										
										
								}//end of if
								 
							} catch (IOException e) {
										// TODO Auto-generated catch block
								e.printStackTrace();
							}
							    
									
						}//end of public void run
							     
								  
								  
				  }).start();//end of runnable
					
				}
		});

		
		
		alert.setNeutralButton("Cancel", new DialogInterface.OnClickListener(){
	        	
        	public void onClick(DialogInterface dialog, int id) {
	        		
        	}
		});
		
		alert.show();
	//	}//end of if
	//	
		
		
	}//end of onListItemClick 
		    
	
	//Copy data from bytebuffer to byte array
	public static byte[] ByteCopy(ByteBuffer b) {
    	byte[] output = new byte[b.limit()];
    	for (int i = 0; i < b.limit(); i++) {
    		output[i] = b.get(i);
    	}
    	return output;
    }
	
	
	
	//Formation of the Row Data into the give list    	    
    private class RowData {
	        
    	protected int mId;
        protected String mTitle;
        //  protected String mDetail;
        RowData(int id,String title){
        	mId=id;
	        mTitle = title;
	     //   mDetail= detail;
	    }
        @Override
        public String toString() {
        	return mId+" "+mTitle;
        }
	        
	}
    //Forming of the List with its data
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
        	    convertView = mInflater.inflate(R.layout.mainlist, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);                   
            }
            holder = (ViewHolder) convertView.getTag();    
            
            title = holder.gettitle();     
            title.setText(rowData.mTitle);
            i11=holder.getImage();
            i11.setImageResource(imgid[rowData.mId]);
            return convertView;
        }
        private class ViewHolder {      
        	private View mRow;
        	private TextView title = null;
        	// private TextView detail = null;
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
        	

        	
        	public ImageView getImage() {
        		if(null == i11){
        			i11 = (ImageView) mRow.findViewById(R.id.img);                        
        		}
        		return i11;
        	}
	             
	    }
    }
}
