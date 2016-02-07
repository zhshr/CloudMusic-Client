package us.acgn.cloudMusicProxy.Processor;

import java.nio.charset.Charset;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpObject;
import us.acgn.cloudMusicProxy.Logger;
import us.acgn.cloudMusicProxy.Logger.Level;

public class Response extends Processor {
	public Response(String uri) {
		super(uri);
	}

	@Override
	public boolean needProcess() {
		// TODO Auto-generated method stub
		if (uri.contains("music.163.com/eapi/")) {
			return true;
		}
		return false;
	}

	public final String PCSearchURL = "http://music.163.com/eapi/batch";

	@Override
	public HttpObject Process(HttpObject obj) {
		// TODO Auto-generated method stub
		if (isCloudMusicMp3() && obj instanceof DefaultHttpResponse) {
			modifyHeader(obj);
			Logger.log(Level.VERBOSE, "Modify Header: " + uri);
		} else if (needProcess()) {
			Logger.log(Level.VERBOSE, "Modify Response: " + uri);
			ByteBuf buffer = ((FullHttpResponse) obj).content();
			String str = buffer.toString(Charset.forName("UTF-8"));
			JSONParser parser = new JSONParser();
			JSONObject json = null;
			try {
				json = (JSONObject) parser.parse(str);
				internalProcess(uri, json);
				if (uri.contains(PCSearchURL)) {
					modifyPCSearchURL(json);
				}
				str = json.toJSONString();
				byte[] buf = str.getBytes(Charset.forName("UTF-8"));
				Logger.log(Level.VERBOSE, "Buffer Capacity   String length   Buffer New Length" + Logger.newLine
						//15+24(padding)
						+ String.format("%15s%16s%20s", buffer.capacity(), str.length(), buf.length));
				buffer.clear();
				buffer.capacity(buf.length);
				buffer.writeBytes(buf);
				((FullHttpResponse) obj).headers().set("Content-Length", buf.length);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				Logger.log(Level.VERBOSE, str);
				if (Logger.isPrint(Level.DEBUG)){
					e.printStackTrace();
				}
			}

		} else {

		}
		return obj;
	}

	private void internalProcess(String uri, JSONObject json) {
		// TODO Auto-generated method stub
		
	}

	private void modifyPCSearchURL(JSONObject json) {
		// TODO Auto-generated method stub

	}

	private void modifyHeader(HttpObject obj) {

	}

	private boolean isCloudMusicMp3() {
		return (uri.contains("music.126.net") && uri.contains(".mp3"));
	}

}
