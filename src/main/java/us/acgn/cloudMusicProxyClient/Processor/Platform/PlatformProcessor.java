package us.acgn.cloudMusicProxyClient.Processor.Platform;

import org.json.simple.JSONObject;

import us.acgn.cloudMusicProxyClient.Processor.Response;

public abstract class PlatformProcessor {
	public abstract void init(Response response);
	public abstract void searchAPI(JSONObject json);
	public abstract void playAPI(JSONObject json);
}
