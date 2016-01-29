package us.acgn.cloudMusicProxy.Processor;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.littleshoot.proxy.HttpFiltersAdapter;

import com.google.common.primitives.Bytes;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.DefaultHttpContent;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;

public class Filter extends HttpFiltersAdapter {

	Request req;
	Response res;
	boolean verbose = true;

	public Filter(HttpRequest originalRequest) {
		super(originalRequest);
		// TODO Auto-generated constructor stub
		req = new Request();
		res = new Response();
		if (originalRequest.getUri().contains(".mp3")) {
			verbose = false;
		}
	}

	@Override
	public HttpResponse clientToProxyRequest(HttpObject httpObject) {
		if (verbose) {
			System.out.println("clientToProxyRequest - to -> " + originalRequest.getUri());
		}
		return null;
	}

	@Override
	public HttpObject serverToProxyResponse(HttpObject httpObject) {
		if (verbose) {
			System.out.println("serverToProxyResponse <- from - " + originalRequest.getUri() + "  "
					+ httpObject.getClass().getName());
		}

		return httpObject;
	}

	@Override
	public HttpObject proxyToClientResponse(HttpObject httpObject) {
		if (verbose) {
			System.out.println("proxyToClientResponse <- from - " + originalRequest.getUri() + "  "
					+ httpObject.getClass().getName());
		}
		return httpObject;
	}

	@Override
	public void serverToProxyResponseReceiving() {
		if (verbose) {
			System.out.println("serverToProxyResponseReceiving");
		}
	}

	@Override
	public void serverToProxyResponseReceived() {
		if (verbose) {
			System.out.println("serverToProxyResponseReceived");
		}
	}

}
