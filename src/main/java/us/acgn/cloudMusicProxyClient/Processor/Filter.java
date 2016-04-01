package us.acgn.cloudMusicProxyClient.Processor;

import org.littleshoot.proxy.HttpFiltersAdapter;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.DefaultHttpContent;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.DefaultLastHttpContent;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.LastHttpContent;
import us.acgn.cloudMusicProxyClient.Logger;
import us.acgn.cloudMusicProxyClient.Logger.Level;

public class Filter extends HttpFiltersAdapter {

	Request req;
	Response res;
	boolean needBuffer = true;

	public Filter(HttpRequest originalRequest) {
		super(originalRequest);
		needBuffer = true;
		// TODO Auto-generated constructor stub
		req = new Request(originalRequest.getUri());
		res = new Response(originalRequest.getUri());
		if (!res.needProcess()) {
			// if it is not a Netease API, skip it and do not process
			// Exception: mp3 from Netease, need to modify headers but here set
			// to no
			// Logger.log(Level.VERBOSE, "Buffering set to Disabled " +
			// originalRequest.getUri());
			needBuffer = false;
		}
	}

	@Override
	public void proxyToServerConnectionSucceeded(ChannelHandlerContext serverCtx) {
		if (!needBuffer) {
			// Logger.log(Level.VERBOSE, "Buffering is Disabled " +
			// originalRequest.getUri());
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

	int PTCCount = 0;
	long PTCsize = 0;
	int PTCCode = 0;
	String PTCPhrase = "";

	@Override
	public HttpObject proxyToClientResponse(HttpObject httpObject) {
		try{
			if (res.isCloudMusicMp3()) {
				if (httpObject instanceof DefaultLastHttpContent) {
					LastHttpContent lhc = (LastHttpContent) httpObject;
					PTCsize += lhc.content().capacity();
					Logger.log(Level.VERBOSE, "proxyToClientResponse[" + String.format("%5d", PTCCount) + " parts "
							+ PTCsize + "bytes] [" + PTCCode + " " + PTCPhrase + "]" + Logger.newLine + "\tFrom:\t"
							+ originalRequest.getUri() + Logger.newLine + "\tClass: \t" + httpObject.getClass().getName());
				}
				if (httpObject instanceof DefaultHttpResponse) {
					HttpResponse hr = (HttpResponse) httpObject;
					PTCsize = 0;
					PTCCode = hr.getStatus().code();
					PTCPhrase = hr.getStatus().reasonPhrase();
					res.modifyHeader(httpObject);
				}
				if (httpObject instanceof DefaultHttpContent){
					HttpContent hc = (HttpContent) httpObject;
					PTCsize += hc.content().capacity();
				}
				return httpObject;
			}
		}catch(Exception e){
			Logger.log(Level.ERROR, "error while processing MP3 chunks");
			e.printStackTrace();
		}
		if (needBuffer) { // APIs
			FullHttpResponse resp = null;
			try {
				resp = (FullHttpResponse) httpObject;
			} catch (Exception e) {
				return httpObject;
			}
			Logger.log(Level.VERBOSE,
					"proxyToClientResponse[" + String.format("%5d", PTCCount) + " parts " + PTCsize + "bytes] ["
							+ PTCCode + " " + PTCPhrase + "]" + Logger.newLine + "\tFrom:\t" + originalRequest.getUri()
							+ Logger.newLine + "\tClass: \t" + httpObject.getClass().getName());
			return res.Process(httpObject);
		}
		return httpObject;
	}

	boolean firstSTPPrinted = false;

}
