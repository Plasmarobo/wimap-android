package com.wimap;

import java.util.List;

public interface ScanListConsumer {
	public void onScanAggrigate(List<BasicResult> l);
}
