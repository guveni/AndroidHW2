package com.example.android_hw2;

import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.ActivityRecognitionClient;

public class ActivityManager implements
ConnectionCallbacks,
OnConnectionFailedListener
 {

	
	/*
     * Store the PendingIntent used to send activity recognition events
     * back to the app
     */
    private PendingIntent mActivityRecognitionPendingIntent;
    // Store the current activity recognition client
    private ActivityRecognitionClient mActivityRecognitionClient;
    
    Activity mContext;
    
 // Flag that indicates if a request is underway.
    private boolean mInProgress;
    
    
    // Constants that define the activity detection interval
	public static final int MILLISECONDS_PER_SECOND = 1000;
	public static final int DETECTION_INTERVAL_SECONDS = 20;
	public static final int DETECTION_INTERVAL_MILLISECONDS =
    MILLISECONDS_PER_SECOND * DETECTION_INTERVAL_SECONDS;
	
	 public enum REQUEST_TYPE {START, STOP}
	    private REQUEST_TYPE mRequestType;
	
	
	public ActivityManager(Activity mCtx) {
		
		
		mContext=mCtx;
		
		// Start with the request flag set to false
        mInProgress = false;
		
		mActivityRecognitionClient = new ActivityRecognitionClient(mContext, this, this);
		
		/*
         * Create the PendingIntent that Location Services uses
         * to send activity recognition updates back to this app.
         */
        Intent intent = new Intent(
                mContext, MyService.class);
        /*
         * Return a PendingIntent that starts the IntentService.
         */
        mActivityRecognitionPendingIntent =
                PendingIntent.getService(mContext, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
	}
	
	
	/**
     * Request activity recognition updates based on the current
     * detection interval.
     *
     */
     public void startUpdates() {
    	 
    	// Set the request type to START
         mRequestType = REQUEST_TYPE.START;
         
        // Check for Google Play services

        if (!Util.servicesConnected(mContext)) {
            return;
        }
        // If a request is not already underway
        if (!mInProgress) {
            // Indicate that a request is in progress
            mInProgress = true;
            // Request a connection to Location Services
            mActivityRecognitionClient.connect();
        //
        } else {
            /*
             * A request is already underway. You can handle
             * this situation by disconnecting the client,
             * re-setting the flag, and then re-trying the
             * request.
             */
        }
    }
     
     /**
      * Turn off activity recognition updates
      *
      */
     public void stopUpdates() {
         // Set the request type to STOP
         mRequestType = REQUEST_TYPE.STOP;
         /*
          * Test for Google Play services after setting the request type.
          * If Google Play services isn't present, the request can be
          * restarted.
          */
         if (!Util.servicesConnected(mContext)) {
             return;
         }
         // If a request is not already underway
         if (!mInProgress) {
             // Indicate that a request is in progress
             mInProgress = true;
             // Request a connection to Location Services
             mActivityRecognitionClient.connect();
         //
         } else {
             /*
              * A request is already underway. You can handle
              * this situation by disconnecting the client,
              * re-setting the flag, and then re-trying the
              * request.
              */
         }
     }
 	


	/*
     * Called by Location Services once the location client is connected.
     *
     * Continue by requesting activity updates.
     */
    @Override
    public void onConnected(Bundle dataBundle) {
    	
    	switch (mRequestType) {
        case START :
        	 /*
             * Request activity recognition updates using the preset
             * detection interval and PendingIntent. This call is
             * synchronous.
             */
            mActivityRecognitionClient.requestActivityUpdates(
                    DETECTION_INTERVAL_MILLISECONDS,
                    mActivityRecognitionPendingIntent);
            break;
		case STOP:
			mActivityRecognitionClient.removeActivityUpdates(
                    mActivityRecognitionPendingIntent);
			break;
		default:
			break;

    }

        /*
         * Since the preceding call is synchronous, turn off the
         * in progress flag and disconnect the client
         */
        mInProgress = false;
        mActivityRecognitionClient.disconnect();
    }

    /*
     * Called by Location Services once the activity recognition
     * client is disconnected.
     */
    @Override
    public void onDisconnected() {
        // Turn off the request flag
        mInProgress = false;
        // Delete the client
        mActivityRecognitionClient = null;
    }
    
 // Implementation of OnConnectionFailedListener.onConnectionFailed
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
                
                // Show the error dialog in the DialogFragment
            	errorDialog.show();
            }
        }
        //...
    }
	
   
}
