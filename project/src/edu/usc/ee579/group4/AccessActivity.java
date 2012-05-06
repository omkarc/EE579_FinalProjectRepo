package edu.usc.ee579.group4;

import java.io.File;
import java.io.IOException;
import java.util.List;

import java.util.Vector;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ParseException;
import android.os.Bundle;
import android.os.Environment;
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

import edu.usc.ee579.group4.*;

/*
 * This class is use to display the options available for the application users when it clicks on My Favourites
 * Like Lock your Account with a password or see your shared files and Sending you list of data to the Server 
*/

public class AccessActivity extends ListActivity {

	private LayoutInflater mInflater;
	private Vector<RowData> data;
	RowData rd;
	CustomAdapter adapter;
	static int flag=0;
	static String[] names=null;
	//Names to be displayed on the screen
	String[] fields = new String[] {
		"Lock Your Account",
		"My Files",
		"Send file names to Server"
	};
	private Integer[] imgid = {   
		R.drawable.shield,R.drawable.upload4,
		R.drawable.images
	};
	static final String[] detail = new String[] {
		"Setup Password for Your Account",
		"See your Shared Personnels",
		"Create a Virtual Image of your Stuff@Server"
	};
	
	
	RelativeLayout layr1;
	
	//This is function which runs first when this Activity is called by clicking on My favorites
	@Override
    public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
	    setContentView(R.layout.main);
	        
        mInflater = (LayoutInflater) getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        data = new Vector<RowData>();
        //	This creates the List View to be dispalyed on the app screen when this activity is called with the above names    
        for(int i=0;i<fields.length;i++){
        	try {
        	     rd = new RowData(i,fields[i],detail[i]);
        	} catch (ParseException e) {
        	e.printStackTrace();
        	}
        	data.add(rd);
        }//end of for loop
				          
        adapter = new CustomAdapter(this, R.layout.list1,R.id.title, data);
        setListAdapter(adapter);
        //   getListView().setTextFilterEnabled(true);
        getListView().setBackgroundResource(R.drawable.wall1);
	    //  Client1.records.removeAll(null);
						
	}//end of onCreate
	    	  
	//On clicking the option on the screen the corressponding functionality is called          
	public void onListItemClick(ListView parent, View v, int position, long id) { 
		adapter = (CustomAdapter) parent.getAdapter();

//===============================================================================================================================
		//This calls the functionality to lock your account
		if(position==0){
			// This asks the users with the password you want to use to lock your account.
			 LayoutInflater factory = LayoutInflater.from(this);            
		     final View textEntryView = factory.inflate(R.layout.access,null);

		     AlertDialog.Builder alert = new AlertDialog.Builder(this); 
		      
		     alert.setTitle("Lock your Account"); 
 			 alert.setMessage("Setup a Password"); 
 			 alert.setIcon(R.drawable.shield);
		  
 			 alert.setView(textEntryView); 
		

 			 //     final EditText input1 = (EditText) textEntryView.findViewById(R.id.email);
 			 //    final EditText input2 = (EditText) textEntryView.findViewById(R.id.username);
			 final EditText input3 = (EditText) textEntryView.findViewById(R.id.password);
			//After confirming with the password the detail is send to the server
	 		 alert.setPositiveButton("Lock", new DialogInterface.OnClickListener() {
		 			
		 			
	 			public void onClick(DialogInterface dialog, int whichButton) {
			
		 			try {
	 					Client1 client = new Client1();
				
						
	 					String valueOfPassword =    input3.getText().toString();
	 					if(MainViewActivity.valueOfEmail==null)
	 						Toast.makeText(getApplicationContext(), "Please Login First",Toast.LENGTH_SHORT).show();
	 					else
	 						client.sendMessage("LOCK"+","+MainViewActivity.valueOfEmail+","+valueOfPassword);
						if(valueOfPassword.length()==0)
							Toast.makeText(getApplicationContext(), "No Password Setup",Toast.LENGTH_SHORT).show();
						else
							Toast.makeText(getApplicationContext(), "Password successfully setup",Toast.LENGTH_SHORT).show();
						
						
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		 			
	 			}
			
 			});
		 		
		 		 
	 		alert.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					Toast.makeText(getApplicationContext(),"Cancelled", Toast.LENGTH_SHORT).show();
		 		
				}
			});
		 		
		 		
		 		
	 		alert.show();
			
			
		}//end of position==0
