package com.witech.wimap;

import java.util.HashMap;
import java.util.List;

public interface AggrigateConsumer {
	public void onScanAggrigate(List<HashMap<String, BasicResult>> l, int aggrigate);
}
