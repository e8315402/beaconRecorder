package tech.onetime.oneplay.schema;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Alexandro on 2016/7/20.
 */
public class DisplayObject implements Serializable {

    public String name = "";
    public String id = "";
    public int x = 0, y = 0;
    public double testDistance = 0; // TODO
    public int listId = -1; // display object 在 overflow 顯示時，需要 id 作為 click event
    //
    public String type;
    public String ssid;
    public String pswd;
    private String host = "";
    public String movie="";
    public int port = 0;
    private VideoObject playingVideo;
    public boolean isPush = false;

    public DisplayObject(String displayStr, String envStr, boolean isPush) throws JSONException {
        JSONObject envJson = new JSONObject(envStr);

        this.isPush = isPush;

        if (isPush) {
            JSONObject disJson = new JSONObject(displayStr);
            x = disJson.getInt("x");
            y = disJson.getInt("y");

            String networkStr = disJson.getString("network");
            JSONObject networksJson = envJson.getJSONObject("networks");
            JSONObject networkJson = networksJson.getJSONObject(networkStr);

            movie = disJson.getString("movie"); // movie name
            type = networkJson.getString("type");
            ssid = networkJson.getString("ssid");
            pswd = networkJson.getString("pswd");
            host = networkJson.getString("host");
            port = networkJson.getInt("port");

        } else {
            JSONObject networkJson = envJson.getJSONObject("network");

            type = networkJson.getString("type");
            ssid = networkJson.getString("ssid");
            pswd = networkJson.getString("pswd");
            host = networkJson.getString("host");
            port = networkJson.getInt("port");
        }
    }

    public VideoObject getPlayingVideo() {
        return playingVideo;
    }

    public String getHost(){
        return this.host + ":" + port;
    }

    public Boolean isPrivateServer() {
        if (type.equals("wlan")) {
            return true;
        } else if (type.equals("wwan")) {
            return false;
        }
        return null;
    }

    public void setPlayingVideo(VideoObject _playingVideo) {
        this.playingVideo = _playingVideo;
    }

    public boolean isMajorMinorEqual(int major, int minor) {
        return this.x == major && this.y == minor;
    }

    public String getMajorMinorString() {
        return ObjUtils.getMajorMinorString(x, y);
    }



}
