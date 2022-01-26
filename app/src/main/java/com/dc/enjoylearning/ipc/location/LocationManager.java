package com.dc.enjoylearning.ipc.location;

import com.dc.ipc.ServiceId;

/**
 * @author Lemon
 */
@ServiceId("LocationManager")
public class LocationManager  {

    private static final LocationManager ourInstance = new LocationManager();

    public static LocationManager getDefault() {
        return ourInstance;
    }

    private LocationManager() {
    }

    private Location location;

    public void setLocation(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

}

