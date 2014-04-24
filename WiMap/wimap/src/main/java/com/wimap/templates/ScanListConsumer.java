package com.wimap.templates;

import com.wimap.components.BasicResult;

import java.util.List;

public interface ScanListConsumer {
	public void onScanAggrigate(List<BasicResult> l);
}
