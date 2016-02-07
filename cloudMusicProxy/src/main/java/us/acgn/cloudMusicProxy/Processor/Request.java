package us.acgn.cloudMusicProxy.Processor;

import io.netty.handler.codec.http.HttpObject;

public class Request extends Processor {
	public Request(String uri){
		super(uri);
	}
	@Override
	public boolean needProcess() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public HttpObject Process(HttpObject obj) {
		// TODO Auto-generated method stub
		return null;
	}

}
