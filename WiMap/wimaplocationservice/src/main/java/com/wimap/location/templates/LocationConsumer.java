package com.wimap.location.templates;

import com.wimap.common.math.Intersect;

/**
 * Created by Austen on 7/9/2014.
 */
public interface LocationConsumer {
    public void onLocation(Intersect location);
}
