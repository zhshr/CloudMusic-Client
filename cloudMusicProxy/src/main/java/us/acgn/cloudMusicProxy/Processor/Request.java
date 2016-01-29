package us.acgn.cloudMusicProxy.Processor;

import io.netty.handler.codec.http.HttpObject;

public class Request extends Processor{

	@Override
	public boolean needProcess(String uri) {
		// TODO Auto-generated method stub
		System.out.print("Request Processor: " + uri + " ");
		return true;
	}

	@Override
	public HttpObject Process(HttpObject obj) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
