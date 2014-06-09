/*
 * Copyright (c) 2014 WiMap.
 *
 * Authorized internal use only.
 * No reproduction or access without express permission of WiMap.
 */

package com.wimap.common;

import java.util.Date;


public class DistanceSample {
    public int router_id;
    public double power;
    public double distance;
    public boolean timestamped;
    public Date time;

    public DistanceSample()
    {
        router_id = -1;
        power = 0;
        distance = 0;
        timestamped = true;
        time = new Date();
    }
}
