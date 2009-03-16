package com.stuffthathappens.games;

public class SensorInfo {
	private final String name;
	private final boolean supported;
	
	public SensorInfo(String name, boolean supported) {
		this.name = name;
		this.supported = supported;
	}

	public String getName() {
		return name;
	}

	public boolean isSupported() {
		return supported;
	}
	
}
