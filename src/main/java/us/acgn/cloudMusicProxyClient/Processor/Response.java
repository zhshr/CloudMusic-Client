package us.acgn.cloudMusicProxyClient.Processor;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpResponse;
import us.acgn.cloudMusicProxyClient.Logger;
import us.acgn.cloudMusicProxyClient.Logger.Level;
import us.acgn.cloudMusicProxyClient.Processor.Platform.PCProcessor;
import us.acgn.cloudMusicProxyClient.Utils.JSONAccesser;

public class Response extends Processor {
	public interface ProcessingFunction {
		public void process(JSONObject json);
	}

	private Map<String, ProcessingFunction> functionList;
	private List<String> excludeList;

	public Response(String uri) {
		super(uri);
		functionList = new HashMap<String, ProcessingFunction>();
		excludeList = new ArrayList<String>();
		initFunctions();
	}

	/**
	 * Used to bind a processor function with a specific uri
	 * 
	 * @param uri
	 *            String, the uri
	 * @param fun
	 *            ProcessingFunction. Use lambda expression, e.g.
	 *            (json)->modifyPCSearchURL(json)
	 */
	public void bind(String uri, ProcessingFunction fun) {
		functionList.put(uri, fun);
	}

	/**
	 * Add a rule to exclude uri.
	 * 
	 * @param str
	 */
	public void exclude(String str) {
		excludeList.add(str);
	}

	public final String iPadSearchURL = "http://music.163.com/ipad/eapi/search/get";
	public final String androidSearchURL = "http://music.163.com/eapi/v1/search/get";

	/**
	 * Used to bind all processing functions and add all exclusions
	 */
	public void initFunctions() {
		PCProcessor pc = new PCProcessor();
		pc.init(this);

		bind(iPadSearchURL, (json) -> modifyiPadSearchURL(json));
		bind(androidSearchURL, (json) -> modifyAndroidSearchURL(json));
		exclude(".jpg");
	}

	/**
	 * Determine whether a request needs to be processed or not
	 */
	@Override
	public boolean needProcess() {
		boolean flag = false;
		if (functionList.containsKey(uri)) {
			flag = true;
		}
		for (String str : excludeList.toArray(new String[1])) {
			if (uri.contains(str)) {
				flag = false;
				break;
			}
		}
		return flag;
	}

	/**
	 * 1. Parse JSON. 2. dispatch to internalProcess 3. reassemble the response
	 */
	@Override
	public HttpObject Process(HttpObject obj) {
		if (isCloudMusicMp3() && obj instanceof DefaultHttpResponse) {
			modifyHeader(obj);
		} else if (needProcess()) {
			Logger.log(Level.VERBOSE, "Modify Response: " + uri + Logger.newLine + obj.getClass().getName());
			ByteBuf buffer = null;
			try {
				if (this.uri == "http://music.163.com/eapi/batch"){
					Logger.log(Level.VERBOSE, this.uri + " " + obj.getClass().getName());
				}
				buffer = ((FullHttpResponse) obj).content();
			} catch (Exception e) {
				Logger.log(Level.NOTICE,
						"Type cast failure in Process  " + obj.getClass().getName() + Logger.newLine + this.uri);
				//HttpResponse dhr = (HttpResponse)obj;
				
				return obj;
			}

			String str = buffer.toString(Charset.forName("UTF-8"));
			JSONParser parser = new JSONParser();
			JSONObject json = null;
			try {
				json = (JSONObject) parser.parse(str);
				internalProcess(uri, json);
				str = json.toJSONString();
				byte[] buf = str.getBytes(Charset.forName("UTF-8"));
				Logger.log(Level.VERBOSE, "Buffer Capacity   String length   Buffer New Length" + Logger.newLine
				// 15+24(padding)
						+ String.format("%15s%16s%20s", buffer.capacity(), str.length(), buf.length));
				buffer.clear();
				buffer.capacity(buf.length);
				buffer.writeBytes(buf);
				((FullHttpResponse) obj).headers().set("Content-Length", buf.length);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				if (str.contains("Bad Gateway")) {
					Logger.log(Level.VERBOSE, str);
				} else if (str.contains("Gateway Timeout")) {
					Logger.log(Level.VERBOSE, str);
				} else {
					Logger.log(Level.VERBOSE, "JSON Parse Error" + Logger.newLine + str);
					if (Logger.isPrint(Level.DEBUG)) {
						e.printStackTrace();
					}
				}
			}

		} else {

		}
		return obj;
	}

