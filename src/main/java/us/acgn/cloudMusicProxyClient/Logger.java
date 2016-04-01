package us.acgn.cloudMusicProxyClient;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
	public enum Level {
		VERBOSE, DEBUG, INFO, NOTICE, WARNING, ERROR, FATAL;
	}

	public static final String newLine = "\n                        ";
	private static Level currentLevel = Level.INFO;

	public static void setLevel(Level level) {
		currentLevel = level;
	}
	
	public static boolean isPrint(Level level){
		if (level.compareTo(currentLevel) >= 0){
			return true;
		}else{
			return false;
		}
	}

	public static void log(Level level, String str) {
		if (level.compareTo(currentLevel) >= 0) {
			Date date = new Date();
			SimpleDateFormat ft = new SimpleDateFormat("kk:mm:ss zzz");
			System.out.println("[" + String.format("%8s",level.toString()) + " " + ft.format(date) + "]" + " " + str);
		}
	}

	// public final int VERBOSE = 0;
	// public final int DEBUG = 1;
	// public final int INFO = 2;
	// public final int NOTICE = 3;
	// public final int WARNING = 4;
	// public final int ERROR = 5;
	// public final int FATAL = 6;
}
