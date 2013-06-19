package com.example.android_hw2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ToggleButton;

public class MainActivity extends Activity
{

	GeofenceManager geofenceManager;
	ActivityManager activityManager;
	
	protected Intent intentservice ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	    
		if (android.os.Build.VERSION.SDK_INT > 9) {
		    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		    StrictMode.setThreadPolicy(policy);
		}
		
		intentservice = new Intent(this, MyService.class);;
		//intentservice.putExtra("mCurrentGeofences", geofenceManager.MyGeofence);
		//TODO
		geofenceManager=new GeofenceManager(this,intentservice);
		geofenceManager.createGeofences();
		geofenceManager.addGeofences();
		
		//TODO
		//activityManager = new ActivityManager(this);
		
		//final Context mContext = getApplicationContext();
		//final Context mContext = getBaseContext();
		
		
		// get your ToggleButton
		ToggleButton b = (ToggleButton) findViewById(R.id.toggleButton1);
		Log.d("Main", "Button is created and is going to wait for the press.");
		// attach an OnClickListener
		b.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton toggleButton,
					boolean isChecked) {
				
				//Intent service = new Intent(mContext, MyService.class);
				 
				//service.setClassName("com.example.android_hw2", "MyService");
				
				if (isChecked) {

				//	mContext.startService(service);
				//	startService(service);
				//	activityManager.startUpdates();
					startService(intentservice);
				} else {
				//	activityManager.stopUpdates();
				//	mContext.stopService(service);
				//	stopService(service);
					startService(intentservice);
				}

			}

		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/*
	 * Handle results returned to the FragmentActivity by Google Play services
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// Decide what to do based on the original request code
		switch (requestCode) {

		case Util.CONNECTION_FAILURE_RESOLUTION_REQUEST:
			/*
			 * If the result code is Activity.RESULT_OK, try to connect again
			 */
			switch (resultCode) {

			case Activity.RESULT_OK:
				/*
				 * Try the request again
				 */

				break;
			}

		}
	}
	
	
	
	
}
