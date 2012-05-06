package edu.usc.ee579.group4;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

//This class is been used to pop a informative message to the user that a file is been requested or Send in background
public class MyBroadcastReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		Toast.makeText(context, "File Received",
				Toast.LENGTH_LONG).show();
		Log.v("abcd","brr");
		// Intent msgIntent = new Intent();
		//	startService(msgIntent);
			
		// Vibrate the mobile phone
		//	Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
		//	vibrator.vibrate(2000);
	}
 
}