package edu.usc.ee579.group4;

import java.io.IOException;
import java.util.List;
import java.util.Vector;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ParseException;
import android.os.Bundle;
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
import edu.usc.ee579.group4.Client1;
public class AddedFriends extends ListActivity {

	private LayoutInflater mInflater;
	private Vector<RowData> data;
	RowData rd;
	CustomAdapter adapter;
	int pos=1;
	

	private Integer[] imgid = {   
			
			R.drawable.f1,R.drawable.f2,
			R.drawable.f3,R.drawable.f4,
			R.drawable.f5,R.drawable.f6,
			R.drawable.f7,R.drawable.f8,
			R.drawable.f9,R.drawable.f10,
			R.drawable.f11,R.drawable.f12,
			R.drawable.f13,R.drawable.f14,
			R.drawable.f20
			
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
	        for(int i=0;i<ProjectActivity.records.size();i++){
				  try {
					  String [] item=ProjectActivity.records.get(i).split(",");
					 // Toast.makeText(getApplicationContext(), item[0], Toast.LENGTH_LONG).show();
				       rd = new RowData(i,item[0],item[1]);
				  } catch (ParseException e) {
				  e.printStackTrace();
				  }
				               data.add(rd);
				          }//end of for loop
				          
				          adapter = new CustomAdapter(this, R.layout.list1,R.id.title, data);
				          setListAdapter(adapter);
				          getListView().setTextFilterEnabled(true);
	    //  Client1.records.removeAll(null);
						
	}//end of onCreate
	    	  
	          /*
	public void onListItemClick(ListView parent, View v, int position, long id) { 
		adapter = (CustomAdapter) parent.getAdapter();
		
		 
		final AlertDialog.Builder alert = new AlertDialog.Builder(this);//alertDialog is a subclass of dialog that can display 1/2/3 buttons
		
		String[] temp=Client1.records.get(position).split(",");
		
		alert.setMessage("Email "+temp[1]);
		alert.setTitle(temp[0]+"\n"+temp[2]);
		//alert.setView(input);
		if(position>14)alert.setIcon(imgid[12]);
		else alert.setIcon(imgid[position]);
		alert.setPositiveButton("Request",
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog,
							int whichButton) {

						//todo list
							}
				});

		alert.setNegativeButton("Share",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						//todo list
					}
				});

		
		
		 alert.setNeutralButton("Cancel", new DialogInterface.OnClickListener(){
	        	
	        	public void onClick(DialogInterface dialog, int id) {
	        		
	        	}
		 });
		 alert.show();
		
		
		       }//end of onListItemClick 
		    
	*/
	
	
	
	
	
	    	    
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
