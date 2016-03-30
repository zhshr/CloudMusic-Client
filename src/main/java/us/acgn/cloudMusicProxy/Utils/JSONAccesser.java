package us.acgn.cloudMusicProxy.Utils;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class JSONAccesser {
	private Object obj;
	public JSONAccesser(JSONObject obj){		
		this.obj = obj;
	}
	public JSONAccesser(JSONArray obj){		
		this.obj = obj;
	}
	public JSONAccesser(String str){
		JSONParser parser = new JSONParser();
		JSONObject json = null;
		try {
			json = (JSONObject) parser.parse(str);
			this.obj = json;
		}catch(Exception e){
			
		}
	}
	public JSONAccesser(Object obj){
		this.obj = obj;
	}
	public JSONAccesser get(String str){
		if (obj instanceof JSONArray){
			throw new UnsupportedOperationException();
		}
		if (obj instanceof JSONObject){
			JSONObject o = (JSONObject)obj;
			Object value = o.get(str); 
			if (value == null){
				return null;
			}
			if (value instanceof JSONObject){
				return new JSONAccesser((JSONObject)value);
			}
			if (value instanceof JSONArray){
				return new JSONAccesser((JSONArray)value);
			}
			return new JSONAccesser(value);
		}		
		return null;
	}
	public JSONAccesser get(int i){
		if (obj instanceof JSONObject){
			throw new UnsupportedOperationException();
		}
		if (obj instanceof JSONArray){
			JSONArray o = (JSONArray)obj;
			Object value = o.get(i); 
			if (value == null){
				return null;
			}
			if (value instanceof JSONObject){
				return new JSONAccesser((JSONObject)value);
			}
			if (value instanceof JSONArray){
				return new JSONAccesser((JSONArray)value);
			}
			return new JSONAccesser(value);
		}		
		return null;
	}
	//final return functions
	public Object value(){
		return obj;
	}
	
	public int parseInt(){
		return (Integer)obj;
	}
	public long parseLong(){
		return (Long)obj;
	}
	public String parseString(){
		return (String)obj;
	}
	
	public JSONObject parseJSONObject(){
		return (JSONObject)obj;
	}
	
	public JSONArray parseJSONArray(){
		return (JSONArray)obj;
	}
	
	public void replace(String key, Object value){
		if (!(obj instanceof JSONObject)){
			throw new UnsupportedOperationException();
		}
		JSONObject o = (JSONObject)obj;
		if (o.containsKey(key)){
			o.remove(key);
			o.put(key, value);
		}
	}
}
	