package com.example.android_hw2;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.xmlrpc.android.XMLRPCClient;
import org.xmlrpc.android.XMLRPCException;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.StrictMode;
import android.util.Log;

public class MyService extends IntentService {

	public MyService(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	public MyService() {
		super(null);
		// TODO Auto-generated constructor stub
	}

	private XMLRPCClient client;
	private URI uri;
	ArrayList<MyGeofence> MyGeofenceAr;
	

	int activityRecognitionConfidence;
	int activityRecognitionType;
	String activityRecognitionName;

	long userID;
	int msgID = 0;

	
  
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    //TODO do something useful
    uri = URI.create("http://vmbaumgarten1.informatik.tu-muenchen.de/2013/info.php");

    if (android.os.Build.VERSION.SDK_INT > 9) {
	    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
	    StrictMode.setThreadPolicy(policy);
	}
    // Restore preferences
      SharedPreferences settings = getSharedPreferences ("pref1",0);
      long userID = settings.getLong("userID", -1);
      if(userID==-1)
      {
    	  
          client = new XMLRPCClient(uri);
          long responseID=(long)getID();
          Log.d("USERID", ""+responseID);
          
          SharedPreferences.Editor editor = settings.edit();
          editor.putLong("userID", responseID);
          editor.commit();
      }
	  
	  
	  
    return Service.START_NOT_STICKY;
  }
  
  

  @Override
  public IBinder onBind(Intent intent) {
  //TODO for communication return IBinder implementation
    return null;
  }
  
  
  private long getID() {
	  long id = -1;
	    try {
	    	id = (Integer) client.call("hw2.getID");
	    } catch (XMLRPCException e) {
	        Log.w("XMLRPC getID", "Error", e);
	        id = -1;
	    }       
	    return id;
	}

	protected void onHandleIntent(Intent intent) {

		onHandleIntentLocationClient(intent);

		onHandleIntentActivityRecognition(intent);

	}

	protected void onHandleIntentLocationClient(Intent intent) {
		// First check for errors
		if (LocationClient.hasError(intent)) {
			// Get the error code with a static method
			int errorCode = LocationClient.getErrorCode(intent);
			// Log the error
			Log.e("ReceiveTransitionsIntentService",
					"Location Services error: " + Integer.toString(errorCode));
			/*
			 * You can also send the error code to an Activity or Fragment with
			 * a broadcast Intent
			 */
			/*
			 * If there's no error, get the transition type and the IDs of the
			 * geofence or geofences that triggered the transition
			 */
		} else {
			// Get the type of transition (entry or exit)
			int transitionType = LocationClient.getGeofenceTransition(intent);
			// Test that a valid transition was reported
			if ((transitionType == Geofence.GEOFENCE_TRANSITION_ENTER)
					|| (transitionType == Geofence.GEOFENCE_TRANSITION_EXIT)) {
				List<Geofence> triggerList = LocationClient
						.getTriggeringGeofences(intent);

				String[] triggerIds = new String[triggerList.size()];

				client = new XMLRPCClient(uri);

				for (int i = 0; i < triggerIds.length; i++) {
					// Store the Id of each geofence
					triggerIds[i] = triggerList.get(i).getRequestId();
					// TODO Get the Geofence maager in order find the suitable
					// Geofence according to ID
					MyGeofence item = MyGeofenceAr.get(Integer
							.parseInt(triggerIds[i]) - 1);
					String response = (String) transfer(userID, msgID++,
							item.getLatitude(), item.getLongitude(), 5, activityRecognitionType,
							System.currentTimeMillis());
					Log.d("Service transfer", response);
				}
				/*
				 * At this point, you can store the IDs for further use display
				 * them, or display the details associated with them.
				 */

			}
			// An invalid transition was reported
			else {
				Log.e("ReceiveTransitionsIntentService",
						"Geofence transition error: "
								+ Integer.toString(transitionType));
			}

		}

	}

	protected void onHandleIntentActivityRecognition(Intent intent) {
		// ...
		// If the incoming intent contains an update
		if (ActivityRecognitionResult.hasResult(intent)) {
			// Get the update
			ActivityRecognitionResult result = ActivityRecognitionResult
					.extractResult(intent);
			// Get the most probable activity
			DetectedActivity mostProbableActivity = result
					.getMostProbableActivity();
			/*
			 * Get the probability that this activity is the the user's actual
			 * activity
			 */
			activityRecognitionConfidence = mostProbableActivity.getConfidence();
			/*
			 * Get an integer describing the type of activity
			 */
			activityRecognitionType = mostProbableActivity.getType();
			activityRecognitionName = getNameFromType(activityRecognitionType);
			/*
			 * At this point, you have retrieved all the information for the
			 * current update. You can display this information to the user in a
			 * notification, or send it to an Activity or Service in a broadcast
			 * Intent.
			 */
			// ...
		} else {
			/*
			 * This implementation ignores intents that don't contain an
			 * activity update. If you wish, you can report them as errors.
			 */
		}
		// ...
	}

	// ...

	/**
	 * Map detected activity types to strings
	 * 
	 * @param activityType
	 *            The detected activity type
	 * @return A user-readable name for the type
	 */
	private String getNameFromType(int activityType) {
		switch (activityType) {
		case DetectedActivity.IN_VEHICLE:
			return "in_vehicle";
		case DetectedActivity.ON_BICYCLE:
			return "on_bicycle";
		case DetectedActivity.ON_FOOT:
			return "on_foot";
		case DetectedActivity.STILL:
			return "still";
		case DetectedActivity.UNKNOWN:
			return "unknown";
		case DetectedActivity.TILTING:
			return "tilting";
		}
		return "unknown";
	}

	
	/*
	 * 
	 * userid - Who is sending it? (c.f. user id) msgid - Unique message number
	 * (incrementing number that day, starts with 0) lat - Latitude longt -
	 * Longitude prec - Precision (of the position e.g. 5 meters or 50
	 * meters/area) move - Movement type (0 = subway, 1 = walking, 2 = bus) time
	 * - Mobile device time [ timestamp – getTime() ]
	 */

	private String transfer(long userid, int msgid, double d, double f,
			int prec, int move, long time) {
		String text = "";
		try {
			int response = (Integer) client.call("hw2.transfer", userid, msgid,
					d, f, prec, move, time);
			if (response == 200)
				text = "Submission Ok";
			else if (response == 500)
				text = "Missing value/other error";

		} catch (XMLRPCException e) {
			Log.w("XMLRPC transfer", "Error", e);
			text = "XMLRPC error";
		}
		return text;
	}

}