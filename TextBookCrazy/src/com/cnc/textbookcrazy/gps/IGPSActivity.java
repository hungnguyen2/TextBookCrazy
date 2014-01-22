package com.cnc.textbookcrazy.gps;

public interface IGPSActivity
{
	public void displayGPSSettingsDialog( );
	
	public void locationChanged( double longitude, double latitude );
}
