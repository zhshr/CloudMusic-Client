package us.acgn.cloudMusicProxyClient.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;
import org.apache.http.message.BasicNameValuePair;

public class HTTP {
	public static String httpPost(String ip, int port, String url, String charset, Map<String, String> headers,
			Map<String, String> params) {
		String finalUrl = "http://" + ip + ":" + port + "/" + url;
		if (charset == null) {
			charset = "UTF-8";
		}
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		if (params != null) {
			for (Map.Entry<String, String> entry : params.entrySet()) {
				nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
			}
		}
		Content content = null;
		HttpResponse response = null;
		try {
			Request req = Request.Post(finalUrl).bodyForm(nvps).viaProxy("127.0.0.1:8888");
			if (headers != null) {
				for (Map.Entry<String, String> entry : headers.entrySet()) {
					req.addHeader(entry.getKey(), entry.getValue());
				}
			}
			content = req.execute().returnContent();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
		String result = content.toString();
		return result;
	}

	public static String httpGet(String ip, int port, String url, String charset, Map<String, String> headers,
			Map<String, String> params) {
		String finalUrl = "http://" + ip + ":" + port + "/" + url;
		if (charset == null) {
			charset = "UTF-8";
		}
//		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
//		if (params != null) {
//			for (Map.Entry<String, String> entry : params.entrySet()) {
//				nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
//			}
//		}
		Content content = null;
		HttpResponse response = null;
		try {
			Request req = Request.Get(finalUrl).viaProxy("127.0.0.1:8888");
			if (headers != null) {
				for (Map.Entry<String, String> entry : headers.entrySet()) {
					req.addHeader(entry.getKey(), entry.getValue());
				}
			}
			content = req.execute().returnContent();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
		String result = content.toString();
		return result;
	}
}
