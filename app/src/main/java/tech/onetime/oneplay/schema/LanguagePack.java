package tech.onetime.oneplay.schema;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Alexandro on 2016/7/12.
 */
public class LanguagePack implements Serializable{
    public String id = "";
    public String ling = "";
    public String name = "";

    public LanguagePack(String qrString) throws JSONException {
        parseFromQr(qrString);
    }

    public LanguagePack(String id, String ling, String name){
        this.name = name;
        this.ling = ling;
        this.id = id;
    }

    public void parseFromQr(String mainJsonString) throws JSONException {
        JSONObject mainJson = new JSONObject(mainJsonString);

        id = mainJson.getString("id");
        ling = mainJson.getString("ling");
        name = mainJson.getString("name");
    }
}
