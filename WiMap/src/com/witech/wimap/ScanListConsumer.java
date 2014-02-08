package com.witech.wimap;

import java.util.List;

import android.net.wifi.ScanResult;

public interface ScanListConsumer {
	public void onScanResult(List<ScanResult> l);
	public void onScanAggrigate(List<List<ScanResult>> l, int aggrigate);
}
