package com.witech.wimap;

import java.util.HashMap;
import java.util.List;

public interface ScanListConsumer {
	public void onScanAggrigate(List<HashMap<String, BasicResult>> l, int aggrigate);
}
