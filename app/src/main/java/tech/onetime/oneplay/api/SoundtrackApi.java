package tech.onetime.oneplay.api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import tech.onetime.oneplay.schema.BeaconObject;
import tech.onetime.oneplay.schema.DisplayObject;
import tech.onetime.oneplay.schema.LanguagePack;
import tech.onetime.oneplay.schema.VideoObject;

/**
 * Created by Alexandro on 2016/7/5.
 */
public class SoundtrackApi {
    public static String playtime_api = GlobalApi.DOMAIN_SOCKETIO;
    private static String SOUNDTRACKS = "/soundtracks";
    public static String api = GlobalApi.DOMAIN + SOUNDTRACKS;

    public static String getApi(String microAppApi) {
        if (microAppApi != null && !"".equals(microAppApi))
            return "http://" + microAppApi + SOUNDTRACKS;
        else return api;
    }

    public static String getSocketIOApi(String microAppApi) {
        if (microAppApi != null && !"".equals(microAppApi))
            return "http://" + microAppApi;
        else return api;
    }

    @Deprecated
    public static HashMap<String, String> single_getRangeHeader(ArrayList<BeaconObject> beacons, String UUID) {
        HashMap<String, String> _headers = new HashMap<String, String>();
        String range = "";
        boolean isFirst = true;
        for (BeaconObject beacon : beacons) {
            String sub = "";
            if (!isFirst)
                sub = ", ";
            isFirst = false;
            range += sub + "major=" + beacon.major + "; minor=" + beacon.minor + "; proximity=" + UUID;
        }
        _headers.put("Range", range);
        return _headers;
    }

    public static HashMap<String, String> getRangeHeader(ArrayList<DisplayObject> displays, String UUID) {
        /*
            get the first obj of arraylist to create range string (the strongest bluetooth signal)
         */
        HashMap<String, String> _headers = new HashMap<String, String>();
        String range = "";
        range = "major=" + displays.get(0).x + "; minor=" + displays.get(0).y + "; proximity=" + UUID;
        _headers.put("Range", range);
        return _headers;
    }

    public static String getSourceLink(String microAppApi, String sourceId) {
        if (microAppApi != null && !"".equals(microAppApi))
            return "http://" + microAppApi + SOUNDTRACKS + "/" + sourceId;
        else return api + "/" + sourceId;
    }

    @Deprecated
    public static String getSourceLink(String sourceId) {
        return api + "/" + sourceId;
    }

    public static ArrayList<VideoObject> parseVideoListObject(String wholeString) throws JSONException {

        JSONObject mainJson = new JSONObject(wholeString);
        JSONObject responseJson = mainJson.getJSONObject("response");
        JSONArray modelArr = responseJson.getJSONArray("models");

        ArrayList<VideoObject> videoObjects = new ArrayList<>();

        for(int i=0; i<modelArr.length(); i++) {
            VideoObject videoObject = new VideoObject();
            videoObject.parseOfCinemaApi_push(modelArr.get(i).toString());
            videoObjects.add(videoObject);
        }
        return videoObjects;
    }

    public static ArrayList<VideoObject> parseSingleVideoObject(String wholeString) throws JSONException {
        JSONObject mainJson = new JSONObject(wholeString);
        JSONObject responseJson = mainJson.getJSONObject("response");
        ArrayList<VideoObject> videoObjects = new ArrayList<>();

        JSONObject modelJson = responseJson.getJSONObject("model");
        VideoObject videoObject = new VideoObject();
        videoObject.parseOfCinemaApi_push(modelJson.toString());
        videoObjects.add(videoObject);

        return videoObjects;
    }

    private static VideoObject parseVideoObject_old(String _responseJson) throws JSONException {
        JSONObject responseJson = new JSONObject(_responseJson);

        String videoId = responseJson.getJSONObject("data").getJSONObject("video").getString("id");
        String videoName = responseJson.getJSONObject("data").getJSONObject("video").getString("name");
        String cinemaId = responseJson.getJSONObject("data").getJSONObject("cinema").getString("id");

        JSONArray modelArr = responseJson.getJSONArray("models");
        ArrayList<LanguagePack> retList = new ArrayList<>();
        for (int i = 0; i < modelArr.length(); i++) {
            JSONObject langJson = modelArr.getJSONObject(i);
            LanguagePack languagePack = new LanguagePack(langJson.toString());
            retList.add(languagePack);
        }

        VideoObject displayObject = new VideoObject(cinemaId, videoName, retList);
        return displayObject;
    }

    @Deprecated
    public static String single_parseSocketIOJoinVideoId(String serverResponse) throws JSONException {
        JSONObject mainJson = new JSONObject(serverResponse);
        return mainJson.getJSONArray("responses").getJSONObject(0).getJSONObject("data").getJSONObject("video").getString("id");
    }

    @Deprecated
    public static String single_parserVideoName(String serverResponse) throws JSONException {
        JSONObject mainJson = new JSONObject(serverResponse);
        return mainJson.getJSONArray("responses").getJSONObject(0).getJSONObject("data").getJSONObject("video").getString("name");
    }

    @Deprecated
    public static ArrayList<LanguagePack> single_parseLanguagePack(String serverResponse) throws JSONException {
        JSONObject mainJson = new JSONObject(serverResponse);
        JSONArray modelArr = mainJson.getJSONArray("responses").getJSONObject(0).getJSONArray("models");
        ArrayList<LanguagePack> retList = new ArrayList<>();
        for (int i = 0; i < modelArr.length(); i++) {
            JSONObject langJson = modelArr.getJSONObject(i);
            LanguagePack languagePack = new LanguagePack(langJson.toString());
            retList.add(languagePack);
        }
        return retList;
    }
}
