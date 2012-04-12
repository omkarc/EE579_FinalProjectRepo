package edu.usc.ee579.group4;

import java.io.IOException;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.usc.ee579.*;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ParseException;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
//import edu.usc.anrg.a3final.R;


public class MainViewActivity extends ListActivity {
public static Client1 client;
private LayoutInflater mInflater;
private Vector<RowData> data;
RowData rd;
CustomAdapter adapter;
int pos=1,flag=0;
static String valueOfEmail;
String[] fields = new String[] {
"Register",
"Go L!ve",
"!nVis!ble"

};
 static String LOGGED="FALSE";
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


@Override
    public void onCreate(Bundle savedInstanceState) {
        
	
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
        getListView().setTextFilterEnabled(true);
      }//end of onCreate
    

 
    
    public void onListItemClick(ListView parent, View v, int position, long id) { 
adapter = (CustomAdapter) parent.getAdapter();
 if(position==0)
 {
	if(flag==0)
	 register();
	else Toast.makeText(getApplicationContext(), "User already Registered", Toast.LENGTH_SHORT);
 }

if(position==1){
//	if(LOGGED=="LOGGED"){callMainView();}
	//else 
		login();

}
   
 if(position==2){
	 callMainView();
 }
	 
    }//end of onItemClickListener
    
   
    
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
         alert.setMessage("Enter Your Name, Email Address and Password"); 
         alert.setIcon(R.drawable.ic_launcher);
         // Set an EditText view to get user input  
         alert.setView(textEntryView); 
         AlertDialog loginPrompt = alert.create();

    		 final EditText input1 = (EditText) textEntryView.findViewById(R.id.name);
			 final EditText input2 = (EditText) textEntryView.findViewById(R.id.email);
		     final EditText input3 = (EditText) textEntryView.findViewById(R.id.password);

    		 alert.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
    			
    			
    			public void onClick(DialogInterface dialog, int whichButton) {
    			 //String Email = input.getText().toString().trim();
    			 
    			
    			
    		        String valueOfName =    input1.getText().toString();
    		        String valueOfEmail =    input2.getText().toString();
    	            String valueOfPassword = input3.getText().toString();
    			 
    			 
    			Matcher m = p.matcher(valueOfEmail);

    		    if(m.matches() && valueOfName!="" && valueOfPassword!="") {
    		    	
    		      try{
    		     //client = new Client1("192.168.0.14",9777);
    		     client = new Client1("207.151.252.143",9777);
    		     
    				client.sendMessage("REGISTER"+","+valueOfName+","+valueOfEmail+","+valueOfPassword);
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
    		    
    		    else {Toast.makeText(getApplicationContext(),"Invalid Email Addresss", Toast.LENGTH_SHORT).show(); 
    		 	register();
    		    }
    			}
    			});
    		
    		
    		alert.setNegativeButton("Cancel",
    				new DialogInterface.OnClickListener() {
    					public void onClick(DialogInterface dialog, int which) {
    						Toast.makeText(getApplicationContext(),
    								"Cancelled", Toast.LENGTH_SHORT)
    								.show();
    		
    					}
    				});
    		
    		
    		
    		 alert.show();

    	 }
    	    
  
    public void login(){
    	

    	
    	LayoutInflater factory = LayoutInflater.from(this);            
        final View textEntryView = factory.inflate(R.layout.login,null);

        AlertDialog.Builder alert = new AlertDialog.Builder(this); 
       
        alert.setTitle("Login Details"); 
        alert.setMessage("Enter Your Email and Password"); 
        alert.setIcon(R.drawable.music12);
        // Set an EditText view to get user input  
        alert.setView(textEntryView); 
        AlertDialog loginPrompt = alert.create();
     
        final EditText input1 = (EditText) textEntryView.findViewById(R.id.username);
        final EditText input2 = (EditText) textEntryView.findViewById(R.id.password);
        //final EditText input3 = (EditText) textEntryView.findViewById(R.id.work_offline);
        
        alert.setPositiveButton("Login", new DialogInterface.OnClickListener() { 
        public void onClick(DialogInterface dialog, int whichButton) {
        	
         
            valueOfEmail =    input1.getText().toString();
            String valueOfPassword = input2.getText().toString();
           try {
    			//Client1	client = new Client1("192.168.0.14",9777);
    			Client1	client = new Client1("207.151.252.143",9777);
    			
    			client.sendLoginDetails("LOGIN"+","+valueOfEmail+","+valueOfPassword);
    			if (client.Confirm =="FALSE"){
    				Toast.makeText(getApplicationContext(),"Incorrect Login Details:LOGIN AGAIN",Toast.LENGTH_SHORT).show();
    				client.closeConnection();
    				login();
    			}
    			else { 
    			LOGGED="LOGGED";   				
    		    Toast.makeText(getApplicationContext(),"Welcome to Euphony",Toast.LENGTH_SHORT).show();
    			
    			callMainView();
    		       			
    			}
    			} catch (IOException e) {
    				// TODO Auto-generated catch block
    				Toast.makeText(getApplicationContext(),"client could not connect",Toast.LENGTH_SHORT).show();
    				e.printStackTrace();
    			}

        } 
        }); 

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() { 
          public void onClick(DialogInterface dialog, int whichButton) { 
            // Canceled. 
          } 
        }); 

        alert.show(); 

    }
    public void callMainView(){
 
    	Intent intentMain = new Intent(MainViewActivity.this , ProjectActivity.class);
    	MainViewActivity.this.startActivity(intentMain);
        Log.i("Content "," Main layout ");
		
    }
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