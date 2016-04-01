package us.acgn.cloudMusicProxyClient.Utils;

import java.util.HashMap;
import java.util.Map;

public class RemoteServer {
	private static String ip = "127.0.0.1";
	private static int port = 1111;
	private static String encodedfsIDs(String[] songIDs) {
		String result = "";
		for (String id : songIDs) {
			result += id + ",";
		}
		return result;
	}
	
	public static String getSongURL(String songID, NeteaseAPI.Quality quality){
		String dfsID = getdfsID(songID, quality);
		//dfsID = "3349112419347723";
		return NeteaseAPI.getURL(dfsID, true, ".mp3");
	}

	public static String getdfsID(String songID, NeteaseAPI.Quality quality) {
		return getdfsIDs(new String[]{songID}, quality)[0];
	}
	
	public static String[] getdfsIDs(String[] songIDs, NeteaseAPI.Quality quality) {
		Map<String, String> params = new HashMap<>();
		params.put("ids", encodedfsIDs(songIDs));
		params.put("quality", quality.toString());
		String temp = HTTP.httpPost(ip, port, "getDFSid", null, null, params);
		return temp.split(",");
		//return new String[]{temp};
	}

	
}
