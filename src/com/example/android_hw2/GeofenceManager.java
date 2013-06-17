package com.example.android_hw2;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationStatusCodes;
import com.google.android.gms.location.LocationClient.OnAddGeofencesResultListener;
import com.google.android.gms.location.LocationClient.OnRemoveGeofencesResultListener;

public class GeofenceManager implements
ConnectionCallbacks,
OnConnectionFailedListener,
OnAddGeofencesResultListener,
OnRemoveGeofencesResultListener
{

	/*
     * Use to set an expiration time for a geofence. After this amount
     * of time Location Services will stop tracking the geofence.
     */
    private static final long SECONDS_PER_HOUR = 60;
    private static final long MILLISECONDS_PER_SECOND = 1000;
    private static final long GEOFENCE_EXPIRATION_IN_HOURS = 100;
    private static final long GEOFENCE_EXPIRATION_TIME =
            GEOFENCE_EXPIRATION_IN_HOURS *
            SECONDS_PER_HOUR *
            MILLISECONDS_PER_SECOND;
    
    public ArrayList<Geofence> mCurrentGeofences;
    public ArrayList<MyGeofence> MyGeofence;
    
    Activity mContext;
    
    public GeofenceManager(Activity mCtx)
    {
    	mContext=mCtx;
    	mCurrentGeofences = new ArrayList<Geofence>();
        MyGeofence = new ArrayList<MyGeofence>();
    }
	
	 public void createGeofences() {
	        /*
	         * Create an internal object to store the data. Set its
	         * ID to "1". This is a "flattened" object that contains
	         * a set of strings
	         */
		 MyGeofence mGeofenceFroettmaning = new MyGeofence(
	                "1",
	                Double.valueOf("48.211798"),
	                Double.valueOf("11.616653"),
	                Float.valueOf("350"),
	                GEOFENCE_EXPIRATION_TIME,
	                // This geofence records only entry transitions
	                Geofence.GEOFENCE_TRANSITION_ENTER);
		 
		
		  
		 MyGeofence mGeofenceKieferngarten = new MyGeofence(
	                "2",
	                Double.valueOf("48.203810"),
	                Double.valueOf("11.613245"),
	                Float.valueOf("350"),
	                GEOFENCE_EXPIRATION_TIME,
	                Geofence.GEOFENCE_TRANSITION_ENTER);
		 
		 MyGeofence mGeofenceStudentenstadt = new MyGeofence(
	                "3",
	                Double.valueOf("48.183508"),
	                Double.valueOf("11.607627"),
	                Float.valueOf("350"),
	                GEOFENCE_EXPIRATION_TIME,
	                Geofence.GEOFENCE_TRANSITION_ENTER);
		 
		 MyGeofence mGeofenceAlteHeide = new MyGeofence(
	                "4",
	                Double.valueOf("48.178603"),
	                Double.valueOf("11.602655"),
	                Float.valueOf("350"),
	                GEOFENCE_EXPIRATION_TIME,
	                Geofence.GEOFENCE_TRANSITION_ENTER);
		 
		 
		 MyGeofence mGeofenceWholeRegion = new MyGeofence(
	                "5",
	                Double.valueOf("48.194926"),
	                Double.valueOf("11.615255"),
	                Float.valueOf("2000"),
	                GEOFENCE_EXPIRATION_TIME,
	                Geofence.GEOFENCE_TRANSITION_ENTER|Geofence.GEOFENCE_TRANSITION_EXIT);

	        MyGeofence.add(mGeofenceFroettmaning);
	        MyGeofence.add(mGeofenceKieferngarten);
	        MyGeofence.add(mGeofenceStudentenstadt);
	        MyGeofence.add(mGeofenceAlteHeide);
	        MyGeofence.add(mGeofenceWholeRegion);
	        
	        
	        
	        mCurrentGeofences.add(mGeofenceFroettmaning.toGeofence());
	        mCurrentGeofences.add(mGeofenceKieferngarten.toGeofence());
	        mCurrentGeofences.add(mGeofenceStudentenstadt.toGeofence());
	        mCurrentGeofences.add(mGeofenceAlteHeide.toGeofence());
	        mCurrentGeofences.add(mGeofenceWholeRegion.toGeofence());
	    }
	 
	// Holds the location client
    private LocationClient mLocationClient;
    // Stores the PendingIntent used to request geofence monitoring
    private PendingIntent mGeofenceRequestIntent;
    
    private PendingIntent mTransitionPendingIntent ;
    
    // Defines the allowable request types.
    public enum REQUEST_TYPE  {ADD,REMOVE_INTENT,REMOVE_LIST}
    private REQUEST_TYPE mRequestType;
    // Flag that indicates if a request is underway.
    private boolean mInProgress;
    // Store the list of geofence Ids to remove
    List<String> mGeofencesToRemove;

	    

		
	private PendingIntent getTransitionPendingIntent() {
		        // Create an explicit Intent
		        Intent intent = new Intent(mContext,
		        		MyService.class);//ReceiveTransitionsIntentService
		        //sends extra info (GeofenceArray)
		        intent.putExtra("mCurrentGeofences", MyGeofence);
		        /*
		         * Return the PendingIntent
		         */
		        return PendingIntent.getService(
		        		mContext,
		                0,
		                intent,
		                PendingIntent.FLAG_UPDATE_CURRENT);
		    }
		 
	    
    /**
     * Start a request for geofence monitoring by calling
     * LocationClient.connect().
     */
	public void addGeofences() {
	        // Start a request to add geofences
	        mRequestType = REQUEST_TYPE.ADD;
	        /*
	         * Test for Google Play services after setting the request type.
	         * If Google Play services isn't present, the proper request
	         * can be restarted.
	         */
	        if (!Util.servicesConnected(mContext)) {
	            return;
	        }
	        /*
	         * Create a new location client object. Since the current
	         * activity class implements ConnectionCallbacks and
	         * OnConnectionFailedListener, pass the current activity object
	         * as the listener for both parameters
	         */
	        mLocationClient = new LocationClient(mContext, this, this);
	        // If a request is not already underway
	        if (!mInProgress) {
	            // Indicate that a request is underway
	            mInProgress = true;
	            // Request a connection from the client to Location Services
	            mLocationClient.connect();
	        } else {
	            /*
	             * A request is already underway. You can handle
	             * this situation by disconnecting the client,
	             * re-setting the flag, and then re-trying the
	             * request.
	             */
	        }
	    }
	    
	@Override
	public void onAddGeofencesResult(int statusCode, String[] geofenceRequestIds) {
		
		//  determine if the addGeofences was successful or not.
		// If adding the geofences was successful
     if (LocationStatusCodes.SUCCESS == statusCode) {
         /*
          * Handle successful addition of geofences here.
          * You can send out a broadcast intent or update the UI.
          * geofences into the Intent's extended data.
          */
     } else {
     // If adding the geofences failed
         /*
          * Report errors here.
          * You can log the error using Log.e() or update
          * the UI.
          */
     }
     // Turn off the in progress flag and disconnect the client
     mInProgress = false;
     mLocationClient.disconnect();
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		// Turn off the request flag
     mInProgress = false;
     /*
      * If the error has a resolution, start a Google Play services
      * activity to resolve it.
      */
     if (connectionResult.hasResolution()) {
         try {
             connectionResult.startResolutionForResult(
            		 mContext,
                     Util.CONNECTION_FAILURE_RESOLUTION_REQUEST);
         } catch (SendIntentException e) {
             // Log the error
             e.printStackTrace();
         }
     // If no resolution is available, display an error dialog
     } else {
         // Get the error code
         int errorCode = connectionResult.getErrorCode();
         // Get the error dialog from Google Play services
         Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
                 errorCode,
                 mContext,
                 Util.CONNECTION_FAILURE_RESOLUTION_REQUEST);
         // If Google Play services can provide an error dialog
         if (errorDialog != null) {
             
         	errorDialog.show();
         }
     }
		
	}

	public void removeGeofences(PendingIntent requestIntent) {
     // Record the type of removal request
     mRequestType = REQUEST_TYPE.REMOVE_INTENT;
     /*
      * Test for Google Play services after setting the request type.
      * If Google Play services isn't present, the request can be
      * restarted.
      */
     if (!Util.servicesConnected(mContext)) {
         return;
     }
     // Store the PendingIntent
     mGeofenceRequestIntent = requestIntent;
     /*
      * Create a new location client object. Since the current
      * activity class implements ConnectionCallbacks and
      * OnConnectionFailedListener, pass the current activity object
      * as the listener for both parameters
      */
     mLocationClient = new LocationClient(mContext, this, this);
     // If a request is not already underway
     if (!mInProgress) {
         // Indicate that a request is underway
         mInProgress = true;
         // Request a connection from the client to Location Services
         mLocationClient.connect();
     } else {
         /*
          * A request is already underway. You can handle
          * this situation by disconnecting the client,
          * re-setting the flag, and then re-trying the
          * request.
          */
     }
 }
	
	public void removeGeofences(List<String> geofenceIds) {
     // If Google Play services is unavailable, exit
     // Record the type of removal request
     mRequestType = REQUEST_TYPE.REMOVE_LIST;
     /*
      * Test for Google Play services after setting the request type.
      * If Google Play services isn't present, the request can be
      * restarted.
      */
     if (!Util.servicesConnected(mContext)) {
         return;
     }
     // Store the list of geofences to remove
     mGeofencesToRemove = geofenceIds;
     /*
      * Create a new location client object. Since the current
      * activity class implements ConnectionCallbacks and
      * OnConnectionFailedListener, pass the current activity object
      * as the listener for both parameters
      */
     mLocationClient = new LocationClient(mContext, this, this);
     // If a request is not already underway
     if (!mInProgress) {
         // Indicate that a request is underway
         mInProgress = true;
         // Request a connection from the client to Location Services
         mLocationClient.connect();
     } else {
         /*
          * A request is already underway. You can handle
          * this situation by disconnecting the client,
          * re-setting the flag, and then re-trying the
          * request.
          */
     }
 }
	
	@Override
	public void onConnected(Bundle arg0) {
		
		//Intent intent = new Intent(mContext,MyService.class);
		//intent.setClassName("com.example.android_hw2", "com.example.android_hw2.MyService");
		//mContext.startActivity(intent);
		
		switch (mRequestType) {
		     case ADD :
		         // Get the PendingIntent for the request
		     	mTransitionPendingIntent =
		                 getTransitionPendingIntent();
		     	
		         // Send a request to add the current geofences
		         mLocationClient.addGeofences(
		         		mCurrentGeofences, mTransitionPendingIntent, this);
		         break;
		     case REMOVE_INTENT :
		         mLocationClient.removeGeofences(
		                 mGeofenceRequestIntent, this);
		         break;
				case REMOVE_LIST:
					mLocationClient.removeGeofences(
		                 mGeofencesToRemove, this);
					break;
				default:
					break;
			}
		
	}

	@Override
	public void onDisconnected() {
		
		// Turn off the request flag
     mInProgress = false;
     // Destroy the current location client
     mLocationClient = null;
	}

	@Override
	public void onRemoveGeofencesByPendingIntentResult(int statusCode,
			PendingIntent pendingIntent) {
		 // If removing the geofences was successful
     if (statusCode == LocationStatusCodes.SUCCESS) {
         /*
          * Handle successful removal of geofences here.
          * You can send out a broadcast intent or update the UI.
          * geofences into the Intent's extended data.
          */
     } else {
     // If adding the geocodes failed
         /*
          * Report errors here.
          * You can log the error using Log.e() or update
          * the UI.
          */
     }
     /*
      * Disconnect the location client regardless of the
      * request status, and indicate that a request is no
      * longer in progress
      */
     mInProgress = false;
     mLocationClient.disconnect();
		
	}

	@Override
	public void onRemoveGeofencesByRequestIdsResult(int statusCode,
			String[] geofenceRequestIds) {
		// If removing the geocodes was successful
     if (LocationStatusCodes.SUCCESS == statusCode) {
         /*
          * Handle successful removal of geofences here.
          * You can send out a broadcast intent or update the UI.
          * geofences into the Intent's extended data.
          */
     } else {
     // If removing the geofences failed
         /*
          * Report errors here.
          * You can log the error using Log.e() or update
          * the UI.
          */
     }
     // Indicate that a request is no longer in progress
     mInProgress = false;
     // Disconnect the location client
     mLocationClient.disconnect();
		
	}
	
}
