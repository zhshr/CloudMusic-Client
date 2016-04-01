package us.acgn.cloudMusicProxyClient.Processor.Platform;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import us.acgn.cloudMusicProxyClient.Logger;
import us.acgn.cloudMusicProxyClient.Logger.Level;
import us.acgn.cloudMusicProxyClient.Processor.Response;
import us.acgn.cloudMusicProxyClient.Utils.JSONAccesser;
import us.acgn.cloudMusicProxyClient.Utils.RemoteServer;
import us.acgn.cloudMusicProxyClient.Utils.NeteaseAPI.Quality;

public class PCProcessor extends PlatformProcessor{
	@Override
	public void init(Response response) {
		// TODO Auto-generated method stub
		response.bind(PCSearchURL, (json) -> searchAPI(json));
		response.bind(PCPlayURL, (json) -> playAPI(json));
		quality = Quality.AsHigh;
	}
	public Quality quality;
	public final String PCSearchURL = "http://music.163.com/eapi/batch";
	public final String PCPlayURL = "http://music.163.com/eapi/song/enhance/player/url";
	@Override
	public void searchAPI(JSONObject json) {
		try {
			JSONAccesser content = new JSONAccesser(json);
			if (content.get("/api/cloudsearch/pc")==null){
				Logger.log(Level.INFO, "Not a Desktop Search URL");
				return;
			}else{
				Logger.log(Level.INFO, "Desktop Search URL code: " + json.get("code").toString());
			}
			JSONArray songs = content.get("/api/cloudsearch/pc").get("result").get("songs").parseJSONArray();
			long songCount = content.get("/api/cloudsearch/pc").get("result").get("songCount").parseLong();
			Logger.log(Level.DEBUG, "\tSong count: " + songCount);
			for (Object obj : songs) {
				JSONAccesser song = new JSONAccesser((JSONObject) obj);
				String name = song.get("name").parseString();
				String artist = song.get("ar").get(0).get("name").parseString();
				String album = song.get("al").get("name").parseString();
				Logger.log(Level.VERBOSE, "\t" + String.format("%-50s%-40s%-30s", name, artist, album));
				if (song.get("privilege").get("st").parseLong() != 0) {
					Logger.log(Level.VERBOSE, "\t\tUnavailable song");
					modifyOneSong(song);
				}
			}
		} catch (Exception e) {
			Logger.log(Level.WARNING, "JSON wrong. Full Text:" + Logger.newLine + json.toJSONString());
			e.printStackTrace();
		}
	}
	private void modifyOneSong(JSONAccesser song){
		JSONAccesser pri = song.get("privilege");
		pri.replace("st", 0);
		pri.replace("subp", 1);
		pri.replace("sp", 7);
		pri.replace("cp", 1);
		pri.replace("fl", 320000);
		pri.replace("dl", 320000);
		pri.replace("pl", 320000);
		song.replace("st", 0);
	}
	@Override
	public void playAPI(JSONObject json) {
		// TODO Auto-generated method stub
		try {
			Logger.log(Level.DEBUG, "Desktop Play URL code: " + json.get("code").toString());
			JSONAccesser song = new JSONAccesser(json).get("data").get(0);
			long songID = song.get("id").parseLong();
			long code = song.get("code").parseLong();
			if (code==200){
				return;
			}
			Logger.log(Level.INFO, "Player API hack, songID: " + songID + "original code: " + code);
			song.replace("code", 200);
			song.replace("type", "mp3");
			song.replace("url", RemoteServer.getSongURL(String.valueOf(songID), quality));
			
		} catch (Exception e) {
			Logger.log(Level.WARNING, "JSON wrong. Full Text:" + Logger.newLine + json.toJSONString());
			e.printStackTrace();
		}
	}
}
