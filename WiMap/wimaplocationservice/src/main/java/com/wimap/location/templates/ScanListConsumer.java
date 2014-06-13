/*
 * Copyright (c) 2014 WiMap.
 *
 * Authorized internal use only.
 * No reproduction or access without express permission of WiMap.
 */

package com.wimap.location.templates;


import com.wimap.location.models.BasicResult;

import java.util.List;

public interface ScanListConsumer {
	public void onScanAggrigate(List<BasicResult> l);
}
