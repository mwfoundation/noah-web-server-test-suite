package org.meltwater.sessions.sysadm.wsts;

import org.apache.http.client.utils.URIBuilder;

public final class Util {

	public static String getHost() {
		return System.getProperty("test.http.host", "localhost");
	}

	public static int getPort() {
		return Integer.parseInt(System.getProperty("test.http.port", "80"));
	}
	
	public static URIBuilder getUriBuilder() {
		return new URIBuilder()
			.setScheme("http")
			.setHost(getHost())
			.setPort(getPort());
	}

}
