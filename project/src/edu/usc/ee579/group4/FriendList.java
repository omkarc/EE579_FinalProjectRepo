package edu.usc.ee579.group4;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ParseException;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
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
 * This activity gives the options of requesting a file from a friend or Sending a file to a friend	
*/
public class FriendList extends ListActivity {

	private LayoutInflater mInflater;
	private Vector<RowData> data;
	RowData rd;
	CustomAdapter adapter;
	int pos=1;static int flag=0;
	static String[] temp;
	static int const1=0;
	private Integer[] imgid = {   
			
		R.drawable.f1,R.drawable.f2,
		R.drawable.f4,R.drawable.f3,
		R.drawable.f11,R.drawable.f6,
		R.drawable.f7,R.drawable.f8,
		R.drawable.f9,R.drawable.f10,
		R.drawable.f5,R.drawable.f12,
		R.drawable.f13,R.drawable.f14,
		R.drawable.f20
			
    };
	RelativeLayout layr1;
	//Animation ar3;
	ProgressDialog progressBar;
	private int progressBarStatus = 0;
	private Handler progressBarHandler = new Handler();
	
	//THis is activity which is called first
	@Override
    public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
	        
        mInflater = (LayoutInflater) getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        data = new Vector<RowData>();
        for(int i=0;i<Client1.records.size();i++){
        	try {
        		String [] item=Client1.records.get(i).split(",");
					 
        		rd = new RowData(i,item[0],item[1]);
        	} catch (ParseException e) {
        		e.printStackTrace();
        	}
        	data.add(rd);
        }//end of for loop
				          
        adapter = new CustomAdapter(this, R.layout.list1,R.id.title, data);
        setListAdapter(adapter);
        //    getListView().setTextFilterEnabled(true);
        getListView().setBackgroundResource(R.drawable.wall1);
	    //  Client1.records.removeAll(null);
						
	}//end of onCreate
	    	  
	          
	public void onListItemClick(ListView parent, View v, int position, long id) { 
		adapter = (CustomAdapter) parent.getAdapter();
		
		 
		final AlertDialog.Builder alert = new AlertDialog.Builder(this);//alertDialog is a subclass of dialog that can display 1/2/3 buttons
		
		//	if(Client1.flag!=2){
		temp=Client1.records.get(position).split(",");
		 
		if(ProjectActivity.const2==0)
		{
		
		
			alert.setMessage("Email "+temp[1]);
			alert.setTitle(temp[0]);
		
			if(position>14)
				alert.setIcon(imgid[12]);
			else 
				alert.setIcon(imgid[position]);
			//If you want to request a file from your friend
			alert.setPositiveButton("Request",new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog,int whichButton) {

				    progressBar = new ProgressDialog(FriendList.this);
					progressBar.setCancelable(true);
					progressBar.setMessage("Requesting Server...");
					progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
					progressBar.setProgress(0);
					progressBar.show();
					  
								
					//The Shared file are requested from the server				  
					new Thread(new Runnable() {
						public void run() {
							try {
								Client1 client2 =new Client1();
								client2.sendMessage("FILES"+","+temp[1]);
								client2.closeConnection();
							} catch (IOException e) {
							// TODO Auto-generated catch block
								e.printStackTrace();
							}
							if(Client1.Confirm2=="TRUE")
								progressBarStatus=1;
												
												
							Looper.prepare(); 
											 
								// Update the progress bar
							progressBarHandler.post(new Runnable() {
								public void run() {
									// progressBar.setProgress(progressBarStatus);
								}
							});
						
							 
											// ok, file is downloaded,
							if (progressBarStatus == 1) {
							 
												// sleep 2 seconds, so that you can see the 100%
											
							 					// close the progress bar dialog
								progressBar.dismiss();
								Toast.makeText(getApplicationContext(),"List Updated", Toast.LENGTH_LONG).show();
								//flag=1;
								ProjectActivity.RequestFromServer=0;
								//After getting the list of files the file requested from the other peer
								Intent intentMain = new Intent(FriendList.this ,GetFileActivity.class);
									    	
								FriendList.this.startActivity(intentMain);
										        
												
												
							}//end of if
					  }//end of public void run
			       }).start();//end of runnable
							
				}
		});
		//To send a file directly to other peer
		alert.setNegativeButton("Send a File",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				//todo list

	          flag=4; const1 = 4;ProjectActivity.downloads=0;
	          //     Toast.makeText(getApplicationContext(), "hello", Toast.LENGTH_SHORT).show();
	          Intent intentMain = new Intent(FriendList.this , MusicActivity.class);
	          FriendList.this.startActivity(intentMain);
	  		
	          
			}
		});

		
		//If you donot want to to Anything
		alert.setNeutralButton("Cancel", new DialogInterface.OnClickListener(){
	        	
        	public void onClick(DialogInterface dialog, int id) {
	        		
        	}
		});
		
		alert.show();
	}
		
	else {
		
		//ProjectActivity.const2=0;
		
		alert.setMessage("Email "+temp[1]);
		alert.setTitle(temp[0]);
			
		//alert.setView(input);
		if(position>14)
			alert.setIcon(imgid[12]);
		else
			alert.setIcon(imgid[position]);
		//Request a file from the peer
		alert.setPositiveButton("Request",new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog,int whichButton) {

			    progressBar = new ProgressDialog(FriendList.this);
				progressBar.setCancelable(true);
				progressBar.setMessage("Requesting "+temp[0]+" from "+temp[1]);
				progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				progressBar.setProgress(0);
				progressBar.show();
							  
									
										  
				new Thread(new Runnable() {
					public void run() {
										
												
						try {
													
							Transceiver user= new Transceiver();
							GetFileActivity.const2=0;
							user.initializeUser(temp[2].split("/")[1]);
							if(Transceiver.Confirm2=="TRUE")
								progressBarStatus = 1;
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

			
		//Else dont do anything
		alert.setNeutralButton("Cancel", new DialogInterface.OnClickListener(){
		        	
        	public void onClick(DialogInterface dialog, int id) {
		        		
        	}
		});
		alert.show();
		
	}
	
		
	//	}
   }//end of onListItemClick 
	//This is portion where the list we got from the server is displayed to the user	    
	public void callProgressBar(){
		progressBar = new ProgressDialog(FriendList.this);
		progressBar.setCancelable(true);
		progressBar.setMessage("Requesting Server...");
		progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressBar.setProgress(0);
	//	progressBar.setMax(100);
		progressBar.show();
  
		
			  
		new Thread(new Runnable() {
			public void run() {
							
				if(Client1.records2.size()>0)
					progressBarStatus=1;
						
						
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
					Intent intentMain = new Intent(FriendList.this ,GetFileActivity.class);
			    	FriendList.this.startActivity(intentMain);
				        
						
						
				}//end of if
		  }//end of public void run
			       }).start();//end of runnable
	 
       }//end of callProgessBar
		
	
	
	
	
	
	//Forming the data in row manner    	    
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
    
    //Filling out the fields in the row list
    private class CustomAdapter extends ArrayAdapter<RowData> {
        public CustomAdapter(Context context, int resource, int textViewResourceId, List<RowData> objects) {
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
            if (rowData.mId>14)i11.setImageResource(imgid[12]);
            else i11.setImageResource(imgid[rowData.mId]);
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