//==================================================================================================================
		
		//On clicking this the app shows what kind of files do the user want to see
		if(position==1){
			// There are three different option to select i.e. Songs,Videos and Images
			AlertDialog.Builder alert = new AlertDialog.Builder(this); 
      
			alert.setTitle("See your Media/Upload to Server"); 
			alert.setMessage("Select your option"); 
			alert.setIcon(R.drawable.main);
      
			alert.setPositiveButton("Songs", new DialogInterface.OnClickListener() {
			
				//Clicking on the songs option it will display all the songs in the Favourites folder
				public void onClick(DialogInterface dialog, int whichButton) {
	
			
					flag=1;
					ProjectActivity.downloads=0;ProjectActivity.const1=0;
					callUploadToServer();
				}
	
			});
		
			//Clicking on the images option it will display all the images in the Favourites folder
			alert.setNegativeButton("Images",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
						Toast.makeText(getApplicationContext(),
								"Showing Images", Toast.LENGTH_SHORT)
								.show();
						flag=3;
						ProjectActivity.downloads=0;ProjectActivity.const1=0;
						callUploadToServer();
				}
			});
		
			//Clicking on the videos option it will display all the videos in the Favourites folder
			alert.setNeutralButton("Videos", new DialogInterface.OnClickListener(){
	        	
	        	public void onClick(DialogInterface dialog, int id) {
	        		
	        		Toast.makeText(getApplicationContext(),
							"Showing Videos", Toast.LENGTH_SHORT)
							.show();
	        		flag=2;
	        		ProjectActivity.downloads=0;ProjectActivity.const1=0;
	        		callUploadToServer();
	        		
	        	}
			});
		
			alert.show();
		}//end of position==1
//===============================================================================================================================
		//This button allows the user to send the list of items to be shared in the Favourites folder
		if(position==2){
			String[] names=null;
			File yourDir;
			if(MainViewActivity.valueOfEmail!=null)
			{
				//This basically scans through all the data in the Favourites folder and send it to the Server for it to 
				//share with others
				try {
		
					File sdCardRoot = Environment.getExternalStorageDirectory();
					yourDir = new File(sdCardRoot, "/favorites");
					names= yourDir.list();
					Client1 client2=new Client1();
					for(int i=0;i<names.length;i++){
        	
						Toast.makeText(getApplicationContext(), names[i], Toast.LENGTH_SHORT).show();
						client2.sendMessage("Share"+","+names[i]);	
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			
		
					}//end of for
		

					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						// 	TODO Auto-generated catch block
						e.printStackTrace();
					}
		
					client2.sendMessage("COMPLETED"+","+MainViewActivity.valueOfEmail);
      
					client2.closeConnection();
			
		
				} catch (IOException e) {
					// 	TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else 
				Toast.makeText(getApplicationContext(), "Please Login First", Toast.LENGTH_SHORT).show();
		}



		
    }//end of onListItemClick 
//===================================================================================================================
	//To upload the data to the server different background activity is been called
	public void callUploadToServer(){
		

 		Intent intentMain = new Intent(AccessActivity.this , MusicActivity.class);
 		AccessActivity.this.startActivity(intentMain);
		
	}
	
	//This class define what is to defined and shown in each row in the list view
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
  //This class will form the rows for the list view the layout to be displayed on the phone screen
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
	        if (rowData.mId>14)
	        	i11.setImageResource(imgid[12]);
	        else 
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

	
	
	
	
	
	
	
	
	
