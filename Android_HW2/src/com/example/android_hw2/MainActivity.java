package com.example.android_hw2;

import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ToggleButton;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationClient.OnAddGeofencesResultListener;
import com.google.android.gms.location.LocationClient.OnRemoveGeofencesResultListener;
import com.google.android.gms.location.LocationStatusCodes;

public class MainActivity extends Activity implements
ConnectionCallbacks,
OnConnectionFailedListener,
OnAddGeofencesResultListener,
OnRemoveGeofencesResultListener{

	GeofenceManager geofenceManager;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		geofenceManager=new GeofenceManager();
		geofenceManager.createGeofences();
		
		mInProgress = false;
		
		// get your ToggleButton
		ToggleButton b = (ToggleButton) findViewById(R.id.toggleButton1);

		// attach an OnClickListener
		b.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton toggleButton,
					boolean isChecked) {
				Context context = getApplicationContext();
				Intent service = new Intent(context, MyService.class);
				if (isChecked) {

					context.startService(service);
				} else {
					context.stopService(service);

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

	// Global constants
	/*
	 * Define a request code to send to Google Play services This code is
	 * returned in Activity.onActivityResult
	 */
	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

	/*
	 * Handle results returned to the FragmentActivity by Google Play services
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// Decide what to do based on the original request code
		switch (requestCode) {

		case CONNECTION_FAILURE_RESOLUTION_REQUEST:
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

	private boolean servicesConnected() {

        // Check that Google Play services is available
        int resultCode =
                GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {

            // In debug mode, log the status
            Log.d("HW2_Anas_Chakfeh", "Google Play services is available.");

            // Continue
            return true;

        // Google Play services was not available for some reason
        } else {

            // Display an error dialog
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode, this, 0);
            if (dialog != null) {	

                dialog.show();
            }
            return false;
        }
    }
	
	 private PendingIntent getTransitionPendingIntent() {
	        // Create an explicit Intent
	        Intent intent = new Intent(this,
	        		MyService.class);//ReceiveTransitionsIntentService
	        /*
	         * Return the PendingIntent
	         */
	        return PendingIntent.getService(
	                this,
	                0,
	                intent,
	                PendingIntent.FLAG_UPDATE_CURRENT);
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
	        if (!servicesConnected()) {
	            return;
	        }
	        /*
	         * Create a new location client object. Since the current
	         * activity class implements ConnectionCallbacks and
	         * OnConnectionFailedListener, pass the current activity object
	         * as the listener for both parameters
	         */
	        mLocationClient = new LocationClient(this, this, this);
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
                        this,
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);
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
                    this,
                    CONNECTION_FAILURE_RESOLUTION_REQUEST);
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
        if (!servicesConnected()) {
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
        mLocationClient = new LocationClient(this, this, this);
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
        if (!servicesConnected()) {
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
        mLocationClient = new LocationClient(this, this, this);
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
		switch (mRequestType) {
        case ADD :
            // Get the PendingIntent for the request
        	mTransitionPendingIntent =
                    getTransitionPendingIntent();
            // Send a request to add the current geofences
            mLocationClient.addGeofences(
            		geofenceManager.mCurrentGeofences, mTransitionPendingIntent, this);
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