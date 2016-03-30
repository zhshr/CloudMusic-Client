package us.acgn.cloudMusicProxy.Utils;
import java.security.*;
import java.util.Random;

import org.apache.commons.codec.binary.Base64;

public class NeteaseAPI {
	public enum Quality{
		AsLow, Low, Medium, High, AsHigh;
	}
	public static String getEncID(String dfsID){
		byte[] magicBytes = "3go8&$8*3*3h0k(2)2".getBytes();
		byte[] songID = dfsID.getBytes();
		for (int i = 0; i < songID.length; i++){
			songID[i] = (byte)(songID[i] ^ magicBytes[i%magicBytes.length]);
		}
		MessageDigest md = null;
		
		try {
			md = MessageDigest.getInstance("MD5");
			byte[] hash = md.digest(songID);
			
			return new String(Base64.encodeBase64(hash, false)).replace('/', '_').replace('+', '-');
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public static String getURL(String dfsID, boolean isForeign, String suffix){
		String result = "";
		Random gen = new Random();
		result = "http://" + (isForeign?"p":"m") + (gen.nextInt(3)+1) + ".music.126.net/" + getEncID(dfsID) + "/" + dfsID + ".mp3"; 
		return result;
	}
}
