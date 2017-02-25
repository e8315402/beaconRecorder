package tech.onetime.oneplay.schema;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Alexandro on 2016/7/19.
 */
public class OnePlayMicroApp implements Serializable {
    public int LOG = 5;
    public int POW = 5;
    public double UPPER = 7;
    public double LOWER = 6;
    //public String UUID = "319245CD-8CB4-4861-BC1B-37A78EE82EF1";
    public String UUID = "f7826da6-4fa2-4e98-8024-bc5b71e0893e";
    public ArrayList<DisplayObject> displayObjects = new ArrayList<>();

    public String cinemaId = null; // pull 才會有 cinema id

    public OnePlayMicroApp(String microAppQrString) throws Exception {
        //defaultPublicServer();

        JSONObject mainJson = new JSONObject(microAppQrString);

        JSONObject headJson = mainJson.getJSONObject("head");
        JSONObject bodyJson = mainJson.getJSONObject("body");

        if (!headJson.getString("service").equals("OnePlay"))
            throw new Exception("not OnePlay");
        if (!headJson.getString("type").equals("exhibition"))
            throw new Exception("not exhibition");

        JSONObject envJson = bodyJson.getJSONObject("env");

        parseUUID(envJson.toString());

        if (bodyJson.has("exhibits")) {   // push
            JSONArray displaysJson = bodyJson.getJSONArray("exhibits");
            displayObjects.clear();
            for (int i = 0; i < displaysJson.length(); i++) {
                JSONObject disJson = displaysJson.getJSONObject(i);
                DisplayObject displayObject = new DisplayObject(disJson.toString(), envJson.toString(), true);
                displayObjects.add(displayObject);
            }
        } else {    // pull
            JSONObject cinemaJson = bodyJson.getJSONObject("cinema");
            cinemaId = cinemaJson.getString("id");

            DisplayObject displayObject = new DisplayObject(null, envJson.toString(), false);
            displayObjects.add(displayObject);
        }

    }

    /**
     * pull mode 會回傳 cinema id，若是push mode則回傳null
     */
    public String getCinemaId() {
        return cinemaId;
    }

    /**
     * pull mode 會回傳 host，若是push mode則回傳null
     */
    public String getPullIp() {
        if (cinemaId != null)
            return displayObjects.get(0).getHost();
        else
            return null;
    }

    /**
     * pull mode 會回傳 是否為private server
     */
    public boolean pullModeIsPrivate() {
        if (cinemaId != null)
            return displayObjects.get(0).isPrivateServer();
        else
            return false;
    }

    /**
     * 取得指定display的ip
     */
    public String getDisplayIP(int x, int y) {
        for (int i = 0; i < displayObjects.size(); i++) {
            if (displayObjects.get(i).isMajorMinorEqual(x, y))
                return displayObjects.get(i).getHost();
        }
        return null;    // no display matched
    }

    /**
     * 取得指定display是public 還是 private
     */
    public boolean isDisplayPrivateIp(int x, int y) {
        for (int i = 0; i < displayObjects.size(); i++) {
            if (displayObjects.get(i).isMajorMinorEqual(x, y))
                return displayObjects.get(i).isPrivateServer();
        }
        return true;    // no display matched
    }

    /**
     * 取得指定display
     */
    public DisplayObject getDisplayObject(int x, int y) {
        for (int i = 0; i < displayObjects.size(); i++) {
            if (displayObjects.get(i).isMajorMinorEqual(x, y))
                return displayObjects.get(i);
        }
        return null;    // no display matched
    }



    /**
     * 取得pull mode display
     */
    public DisplayObject getPullDisplayObject() {
        return displayObjects.get(0);
    }

    /**
     * parse uuid
     */
    public void parseUUID(String envString) throws JSONException {
        if (new JSONObject(envString).has("region"))
            UUID = new JSONObject(envString).getJSONObject("region").getString("proximity");
    }

}