	private void internalProcess(String uri, JSONObject json) {
		// TODO Auto-generated method stub
		if (functionList.containsKey(uri)) {
			ProcessingFunction fun = functionList.get(uri);
			Logger.log(Level.VERBOSE, "Processor Function from list");
			fun.process(json);
		}
	}

	private void modifyAndroidSearchURL(JSONObject json) {
		// TODO Auto-generated method stub
		try {
			Logger.log(Level.DEBUG, "Desktop Search URL code: " + json.get("code").toString());
			JSONAccesser content = new JSONAccesser(json);
			JSONArray songs = content.get("result").get("songs").parseJSONArray();
			long songCount = content.get("result").get("songCount").parseLong();
			Logger.log(Level.DEBUG, "\tSong count: " + songCount);
			for (Object obj : songs) {
				JSONAccesser song = new JSONAccesser((JSONObject) obj);
				String name = song.get("name").parseString();
				String artist = song.get("ar").get(0).get("name").parseString();
				String album = song.get("al").get("name").parseString();
				Logger.log(Level.VERBOSE, "\t" + String.format("%-50s%-40s%-30s", name, artist, album));

				if (song.get("privilege").get("st").parseLong() < 0) {
					Logger.log(Level.VERBOSE, "\t\tUnavailable song");
					song.get("privilege").replace("st", 0);
					song.get("privilege").replace("subp", 1);
					song.get("privilege").replace("cs", true);
					song.get("privilege").replace("sp", 7);
					song.get("privilege").replace("cp", 1);
					song.get("privilege").replace("dl", 320000);
					song.get("privilege").replace("pl", 320000);
					song.replace("st", 0);
				}
			}
		} catch (Exception e) {
			Logger.log(Level.WARNING, "JSON wrong. Full Text:" + Logger.newLine + json.toJSONString());
			e.printStackTrace();
		}
	}

	private void modifyiPadSearchURL(JSONObject json) {
		// TODO Auto-generated method stub
		try {
			Logger.log(Level.DEBUG, "iPad Search URL code: " + json.get("code").toString());
			JSONAccesser content = new JSONAccesser(json);
			JSONArray songs = content.get("result").get("songs").parseJSONArray();
			long songCount = content.get("result").get("songCount").parseLong();
			Logger.log(Level.DEBUG, "\tSong count: " + songCount);
			for (Object obj : songs) {
				JSONAccesser song = new JSONAccesser((JSONObject) obj);
				String name = song.get("name").parseString();
				String artist = song.get("artists").get(0).get("name").parseString();
				String album = song.get("album").get("name").parseString();
				Logger.log(Level.VERBOSE, "\t" + String.format("%-50s%-40s%-30s", name, artist, album));

				if (song.get("status").parseLong() != 1) {
					song.replace("status", 1);
					Logger.log(Level.VERBOSE, "\t\tUnavailable song");
				}
			}
		} catch (Exception e) {
			Logger.log(Level.WARNING, "JSON wrong. Full Text:" + Logger.newLine + json.toJSONString());
			e.printStackTrace();
		}

	}

	// modify the header of overseas mp3
	// e.g. jpg->mp3
	public void modifyHeader(HttpObject obj) {
		if (obj instanceof DefaultHttpResponse) {
			DefaultHttpResponse res = (DefaultHttpResponse) obj;
			Logger.log(Level.VERBOSE, "Modify Header: " + uri + Logger.newLine + res.headers().get("Content-Type")
					+ " -> " + "audio/mpeg;charset=UTF-8");
			res.headers().set("Content-Type", "audio/mpeg;charset=UTF-8");
		}
	}

	public boolean isCloudMusicMp3() {
		return (uri.contains("music.126.net") && uri.contains(".mp3"));
	}

}
