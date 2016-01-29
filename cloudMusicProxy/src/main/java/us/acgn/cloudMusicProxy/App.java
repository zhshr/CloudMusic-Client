package us.acgn.cloudMusicProxy;

import org.littleshoot.proxy.HttpFilters;
import org.littleshoot.proxy.HttpFiltersSourceAdapter;
import org.littleshoot.proxy.HttpProxyServer;
import org.littleshoot.proxy.impl.DefaultHttpProxyServer;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;
import us.acgn.cloudMusicProxy.Processor.Filter;

/**
 * Hello world!
 *
 */
public class App {

	public static void main(String[] args) {
		System.out.println("Hello World!");
		HttpProxyServer server = DefaultHttpProxyServer.bootstrap().withPort(9001)
				.withFiltersSource(new HttpFiltersSourceAdapter() {
					public HttpFilters filterRequest(HttpRequest originalRequest, ChannelHandlerContext ctx) {
						System.out.println("Filter Request: " + originalRequest.getUri());
						Filter filter = new Filter(originalRequest);
						return filter;
					}

					// @Override
					// public int getMaximumRequestBufferSizeInBytes() {
					// return 102400;
					// }
					@Override
					public int getMaximumResponseBufferSizeInBytes() {
						return 1024*1024*10;
					}
				}).start();

	}
}
