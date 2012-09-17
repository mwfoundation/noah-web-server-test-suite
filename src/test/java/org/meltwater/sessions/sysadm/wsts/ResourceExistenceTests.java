package org.meltwater.sessions.sysadm.wsts;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpHead;
import org.junit.Assert;
import org.junit.Test;

public class ResourceExistenceTests extends HttpClientProvidingTestCase {

	@Test
	public void testDocumentRootExists() throws URISyntaxException, ClientProtocolException, IOException {
		testPathExists("/");
	}

	@Test
	public void testDocumentRootIndexHtmlExists() throws URISyntaxException, ClientProtocolException, IOException {
		testPathExists("/index.html");

		this.uriBuilder.addParameter("rock", "on");
		testPathExists("/index.html");
	}

	@Test
	public void testChildExists() throws URISyntaxException, ClientProtocolException, IOException {
		testPathExists("/child");
		testPathExists("/child/");
	}

	@Test
	public void testChildIndexHtmlExists() throws URISyntaxException, ClientProtocolException, IOException {
		testPathExists("/child/index.html");		
	}

	@Test
	public void testChildAboutHtmlExists() throws URISyntaxException, ClientProtocolException, IOException {
		testPathExists("/child/about.html");		
	}

	@Test
	public void testU1Exists() throws URISyntaxException, ClientProtocolException, IOException {
		testPathExists("/~u1/");		
	}

	@Test
	public void testU1IndexHtmlExists() throws URISyntaxException, ClientProtocolException, IOException {
		testPathExists("/~u1/index.html");		
	}

	@Test
	public void testU1TestHtmlExists() throws URISyntaxException, ClientProtocolException, IOException {
		testPathExists("/~u1/test.html");		
	}

	@Test
	public void testDocumentRootNotFoundHtmlDoesNotExist() throws URISyntaxException, ClientProtocolException, IOException {
		testPathDoesNotExist("/not-found.html");

		this.uriBuilder.addParameter("call-me", "maybe");
		testPathDoesNotExist("/not-found.html");
	}

	@Test
	public void testU1ChildDoesNotExist() throws URISyntaxException, ClientProtocolException, IOException {
		testPathDoesNotExist("/~u1/child");
		testPathDoesNotExist("/~u1/child/");
		testPathDoesNotExist("/~u1/child/index.html");
	}

	private void testPathExists(String path) throws URISyntaxException, ClientProtocolException, IOException {
		this.testPathHasStatusCode(path, 200, "OK");
	}

	private void testPathDoesNotExist(String path) throws URISyntaxException, ClientProtocolException, IOException {
		this.testPathHasStatusCode(path, 404, "Not Found");
	}

	private void testPathHasStatusCode(String path, int statusCode, String reasonPhrase) throws URISyntaxException, ClientProtocolException, IOException {
		this.uriBuilder.setPath(path);

		HttpHead headRequest = new HttpHead(this.uriBuilder.build());
		HttpResponse response = this.client.execute(headRequest);

		Assert.assertEquals("Server returns " + reasonPhrase + " response for request to " + path, statusCode, response.getStatusLine().getStatusCode());
	}

}
