/*
 * Copyright (c) 2014 WiMap.
 *
 * Authorized internal use only.
 * No reproduction or access without express permission of WiMap.
 */

package com.wimap.api;

import java.util.List;

public class SitesAPI implements HTTPInterface {

    public class Site
    {
        public int id;
        public String name;

        public Site()
        {
            id = 0;
            name = "ERROR";
        }

        public Site(int id, String name)
        {
            this.id = id;
            this.name = name;
        }

    }

    List<Site> cache;

    public SitesAPI()
    {
        cache = null;
    }

    public void PullSites()
    {

    }

    @Override
    public boolean PerformRequest(Integer progress) {
        return false;
    }
}
