package us.acgn.cloudMusicProxy;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.littleshoot.proxy.HttpFilters;
import org.littleshoot.proxy.HttpFiltersSourceAdapter;
import org.littleshoot.proxy.HttpProxyServer;
import org.littleshoot.proxy.impl.DefaultHttpProxyServer;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;
import us.acgn.cloudMusicProxy.Logger.Level;
import us.acgn.cloudMusicProxy.Processor.Filter;

/**
 * Hello world!
 *
 */
public class App {
	public static void main(String[] args) throws ClientProtocolException, IOException {
		Logger.setLevel(Level.VERBOSE);
//		Logger.log(Level.VERBOSE, NeteaseAPI.getURL("3397490930293189", true, ".mp3"));
//		Logger.log(Level.VERBOSE, RemoteServer.getdfsID(new String[]{"1","2","3"}, Quality.AsHigh)[0]);
		System.out.println("Hello World!");
		DefaultHttpProxyServer.bootstrap().withPort(9001).withConnectTimeout(5000)
				.withFiltersSource(new HttpFiltersSourceAdapter() {
					public HttpFilters filterRequest(HttpRequest originalRequest, ChannelHandlerContext ctx) {
				//		Logger.log(Level.DEBUG,"Filter Request: " + originalRequest.getUri());
						Filter filter = new Filter(originalRequest);
						return filter;
					}


					@Override
					public int getMaximumResponseBufferSizeInBytes() {
						return 1024*1024*1024;
					}
				}).start();
		System.out.println("After start");
	}
}
