package tech.onetime.oneplay.schema;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Alexandro on 2016/7/25.
 */
public class Region {
    public String proximity = "";
    public String identifier = "";
    public String mode = "";

    public Region(String proximity, String identifier, String mode){
        this.proximity = proximity;
        this.identifier = identifier;
        this.mode = mode;
    }

    /**
     * @param qrParam 傳入 qr code 中 region tag
     */
    public void parseFromQr(String qrParam) throws JSONException {
        JSONObject regionJson = new JSONObject(qrParam);
        if(regionJson.has("proximity"))
            proximity = regionJson.getString("proximity");
        if(regionJson.has("identifier"))
            identifier = regionJson.getString("identifier");
        if(regionJson.has("mode"))
            mode = regionJson.getString("mode");
    }

}
