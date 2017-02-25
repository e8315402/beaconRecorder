package tech.onetime.oneplay.connect;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.HashMap;

//import tech.onetime.onechefmodule.utils.Config;
//import tech.onetime.onechefmodule.utils.P;
//import tech.onetime.onechefmodule.utils.PrefRW;

//import tech.onetime.oneplay.utils.UserPref;
import tech.onetime.oneplay.utils.Config;
import tech.onetime.oneplay.utils.PrefRW;

/**
 * Created by Alexandro on 2016/2/2.
 */

/**
 * <pre>
 * 負責處理連線的rest templete，記得絕對不要直接使用，請務必用繼承的方式使用，並實作getAppVersion，把自己app端的version傳過來。
 * </pre>
 **/
public class RestConnect implements iRestConnect {

    public  final static String TAG = "RestConnect";

    public final static String LOG_IDENTIFICATOR = "errorLog";

    protected HashMap<String, String> headers = new HashMap<String, String>();

    public RestConnect(HashMap<String, String> headers) {
        this.headers = headers;
    }

    public RestConnect() {}

//    public RestConnect(Context ctx, boolean auth) {
//        super();
//        if (headers != null)
//            headers.put("Authorization", "Bearer " + UserPref.getJwt(ctx));
//    }

    /**
     * 設定app的版本號 (http header User-agent需要)
     */
    @Override
    public String getAppVersion(Context ctx) {
        return tech.onetime.oneplay.utils.Config.getAppVersionName(ctx);
    }

    public ResponseEntity<String> get(Context ctx, String url, ResponseErrorHandler errorHandler) throws Exception {
        return request("GET", ctx, url, errorHandler, null, null, null);
    }

    public ResponseEntity<String> delete(Context ctx, String url, ResponseErrorHandler errorHandler) throws Exception {
        return request("DELETE", ctx, url, errorHandler, null, null, null);
    }

    public ResponseEntity<String> post(Context ctx, String url, JSONObject jsonBody, ResponseErrorHandler errorHandler) throws Exception {
        return request("POST", ctx, url, errorHandler, jsonBody, null, null);
    }

    public ResponseEntity<String> put(Context ctx, String url, JSONObject jsonBody, ResponseErrorHandler errorHandler) throws Exception {
        return request("PUT", ctx, url, errorHandler, jsonBody, null, null);
    }

    public ResponseEntity<String> patch(Context ctx, String url, JSONObject jsonBody, ResponseErrorHandler errorHandler) throws Exception {
        return request("PATCH", ctx, url, errorHandler, jsonBody, null, null);
    }

    public ResponseEntity<String> request(String methodType, Context ctx, String url, ResponseErrorHandler errorHandler, JSONObject jsonBody
            , String userName, String password) throws Exception {

        Log.d(TAG, " - - - - - - - - - - - - - - - - Http Connection Start - - - - - - - - - - - - - - - - ");
        Log.d(TAG, "REST / " + methodType + " / " + url);

        // Set the Accept header
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.set("Accept", Config.getHeaderAccept());
        requestHeaders.setUserAgent(Config.getHeaderUserAgent(ctx, getAppVersion(ctx)));
        if (headers != null && headers.size() > 0)
            for (String key : headers.keySet()) {
                requestHeaders.set(key, headers.get(key));
                Log.d(TAG, key + ":" + headers.get(key));
            }

        Log.d(TAG, "Accept:" + Config.getHeaderAccept());
        Log.d(TAG, "User-Agent:" + Config.getHeaderUserAgent(ctx, getAppVersion(ctx)));

        // Authorization
        if (userName != null && password != null) {
            Log.d(TAG, "userName:" + userName + ",passwor:" + password);
            String plainCreds = userName + ":" + password;
            byte[] plainCredsBytes = plainCreds.getBytes();
            byte[] base64CredsBytes = Base64.encode(plainCredsBytes, Base64.DEFAULT);
            String base64Creds = new String(base64CredsBytes);
            requestHeaders.add("Authorization", "Basic " + base64Creds);
        }

        // Create a new RestTemplate instance
        RestTemplate restTemplate = new RestTemplate();
        if (errorHandler == null)
            restTemplate.setErrorHandler(new MyResponseErrorHandler()); // 如果不設handler，default handler會吃掉所有error
        else
            restTemplate.setErrorHandler(errorHandler);

        // Make the HTTP request, marshaling the response from JSON to an array of Events
        ResponseEntity<String> responseEntity = null;
        HttpEntity<?> requestEntity = null;

        switch (methodType) {
            case "GET":
                // Add the String message converter
                restTemplate.getMessageConverters().add(new StringHttpMessageConverter(Charset.forName("UTF-8")));
                requestEntity = new HttpEntity<Object>(requestHeaders);
                responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);
                break;

            case "DELTET":
                // Add the String message converter
                restTemplate.getMessageConverters().add(new StringHttpMessageConverter(Charset.forName("UTF-8")));
                requestEntity = new HttpEntity<Object>(requestHeaders);
                responseEntity = restTemplate.exchange(url, HttpMethod.DELETE, requestEntity, String.class);
                break;

            case "POST":
                // Add the String message converter
                restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
                requestEntity = new HttpEntity<String>(jsonBody.toString(), requestHeaders);
                responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
                break;

            case "PUT":
                // Add the String message converter
                restTemplate.getMessageConverters().add(new StringHttpMessageConverter(Charset.forName("UTF-8")));
                requestEntity = new HttpEntity<Object>(jsonBody.toString(), requestHeaders);
                responseEntity = restTemplate.exchange(url, HttpMethod.PUT, requestEntity, String.class);
                break;

            case "PATCH":
                // Add the String message converter
                restTemplate.getMessageConverters().add(new StringHttpMessageConverter(Charset.forName("UTF-8")));
                requestEntity = new HttpEntity<Object>(jsonBody.toString(), requestHeaders);
                responseEntity = restTemplate.exchange(url, HttpMethod.PATCH, requestEntity, String.class);
                break;

        }

