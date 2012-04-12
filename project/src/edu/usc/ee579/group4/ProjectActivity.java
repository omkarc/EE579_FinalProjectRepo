package edu.usc.ee579.group4;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;



//import edu.usc.ee579.group4.MainViewActivity;
import edu.usc.ee579.group4.Client1;

public class ProjectActivity extends ListActivity {
private LayoutInflater mInflater;
private Vector<RowData> data;
static List<String> records = new ArrayList<String>();
RowData rd;
CustomAdapter adapter;
int pos=1;
String[] fields = new String[] {
//"Register",
//"Go L!ve",
"Friends Online",
"My Favourites",
"Request a File",
"Search Online",
"My Activity",
"Upload to Server",
"Add Friends",
"Log Off"

};

static final String[] detail = new String[] {
//"Click to Register your App",
//"Logon to Server@Euphony",
"Look Who's Online",  
"Share your favourites",
"Get what you want  ",
"Search for an item Online",
"Shows your past Activity",
"Create your own Database @Euphony",
"Gr(',')w your fRIEND Circle",
"Log Off"
};

private Integer[] imgid = {   
		
		R.drawable.friends,
		R.drawable.fav,R.drawable.arrow,
		R.drawable.search_512,R.drawable.activities,
		R.drawable.images,R.drawable.add,
		R.drawable.exit

    };
RelativeLayout layr1;
//Animation ar3;

@Override
    public void onCreate(Bundle savedInstanceState) {
        
	
	Bundle b = this.getIntent().getExtras();
	
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


//===========================================================================================================================

if(position==6){
	
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

if(position==0){
	
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
		alert.setPositiveButton("Search by Name",
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog,
							int whichButton) {
						String  Name1,Name2,Name3;
						  Name1 =    input1.getText().toString();
	    		          Name2 =    input2.getText().toString();
	    	              Name3 =    input3.getText().toString();
	    	              if(!((Name1.length())>0))Name1="*";
	    	              if(!((Name2.length())>0))Name2="*";
	    	              if(!((Name3.length())>0))Name3="*";
	    	            searchFriends(1,0,Name1,Name2,Name3);
						
						//alert.setView(input);						
						
					}
				});

		alert.setNegativeButton("Show My Gang",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
					
					try {
						Toast.makeText(getApplicationContext(),
								"Request sent to server", Toast.LENGTH_SHORT)
								.show();
					
						
						
						addFriends(null,null,0,1);
					
					
					
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					}
				});

	
	
		 alert.setNeutralButton("Show Me All", new DialogInterface.OnClickListener(){
	        	
	        	public void onClick(DialogInterface dialog, int id) {
	        		
	        	
	        		searchFriends(0,1,null,null,null);
	        		
	        	}
	        	
	        	
	        	
	        });
	
			alert.show();
}
	//searchFriends();
			//===========================================================================================================================	
