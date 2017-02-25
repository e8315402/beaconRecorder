package tech.onetime.oneplay.api;

/**
 * Created by Alexandro on 2016/7/26.
 */
public class CinemaApi {

    public static String getApi(String microAppApi, String cinemaId) {
        if (microAppApi != null)
            return "http://" + microAppApi + "/cinemas/" + cinemaId;
        else return GlobalApi.DOMAIN + "/" + cinemaId;
    }

    public static String getApi(String microAppApi) {
        if (microAppApi != null)
            return "http://" + microAppApi + "/cinemas";
        else return GlobalApi.DOMAIN;
    }
}
