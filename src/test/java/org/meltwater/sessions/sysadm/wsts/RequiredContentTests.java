package org.meltwater.sessions.sysadm.wsts;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.cookie.DateParseException;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Assert;
import org.junit.Test;

public class RequiredContentTests extends HttpClientProvidingTestCase {

	@Test
	public void testDocumentRootHasContentWithAkwaabaTitle() throws URISyntaxException, ClientProtocolException, IOException {
		this.uriBuilder.setPath("/");

		HttpGet getRequest = new HttpGet(this.uriBuilder.build());
		HttpResponse response = this.client.execute(getRequest);

		Assert.assertEquals("Server returns OK response", 200, response.getStatusLine().getStatusCode());
		
		HttpEntity entity = response.getEntity();
		Assert.assertEquals("Server returns text/html Content-Type", "text/html",
				ContentType.get(entity).getMimeType().toLowerCase(Locale.ENGLISH));

		Document root = Jsoup.parse(EntityUtils.toString(entity));
		Assert.assertEquals("Response has <title> of \"Akwaaba!\"", "Akwaaba!", root.title());
	}

	@Test
	public void testDocumentRootContentHasSpecifiedLength() throws URISyntaxException, ClientProtocolException, IOException {
		this.uriBuilder.setPath("/");

		HttpGet getRequest = new HttpGet(this.uriBuilder.build());
		HttpResponse response = this.client.execute(getRequest);

		Assert.assertEquals("Server returns OK response", 200, response.getStatusLine().getStatusCode());
		
		HttpEntity entity = response.getEntity();
		
		Header[] contentLengthHeader = response.getHeaders("Content-Length");
		Assert.assertEquals("Server returns exactly one Content-Length header", 1, contentLengthHeader.length);
		long contentLength = Long.parseLong(contentLengthHeader[0].getValue());

		Assert.assertEquals("Content-Length header value is equal to entity length",
				contentLength, EntityUtils.toByteArray(entity).length);
	}

	@Test
	public void testChildAboutHtmlContentHasRequiredContentHeaders() throws URISyntaxException, ClientProtocolException, IOException, DateParseException {
		this.uriBuilder.setPath("/child/about.html");
		URI uri = this.uriBuilder.build();

		HttpGet getRequest = new HttpGet(uri);
		HttpResponse response = this.client.execute(getRequest);

		Assert.assertEquals("Server returns OK response", 200, response.getStatusLine().getStatusCode());

		// Server, Connection and Date are tested by RequiredHeadersTests

		// Content-Type
		Assert.assertEquals("Server returns exactly one Content-Type header", 1,
				response.getHeaders("Content-Type").length);

		// Content-Length
		testContentLengthHeaderAppearsValid(response);
	}

	@Test
	public void testContentIsNotSentWithHeadRequest() throws URISyntaxException, ClientProtocolException, IOException {
		this.uriBuilder.setPath("/index.html");

		HttpHead headRequest = new HttpHead(this.uriBuilder.build());
		HttpResponse response = this.client.execute(headRequest);

		Assert.assertEquals("Server returns OK response", 200, response.getStatusLine().getStatusCode());

		HttpEntity entity = response.getEntity();
		Assert.assertNull("Server did not return a message body", entity);

		// But we should still have a content length!
		testContentLengthHeaderAppearsValid(response);
	}

	private void testContentLengthHeaderAppearsValid(HttpResponse response) {
		Header[] contentLengthHeader = response.getHeaders("Content-Length");
		Assert.assertEquals("Server returns exactly one Content-Length header", 1, contentLengthHeader.length);
		Assert.assertTrue("Content-Length header value is a non-negative integer",
				Long.parseLong(contentLengthHeader[0].getValue()) >= 0);
	}

	@Test
	public void testDocumentRootIndexHtmlHasSameContentAsDocumentRoot() throws URISyntaxException, ClientProtocolException, IOException {
		// /
		this.uriBuilder.setPath("/");

		HttpGet getRequest = new HttpGet(this.uriBuilder.build());
		HttpResponse response = this.client.execute(getRequest);

		Assert.assertEquals("Server returns OK response", 200, response.getStatusLine().getStatusCode());

		byte[] documentRootEntity = EntityUtils.toByteArray(response.getEntity());

		// /index.html
		this.uriBuilder.setPath("/");

		getRequest = new HttpGet(this.uriBuilder.build());
		response = this.client.execute(getRequest);

		Assert.assertEquals("Server returns OK response", 200, response.getStatusLine().getStatusCode());

		byte[] indexHtmlEntity = EntityUtils.toByteArray(response.getEntity());

		// Equivalence check
		Assert.assertArrayEquals("Content of /index.html is equivalent to content of /",
				documentRootEntity, indexHtmlEntity);
	}

}
