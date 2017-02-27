package tech.onetime.beaconRecorder.utils;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import tech.onetime.beaconRecorder.R;

/**
 * Created by Alexandro on 2016/2/5.
 */
public class Config {

    public static final String TAG = "Config";

    /**
     * 取得app的version
     * */
    public static String getAppVersionName(Context ctx) {
        return ctx.getString(R.string.versionName);
    }

    /**
     * 取得http header Accept
     */
    public static String getHeaderAccept() {
        return "application/json; " + usingApiVersion();
    }

    /**
     * 取得http header User-Agent
     */
    public static String getHeaderUserAgent(Context ctx, String appVersion) {
        return "OnePlay/" + appVersion
                + " (android " + Config.getAndroidReleaseVersion() + "; "
                + Config.getDeviceAndroidId(ctx) + ")";
    }

    /**
     * 取得App使用的 server api version
     */
    private static String getAndroidReleaseVersion() {
        return Build.VERSION.RELEASE;
    }

    /**
     * 取得App使用的 server api version
     */
    private static String usingApiVersion() {
        return "version=1";
    }

    /**
     * 回傳device的android sid
     *
     * @link http://developer.android.com/intl/zh-tw/reference/android/provider/Settings.Secure.html#ANDROID_ID
     */
    private static String getDeviceAndroidId(Context ctx) {
        try {
            return RIS_androidId(Settings.Secure.getString(ctx.getContentResolver(), Settings.Secure.ANDROID_ID));
        }catch(NoSuchAlgorithmException e){
//            P.print("parse android id to RIS format exception: no method SHA1");
            Log.d(TAG, "parse android id to RIS format exception: no method SHA1");
            return Settings.Secure.getString(ctx.getContentResolver(), Settings.Secure.ANDROID_ID);
        }
    }

    /**
     * @param androidId android 取得的 Id。
     * @return 把android id轉為RIS格式。
     * */
    private static String RIS_androidId(String androidId) throws NoSuchAlgorithmException {

        int length = androidId.length()/2;

        byte bytes[] = new byte[length];

        for (int i = 0; i < length; i++) {

            bytes[i] = (byte) Integer.parseInt(androidId.substring(i*2, i*2+2), 16);

        }

        byte[] result = MessageDigest.getInstance("SHA1").digest(bytes);
        StringBuffer string_buffer = new StringBuffer();
        for (int i = 0; i < result.length; i++) {
            string_buffer.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
        }

        String hex = string_buffer.toString();
        String time_low = hex.substring(0, 8);
        String time_mid = hex.substring(8, 12);
        String time_hi_and_version = toHex((byte)(result[6] & 0x0f | 0x50)) + hex.substring(14, 16);
        String clk_seq_hi_res = toHex((byte)(result[8] & 0x3f | 0x80));
        String clk_seq_low = hex.substring(18, 20);
        String node = hex.substring(20, 32);
        String uuid = new String(time_low + '-' + time_mid + '-' + time_hi_and_version + '-' + clk_seq_hi_res + clk_seq_low + '-' + node);

        return uuid.toUpperCase();
    }

    private static String toHex(byte b) {
        String hex_char_set = "0123456789ABCDEF";
        return new StringBuilder().append(hex_char_set.charAt(0xf & b >> 4)).append(hex_char_set.charAt(b & 0xf)).toString();

    }

}
