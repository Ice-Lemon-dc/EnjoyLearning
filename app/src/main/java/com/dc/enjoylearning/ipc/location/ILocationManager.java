package com.dc.enjoylearning.ipc.location;

import com.dc.ipc.ServiceId;

/**
 * @author Lemon
 */
@ServiceId("LocationManager")
public interface ILocationManager {
    /**
     * 获取信息
     *
     * @return Location
     */
    Location getLocation();
}
