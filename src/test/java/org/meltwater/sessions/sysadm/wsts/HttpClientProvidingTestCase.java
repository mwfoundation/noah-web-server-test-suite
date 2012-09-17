package org.meltwater.sessions.sysadm.wsts;

import org.apache.http.client.HttpClient;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.After;
import org.junit.Before;

public abstract class HttpClientProvidingTestCase {

	protected HttpClient client = null;
	protected URIBuilder uriBuilder = null;

	@Before
	public void initializeHttpClient() {
		this.client = new DefaultHttpClient();

		this.uriBuilder = Util.getUriBuilder();
	}

	@After
	public void destroyHttpClient() {
		this.client.getConnectionManager().shutdown();
		this.client = null;

		this.uriBuilder = null;
	}

}
