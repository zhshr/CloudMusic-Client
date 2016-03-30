package us.acgn.cloudMusicProxy.Processor;

import org.littleshoot.proxy.HttpFiltersAdapter;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.DefaultLastHttpContent;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import us.acgn.cloudMusicProxy.Logger;
import us.acgn.cloudMusicProxy.Logger.Level;

public class Filter extends HttpFiltersAdapter {

	Request req;
	Response res;
	boolean needBuffer = true;

	public Filter(HttpRequest originalRequest) {
		super(originalRequest);
		// TODO Auto-generated constructor stub
		req = new Request(originalRequest.getUri());
		res = new Response(originalRequest.getUri());
		if (!req.needProcess() && !res.needProcess()) {
			// if it is not a Netease API, skip it and do not process
			// Exception: mp3 from Netease, need to modify headers but here set
			// to no
			needBuffer = false;
		}
	}

	@Override
	public void proxyToServerConnectionSucceeded(ChannelHandlerContext serverCtx) {
		if (!needBuffer) {
			Logger.log(Level.DEBUG, "Buffering is Disabled " + originalRequest.getUri());
			ChannelPipeline pipeline = serverCtx.pipeline();
			if (pipeline.get("inflater") != null) {
				pipeline.remove("inflater");
			}
			if (pipeline.get("aggregator") != null) {
				pipeline.remove("aggregator");
			}
			super.proxyToServerConnectionSucceeded(serverCtx);
		}
	}

	// @Override
	// public HttpResponse clientToProxyRequest(HttpObject httpObject) {
	// Logger.log(Level.VERBOSE, "clientToProxyRequest - to -> " +
	// originalRequest.getUri());
	// return null;
	// }

	// int STPCount = 0;
	//
	// @Override
	// public HttpObject serverToProxyResponse(HttpObject httpObject) {
	// STPCount++;
	// if (needBuffer || httpObject instanceof DefaultLastHttpContent) {
	// Logger.log(Level.VERBOSE, "serverToProxyResponse[" + String.format("%5d",
	// STPCount) + "] <- from - "
	// + originalRequest.getUri() + " " + httpObject.getClass().getName());
	// }
	//
	// return res.Process(httpObject);
	// }

	
	
	int PTCCount = 0;
	long PTCsize = 0;
	int PTCCode = 0;
	String PTCPhrase = "";
	@Override
	public HttpObject proxyToClientResponse(HttpObject httpObject) {
		PTCCount++;
		if (httpObject instanceof DefaultHttpResponse) {
			try{
				PTCsize = HttpHeaders.getContentLength((HttpMessage) httpObject);
				PTCCode = ((DefaultHttpResponse)httpObject).getStatus().code();
				PTCPhrase = ((DefaultHttpResponse)httpObject).getStatus().reasonPhrase();
			}catch(Exception e){
				Logger.log(Level.VERBOSE, "Content-Length retrival failed");
			}
			
		}
		if (needBuffer || httpObject instanceof DefaultLastHttpContent) {
			if (!(httpObject instanceof DefaultLastHttpContent)){
				FullHttpResponse resp = (FullHttpResponse) httpObject;
				PTCsize = resp.content().capacity();
				PTCCode =  resp.getStatus().code();
				PTCPhrase = resp.getStatus().reasonPhrase();
			}
			Logger.log(Level.VERBOSE,
					"proxyToClientResponse[" + String.format("%5d", PTCCount) + " parts " + PTCsize + "bytes] ["
							+ PTCCode + " " + PTCPhrase + "]" + Logger.newLine
							+ "\tFrom:\t" + originalRequest.getUri() + Logger.newLine + "\tClass: \t"
							+ httpObject.getClass().getName());
		}
		return res.Process(httpObject);
	}

	boolean firstSTPPrinted = false;

	// @Override
	// public void serverToProxyResponseReceiving() {
	// if (needBuffer || !firstSTPPrinted) {
	// Logger.log(Level.VERBOSE, "serverToProxyResponseReceiving");
	// firstSTPPrinted = true;
	// }
	// }
	//
	// @Override
	// public void serverToProxyResponseReceived() {
	// if (needBuffer || !firstSTPPrinted) {
	// Logger.log(Level.VERBOSE, "serverToProxyResponseReceived");
	// }
	// }

}
