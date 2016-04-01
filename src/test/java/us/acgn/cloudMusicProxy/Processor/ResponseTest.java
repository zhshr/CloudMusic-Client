package us.acgn.cloudMusicProxy.Processor;

import junit.framework.TestCase;
import us.acgn.cloudMusicProxyClient.Processor.Response;

public class ResponseTest extends TestCase {
	private Response res = null;
	protected void setUp() throws Exception {
		super.setUp();
		
	}

	public void testNeedProcess() {
		this.res = new Response("http://music.163.com/eapi/batch");
		assertTrue(res.needProcess());
	}

	public void testProcess() {
		fail("Not yet implemented");
	}

	public void testIsCloudMusicMp3() {
		fail("Not yet implemented");
	}

}