        // 若回傳500series則寫入log
        if (responseEntity.getStatusCode().series().equals(HttpStatus.Series.SERVER_ERROR)) {
            String jsonString = responseEntity.getBody();
            if (jsonString == null)
                jsonString = url;
            writeErrorLog(ctx, getServiceIdFromServerResponse(jsonString));
        }

        Log.d(TAG, "status code:" + responseEntity.getStatusCode());
        if (responseEntity.getBody() != null)
            Log.d(TAG, "response body:" + responseEntity.getBody().toString());
        else
            throw new NullPointerException("response body(json) null");

        Log.d(TAG, " - - - - - - - - - - - - - - - - Http Connection End - - - - - - - - - - - - - - - - ");
        return responseEntity;

    }

    /**
     * 把 error code 5 series 存到local
     */
    private void writeErrorLog(Context ctx, String data) {
        try {
            PrefRW.write(ctx, LOG_IDENTIFICATOR, data);
        } catch (Exception e) {
            Log.d(TAG, "write error log failed.");
//            P.print("write error log failed.");
            e.printStackTrace();
        }
    }

    /**
     * 從server回傳的JSON抓取serviceId
     */
    private String getServiceIdFromServerResponse(String jsonString) {
        String serviceId = null;
        try {
            // TODO
        } catch (Exception e) {
            e.printStackTrace();
        }
        return serviceId;
    }

    /**
     * custom error handler，主要是避免RestTemplete自定義handler，造成catch不到error code的問題
     */
    public static class MyResponseErrorHandler implements ResponseErrorHandler {

        @Override
        public void handleError(ClientHttpResponse clienthttpresponse) throws IOException {

//            if (clienthttpresponse.getStatusCode() == HttpStatus.FORBIDDEN) {
//                P.print(HttpStatus.FORBIDDEN + " response. Throwing authentication exception");
//                throw new AuthenticationException();
//            }
            Log.d(TAG, "handle error");
//            P.print("handle error");
        }

        @Override
        public boolean hasError(ClientHttpResponse clienthttpresponse) throws IOException, UnknownHostException {
//            if (clienthttpresponse.getStatusCode() != HttpStatus.OK) {
//                P.print("Status code: " + clienthttpresponse.getStatusCode());
//                P.print("Response" + clienthttpresponse.getStatusText());
//                P.print(clienthttpresponse.getBody());
//
//                if (clienthttpresponse.getStatusCode() == HttpStatus.FORBIDDEN) {
//                    P.print("Call returned a error 403 forbidden resposne ");
//                    return true;
//                }
//            }
            return false;
        }
    }

}

