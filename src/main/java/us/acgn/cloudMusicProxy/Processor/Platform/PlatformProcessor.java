package us.acgn.cloudMusicProxy.Processor.Platform;

import org.json.simple.JSONObject;

import us.acgn.cloudMusicProxy.Processor.Response;

public abstract class PlatformProcessor {
	public abstract void init(Response response);
	public abstract void searchAPI(JSONObject json);
	public abstract void playAPI(JSONObject json);
}