if(position==7){
	
	if(MainViewActivity.LOGGED=="LOGGED")
	{
	
		MainViewActivity.LOGGED="FLASE";
		try {
		Client1 client = new Client1("192.168.0.14",9777);
		
		
		client.sendMessage("LOG OFF"+","+MainViewActivity.valueOfEmail);
		Toast.makeText(getApplicationContext(),
				"Signed Out", Toast.LENGTH_SHORT)
				.show();
		}
	
	
	 catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	}
	
	else Toast.makeText(getApplicationContext(),
			"You are already Logged Off", Toast.LENGTH_SHORT)
			.show();
	
	
	
}



    
       }//end of onListItemClick
  //==============================================================================================================  

  //  =============================================================================================================== 
    	public void searchFriends(int i,int ii, String Name1,String Name2,String Name3){
    
    	      	  
    try {
    	// Client1 getListOfFriends = new Client1("192.168.0.14",9777);//(Client1)getIntent().getSerializableExtra("abcd");
    	Client1 getListOfFriends = new Client1("207.151.252.143",9777);
		if(ii==1)
		{
    	 getListOfFriends.sendMessage("GET LIST OF FRIENDS");
		}
		else if(i==1){
			getListOfFriends.sendMessage("REQUESTED "+","+Name1+","+Name2+","+Name3);
		}
		Toast.makeText(getApplicationContext(),"List Updated", Toast.LENGTH_LONG).show();
		Intent intentMain = new Intent(ProjectActivity.this , FriendList.class);
    	ProjectActivity.this.startActivity(intentMain);
        Log.i("Content "," Main layout ");
		
		
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    
    	  
    	  }//end of searchFriends()
    //========================================================================================================
      
    //========================================================================================================
    	
    	
    	public void addFriends(String valueOfName,String valueOfEmail,int action,int action2) throws IOException{
    		
    		  if(records.size()!=0)
    			  records.clear();

    		File f = new File("sdcard/MyFriends/MyFriends.txt");
    			if (f.exists()) {
    				
    				if(action==0||action2==1){
    					
    					
    					
    				BufferedReader reader = null;
    			
    					reader = new BufferedReader(new FileReader(
    							"/sdcard/MyFriends/MyFriends.txt"));
    			    	String line;
    				
    					int i=-1;
    						while ((line = reader.readLine()) != null)

    						{
    							if(!line.contains("*")||!line.contains("")){
    		                 //   Toast.makeText(getApplicationContext(), line, Toast.LENGTH_SHORT).show();
    							//records.add(line);
    								i=i+1;
                                records.add(line);
                                
    							}
    						}
    					
    				reader.close();
    				if(action==0 && action2!=1){
    				Intent intentMain = new Intent(ProjectActivity.this , AddedFriends.class);
    		    	ProjectActivity.this.startActivity(intentMain);}
    				
    				
    				 if(action2==1){
    				
    					 Client1 getOnlineFriends = new Client1("207.151.252.143",9777);
    				
    					Toast.makeText(getApplicationContext(), "hello", Toast.LENGTH_SHORT).show();
    					for(i=0;i<records.size();i++){
    					String[] temp= records.get(i).split(",");
    			     //   Toast.makeText(getApplicationContext(),records.get(i), Toast.LENGTH_SHORT).show();
    			        getOnlineFriends.sendMessage("GANG"+","+temp[0]+","+temp[1]);
    			        try {
							
    			        	Thread.sleep(2000);}
						    catch (InterruptedException e) {
							// TODO Auto-generated catch block
							  e.printStackTrace();}
						
    			        
    			        
    			        temp=null;
    			        }//end of for
    				
    					getOnlineFriends.sendMessage("END");
try {
							
    			        	Thread.sleep(500);}
						    catch (InterruptedException e) {
							// TODO Auto-generated catch block
							  e.printStackTrace();}
    					Intent intentMain = new Intent(ProjectActivity.this , FriendList.class);
        		    	ProjectActivity.this.startActivity(intentMain);
    					
    					
    					
    					
    				}//end of if (action2==1)
    				
    				
    				}//end of action==0 || action2==0

    				if(action==1){
    					FileWriter fstream = new FileWriter("/sdcard/MyFriends/MyFriends.txt",true);
    					BufferedWriter out = new BufferedWriter(fstream);
    			
    					out.write("\n*\n");
    					out.write(valueOfName+","+valueOfEmail);
    					out.close();
    			        Toast.makeText(getApplicationContext(),"FRIEND ADDED", Toast.LENGTH_SHORT).show();
    				
    				}
    			
    			  				
    				
    				
    				
    			}//end of if(f.exists)
    			else if(!f.exists() && action==1){
    				File ff = new File("sdcard/MyFriends/");
    					ff.mkdirs();
    					FileWriter fstream = new FileWriter("/sdcard/MyFriends/MyFriends.txt",true);
    					BufferedWriter out = new BufferedWriter(fstream);
    					//out.write("\n*\n");
    					out.write(valueOfName+","+valueOfEmail);
    					out.close();
    					Toast.makeText(getApplicationContext(),"FRIEND ADDED", Toast.LENGTH_SHORT).show();
    				}
    				

    			 
    			 
    			//}//end of else
    			
    		    }//end of addFriends() 
    	
  	//============================================================================================================
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