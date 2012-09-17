package org.meltwater.sessions.sysadm.wsts;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.http.Header;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.cookie.DateParseException;
import org.apache.http.impl.cookie.DateUtils;
import org.apache.http.message.BasicHeaderElementIterator;
import org.junit.Assert;
import org.junit.Test;

public class RequiredHeadersTests extends HttpClientProvidingTestCase {

	@Test
	public void testOptionsRequestReturnsCorrectAllowHeader() throws URISyntaxException, ClientProtocolException, IOException {
		this.uriBuilder.setPath("/");

		HttpOptions optionsRequest = new HttpOptions(this.uriBuilder.build());
		HttpResponse response = this.client.execute(optionsRequest);

		Assert.assertEquals("Server returns OK response", 200, response.getStatusLine().getStatusCode());
		testCorrectAllowHeader(response);
	}

	@Test
	public void testMethodNotAllowedResponseReturnsCorrectAllowHeader() throws URISyntaxException, ClientProtocolException, IOException {
		this.uriBuilder.setPath("/");

		HttpPost postRequest = new HttpPost(this.uriBuilder.build());
		HttpResponse response = this.client.execute(postRequest);

		Assert.assertEquals("Server returns Method Not Allowed response", 405, response.getStatusLine().getStatusCode());
		testCorrectAllowHeader(response);
	}

	private void testCorrectAllowHeader(HttpResponse response) {
		Set<String> expectedValues = new HashSet<String>(Arrays.asList("GET", "OPTIONS", "HEAD"));
		Set<String> actualValues = new HashSet<String>();
		HeaderElementIterator it = new BasicHeaderElementIterator(response.headerIterator("Allow"));
		while(it.hasNext())
			actualValues.add(it.nextElement().getName());

		Assert.assertEquals("Response contains correct Allow header", expectedValues, actualValues);
	}

	@Test
	public void testDocumentRootHasRequiredHeaders() throws URISyntaxException, ClientProtocolException, IOException, DateParseException {
		this.uriBuilder.setPath("/");
		URI uri = this.uriBuilder.build();

		HttpHead getRequest = new HttpHead(uri);
		HttpResponse response = this.client.execute(getRequest);

		Assert.assertEquals("Server returns OK response", 200, response.getStatusLine().getStatusCode());

		// Server
		Assert.assertEquals("Server returns exactly one Server header", 1,
				response.getHeaders("Server").length);

		// Connection
		Assert.assertEquals("Server returns exactly one Connection header", 1,
				response.getHeaders("Connection").length);

		// Date
		// Must be in RFC 1123-date format
		Header[] dateHeader = response.getHeaders("Date");
		Assert.assertEquals("Server returns exactly one Date header", 1, dateHeader.length);

		String dateHeaderValue = dateHeader[0].getValue();
		DateUtils.parseDate(dateHeaderValue, new String[] { DateUtils.PATTERN_RFC1123 });
	}

}
