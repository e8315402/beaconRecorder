package tech.onetime.oneplay.schema;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Alexandro on 2016/7/21.
 */
public class VideoObject implements Serializable {
    public String videoId = "";
    public String cinemaId = "";    // SOCKETIO id
    public String name = "...";

    public ArrayList<LanguagePack> languagePacks = new ArrayList<>();

    public VideoObject() {

    }

    public VideoObject(String cinemaId, String name, ArrayList<LanguagePack> languagePacks) {
        this.cinemaId = cinemaId;
        this.name = name;
        this.languagePacks = languagePacks;
    }

    public String getLanguageIdByName(String name) throws NullPointerException {
        for (LanguagePack languagePack : languagePacks) {
            if (name.equals(languagePack.name)) {
                return languagePack.id;
            }
        }
        return null;
    }

    /**
     * 傳入qr code中的cinema tag
     */
    public String parseCinemaId(String cinemaString) throws JSONException {
        return new JSONObject(cinemaString).getString("id");
    }

    /**
     * 在 cinemaApi GET 的 response 中 parse (應用情境 : pull model of PUBLIC|PRIVATE)
     */
    public void parseOfCinemaApi_pull(String cinemaResponse) throws JSONException {

        JSONObject jsonObject = new JSONObject(cinemaResponse);
        JSONObject responseJson = jsonObject.getJSONObject("response");
        JSONObject modelJson = responseJson.getJSONObject(MODEL_fromCinemaApi(false));

        JSONObject movieJson = modelJson.getJSONObject("movie");
        this.cinemaId = modelJson.getString("id");
        this.name = movieJson.getString("name");

        JSONArray soundArr = modelJson.getJSONArray("soundtracks");
        this.languagePacks.clear();
        for (int i = 0; i < soundArr.length(); i++) {
            JSONObject soundJson = soundArr.getJSONObject(i);
            this.languagePacks.add(
                    new LanguagePack(soundJson.getString("id"), soundJson.getString("ling"), soundJson.getString("name")));
        }
    }

    /**
     * 在 cinemaApi GET 的 response 中 parse (應用情境 : push model of PUBLIC|PRIVATE)
     */
    public void parseOfCinemaApi_push(String objString) throws JSONException {

        JSONObject objJson = new JSONObject(objString);

        JSONObject movieJson = objJson.getJSONObject("movie");
        this.cinemaId = objJson.getString("id");
        this.name = movieJson.getString("name");

        JSONArray soundArr = objJson.getJSONArray("soundtracks");
        this.languagePacks.clear();
        for (int i = 0; i < soundArr.length(); i++) {
            JSONObject soundJson = soundArr.getJSONObject(i);
            this.languagePacks.add(
                    new LanguagePack(soundJson.getString("id"), soundJson.getString("ling"), soundJson.getString("name")));
        }
    }

    public String MODEL_fromCinemaApi(boolean multi) {
        if (!multi)
            return "model";
        else return "models";
    }
}
