package tech.onetime.oneplay.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class PrefRW {

	public static void write(Context ctx, String identification, String data) {

		SharedPreferences settings = ctx.getSharedPreferences(PRE_ROOT_NAME,
				Context.MODE_PRIVATE);
		Log.d(TAG, "writting:" + data);
		settings.edit().putString(identification, data).commit();
	}

	public static String read(Context ctx, String identification) {
		String defaultString = "";
		SharedPreferences settings = ctx.getSharedPreferences(PRE_ROOT_NAME,
				Context.MODE_PRIVATE);
		String info = settings.getString(identification, defaultString);
		Log.d(TAG, "read:" + info);
		return info;
	}

	private static final String TAG = "RW";
	private static final String PRE_ROOT_NAME = "nRis";
}
