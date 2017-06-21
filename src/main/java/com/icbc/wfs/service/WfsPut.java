package com.icbc.wfs.service;

import java.io.InputStream;

public interface WfsPut {
	boolean put(String name, InputStream in);
}
