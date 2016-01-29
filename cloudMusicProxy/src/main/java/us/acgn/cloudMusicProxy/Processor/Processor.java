package us.acgn.cloudMusicProxy.Processor;

import io.netty.handler.codec.http.HttpObject;

public abstract class Processor {
	abstract public boolean needProcess(String uri);
	abstract public HttpObject Process(HttpObject obj);
}
