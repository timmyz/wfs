package com.icbc.wfs;

public class InputStreamManager {
	
	private static final InputStreamManager instance = new InputStreamManager();
	
	
	private InputStreamManager(){};
	
	public static InputStreamManager getInstance(){
		return instance;
	}

}
