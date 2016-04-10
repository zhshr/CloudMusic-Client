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
		response.bind(PCSearchURL, (json) -> searchAPI(json));
		response.bind(PCPlayURL, (json) -> playAPI(json));
		response.bind(PCPlaylistURL, (json) -> playlistAPI(json));
		response.bind(PCPlaylistURL2, (json) -> playlistAPI(json));
		quality = Quality.AsHigh;
	}
	public Quality quality;
	public final String PCSearchURL = "http://music.163.com/eapi/batch";
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
					modifyPrivilege(song.get("privilege"));
				}
			}
		} catch (Exception e) {
			Logger.log(Level.WARNING, "JSON wrong. Full Text:" + Logger.newLine + json.toJSONString());
			e.printStackTrace();
		}
	}
	private void modifyPrivilege(JSONAccesser pri){
		long maxbr = pri.get("maxbr").parseLong();
		pri.replace("st", 0);
		pri.replace("subp", 1);
		pri.replace("sp", 7);
		pri.replace("cp", 1);
		pri.replace("fl", maxbr);
		pri.replace("dl", maxbr);
		pri.replace("pl", maxbr);
		//song.replace("st", 0);
	}

	public final String PCPlayURL = "http://music.163.com/eapi/song/enhance/player/url";
	@Override
	public void playAPI(JSONObject json) {
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
	public final String PCPlaylistURL = "http://music.163.com/eapi/v3/song/detail/";
	public final String PCPlaylistURL2 = "http://music.163.com/eapi/v3/playlist/detail";
	@Override
	public void playlistAPI(JSONObject json) {
		Logger.log(Level.INFO, "Desktop Playlist URL code: " + json.get("code").toString());
		JSONAccesser pris = new JSONAccesser(json).get("privileges");
		for (Object temp : pris.parseJSONArray()){
			JSONAccesser pri = new JSONAccesser((JSONObject)temp);
			modifyPrivilege(pri);
		}
	}
}
