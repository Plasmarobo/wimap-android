/*
 * Copyright (c) 2014 WiMap.
 *
 * Authorized internal use only.
 * No reproduction or access without express permission of WiMap.
 */

package com.wimap.templates;

import com.wimap.components.BasicResult;

import java.util.List;

public interface ScanListConsumer {
	public void onScanAggrigate(List<BasicResult> l);
}
