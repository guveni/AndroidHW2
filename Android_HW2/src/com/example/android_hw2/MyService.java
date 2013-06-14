package com.example.android_hw2;
import java.net.URI;
import java.util.List;

import org.xmlrpc.android.XMLRPCClient;
import org.xmlrpc.android.XMLRPCException;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
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
	
  
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    //TODO do something useful
    uri = URI.create("http://vmbaumgarten1.informatik.tu-muenchen.de/2013/info.php");
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
  
  protected void onHandleIntent(Intent intent) {
      // First check for errors
      if (LocationClient.hasError(intent)) {
          // Get the error code with a static method
          int errorCode = LocationClient.getErrorCode(intent);
          // Log the error
          Log.e("ReceiveTransitionsIntentService",
                  "Location Services error: " +
                  Integer.toString(errorCode));
          /*
           * You can also send the error code to an Activity or
           * Fragment with a broadcast Intent
           */
      /*
       * If there's no error, get the transition type and the IDs
       * of the geofence or geofences that triggered the transition
       */
      } else {
          // Get the type of transition (entry or exit)
          int transitionType =
                  LocationClient.getGeofenceTransition(intent);
          // Test that a valid transition was reported
          if (
              (transitionType == Geofence.GEOFENCE_TRANSITION_ENTER)
               ||
              (transitionType == Geofence.GEOFENCE_TRANSITION_EXIT)
             ) {
              List <Geofence> triggerList =
              		LocationClient.getTriggeringGeofences(intent);

              String[] triggerIds = new String[triggerList.size()];

              
              client = new XMLRPCClient(uri);
              
              for (int i = 0; i < triggerIds.length; i++) {
                  // Store the Id of each geofence
                  triggerIds[i] = triggerList.get(i).getRequestId();
                  //TODO Get the Geofence maager in order find the suitable Geofence according to ID
                  //String response=(String)transfer(triggerIds[i].ge);
                  //System.out.println(response);
              }
              /*
               * At this point, you can store the IDs for further use
               * display them, or display the details associated with
               * them.
               */
              
              
          }
          // An invalid transition was reported
          else {
              Log.e("ReceiveTransitionsIntentService",
                      "Geofence transition error: "+Integer.toString(transitionType));
          }
      
      }
  }
	
  

  @Override
  public IBinder onBind(Intent intent) {
  //TODO for communication return IBinder implementation
    return null;
  }
  
  
  private long getID() {
	  long id = -1;
	    try {
	    	id = (Long) client.call("hw2.getID");
	    } catch (XMLRPCException e) {
	        Log.w("XMLRPC getID", "Error", e);
	        id = -1;
	    }       
	    return id;
	}
  
  /*
   * 
  userid - Who is sending it? (c.f. user id)
  msgid - Unique message number (incrementing number that day, starts with 0)
  lat - Latitude
  longt - Longitude
  prec - Precision (of the position e.g. 5 meters or 50 meters/area)
  move - Movement type (0 = subway, 1 = walking, 2 = bus)
  time - Mobile device time [ timestamp � getTime() ]
   */
  
  private String transfer(int userid,int msgid,int lat,int longt,int prec,int move,int time) {
	    String text = "";
	    try {
	    	int response = (Integer) client.call("hw2.transfer",  userid, msgid, lat, longt, prec, move, time);
	        if(response==200)
	            text = "Submission Ok";
	        else if(response==500)
	        	text="Missing value/other error";
	        
	        
	    } catch (XMLRPCException e) {
	        Log.w("XMLRPC transfer", "Error", e);
	        text = "XMLRPC error";
	    }       
	    return text;
	}
  
 
  
  
		
	
}