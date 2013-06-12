package com.example.hw2_anas_chakfeh;

import java.util.ArrayList;

import com.google.android.gms.location.Geofence;

public class GeofenceManager {

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
    
    public GeofenceManager()
    {
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
	
}
