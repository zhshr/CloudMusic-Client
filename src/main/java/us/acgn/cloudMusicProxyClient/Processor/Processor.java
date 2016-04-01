package us.acgn.cloudMusicProxyClient.Processor;

import io.netty.handler.codec.http.HttpObject;

public abstract class Processor {
	protected String uri;
	public Processor(String uri){
		this.uri = uri;
	}
	abstract public boolean needProcess();

	abstract public HttpObject Process(HttpObject obj);
}
