package com.icbc.wfs.service;

import java.io.InputStream;

public interface WfsGet {
	InputStream get(String path);
}
