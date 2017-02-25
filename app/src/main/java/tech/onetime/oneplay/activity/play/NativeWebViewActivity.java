package tech.onetime.oneplay.activity.play;

import android.Manifest;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import org.androidannotations.annotations.AfterExtras;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;

//import tech.onetime.onechefmodule.utils.P;
//import tech.onetime.onechefmodule.utils.PrefRW;
//import tech.onetime.onechefmodule.view.ConnectionErrorDialog;
//import tech.onetime.onechefmodule.view.RisToast;

import tech.onetime.oneplay.R;
import tech.onetime.oneplay.schema.BeaconObject;
import tech.onetime.oneplay.utils.PrefRW;
import tech.onetime.oneplay.api.CinemaApi;
import tech.onetime.oneplay.api.SoundtrackApi;
import tech.onetime.oneplay.ble.BeaconScanCallback;
import tech.onetime.oneplay.connect.RestConnect;
import tech.onetime.oneplay.schema.DisplayObject;
import tech.onetime.oneplay.schema.LanguagePack;
import tech.onetime.oneplay.schema.OnePlayMicroApp;
import tech.onetime.oneplay.schema.VideoObject;
import tech.onetime.oneplay.utils.GenUtils;
import tech.onetime.oneplay.view.LanguageViews;
import tech.onetime.oneplay.view.LanguageRowView;
import tech.onetime.oneplay.view.ConnectionErrorDialog;
import tech.onetime.oneplay.view.RisToast;

@EActivity(R.layout.activity_native_webview)
public class NativeWebViewActivity extends AppCompatActivity implements BeaconScanCallback.iBeaconScanCallback
        , SensorEventListener {

    private final String TAG = "NativeWebViewActivity";

    private final int REQUEST_ENABLE_BT = 1001;
    private final int REQUEST_LOCATION = 1002;
    private final int PERMISSION_REQUEST_INTENT = 1;
    private final String LANGUAGE_RW_ID = "selected_language";

    @ViewById(R.id.ctLayout)
    public LinearLayout ctLayout;
    @ViewById(R.id.progressBar)
    public ProgressBar progressBar;
    @ViewById(R.id.playingName)
    public TextView videoNameTx;
    @ViewById(R.id.playingTime)
    public TextView playingTimeTx;
    public LanguageViews languageListView;
    @ViewById(R.id.webView)
    public WebView webView;
    public SensorManager mySensorManager;
    public Sensor myProximitySensor;
    @ViewById(R.id.SelectVolumn)
    public ImageButton mute_btn;
    @Extra("microApp")
    OnePlayMicroApp microApp;
    private String cinemaId = "-";
    private String socketIOApi = "";
    private String microAppApi = null;
    private boolean isPush = false;
    private BeaconScanCallback beaconCallback;
    private long lastUpdateTime = 0;
    private boolean gettingVideoId = false;
    private String usingDisplayObjectId = null;
    private boolean muteFlag = false;
    @ViewById(R.id.floatingActionButton)
    FloatingActionButton fab;

    private boolean autoDetect = true;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    /**
     * NativeWebViewActivity_ will execute onCreate( ) and injectExtra( ) to set variable
     * onResume() is the first method NativeWebViewActivity called
     */
    @Override
    public void onResume() {
        super.onResume();
        System.out.println("on resume");

//        mySensorManager.registerListener(this, myProximitySensor, SensorManager.SENSOR_DELAY_NORMAL);

        afterOnResume();
    }

    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        if (beaconCallback != null)
            beaconCallback.stopScan();
//        setModeSpeaker();
        mySensorManager.unregisterListener(this);

        if (webView != null) {
            webView.evaluateJavascript("pause()", null);
            webView.evaluateJavascript("releaseSocketIO()", null);
            webView.clearHistory();
            webView.clearCache(true);
            webView.destroy();
            webView = null;
        }
    }

    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }

    @AfterExtras
    public void afterExtra() {
        if (microApp == null) {
            finish();
            return;
        }
        // 若無 cinemaId 則為push，反之則為pull
        isPush = (microApp.cinemaId == null);
    }

    @AfterViews
    public void afterViews() {
        if (mySensorManager == null)
            mySensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (myProximitySensor == null)
            myProximitySensor = mySensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        // Set sound mode
//        setModeCommunication();
    }

    /**
     * This method is called by
     * onResume( ), onActivityResult( ), onRequestPermissionsResult( )
     * and javascript interface: movieChanged( )
     */
    private void afterOnResume() {
        if (microApp == null) {
            System.out.println("micro app must not be null");
            finish();
            return;
        }

        // 設置api        TODO 設置 api 應該在 connection function 內，因 connection function 內有最近的beacon display
        /*if (microApp.getNetworkInfo() != null && !"".equals(microApp.getNetworkInfo().host))
            microAppApi = microApp.getNetworkInfo().getApi();
        socketIOApi = SoundtrackApi.getSocketIOApi(microAppApi);*/

        modeDispatch();
    }

    /**
     * 依據PUSH or PULL執行對應初始
     */
    private void modeDispatch() {
        if (isPush) { // PUSH
            // Get permission of Access_Fine_Location
            // If not granted, request again and back to afterOnResume()
            if (!permissionCheck())
                return;
            // Initialize bluetooth connection
            if (!bleInit())
                return;
        } else { // PULL
            if (microApp.cinemaId == null) {
                finish();
                return;
            }
            cinemaId = microApp.cinemaId;
            Log.d(TAG, "pull mode starting");
            getVideo_start(null, cinemaId, true);
        }
    }

    /**
     * 初始webview。只有在下載 video 包完成後才能執行
     */
    private void initWebView() {

        Log.d(TAG, "initWebView");

        if (webView == null) {
            Log.d(TAG, "initWebView__webView == null");
            return;
        }

        webView.getSettings().setJavaScriptEnabled(true);

        webView.addJavascriptInterface(this, "AndroidFunction");
        webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        webView.loadUrl("file:///android_res/raw/player.html");

    }

    @Click(R.id.SelectVolumn)
    void setMute_btn() {
        if (webView != null) {
            if (!muteFlag) {
                muteFlag = true;
                mute_btn.setImageResource(R.mipmap.ic_mute);

//                    LanguageRowView.animStop();

                webView.loadUrl("javascript:mute()");
            } else {
                muteFlag = false;
                mute_btn.setImageResource(R.mipmap.ic_unmute);
                webView.loadUrl("javascript:unmute()");
                LanguageRowView.animStart();
            }
        }
    }

    //取得展場的所有播放清單。動態的放進清單，第0個為 auto detect。
    int selectedIndex = 0;//single select item index

    @Click(R.id.floatingActionButton)
    void getAudioPlayList() {

        final AlertDialog.Builder dialog_list = new AlertDialog.Builder(this);
        final String playlist[] = new String[microApp.displayObjects.size() + 1];
        playlist[0] = "Auto Detect";//第0個為 auto detect。
        //動態的放進清單
        for (int i = 1; i < microApp.displayObjects.size() + 1; i++)
            playlist[i] = microApp.displayObjects.get(i - 1).movie;

        if (autoDetect) selectedIndex = 0; // autodetect setSingleChoiceItems index = 0
        else selectedIndex = 1;

        dialog_list.setTitle(getString(R.string.floatbtnConfirmTitle)); // play list dialog title
        dialog_list.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Log.d("@which", selectedIndex +"");
                if (selectedIndex > 0) {//手動選取
                    DisplayObject temp;
                    temp = microApp.displayObjects.get(0);//將選取的display object放到第一個
                    microApp.displayObjects.set(0, microApp.displayObjects.get(selectedIndex - 1));
                    microApp.displayObjects.set(selectedIndex - 1, temp);
                    getVideo_start(microApp.displayObjects, null, false);
                } else {//自動選取
                    autoDetect = true;
                    getVideo_start(microApp.displayObjects, null, true);
                }
                Toast.makeText(NativeWebViewActivity.this, getString(R.string.selectIs) + playlist[selectedIndex], Toast.LENGTH_SHORT).show();
            }
        });

        dialog_list.setSingleChoiceItems(playlist, selectedIndex, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                selectedIndex = which;

            }
        });

        dialog_list.show();
    }

    /**
     * 向webView要求撥放指定聲音檔
     */
    @UiThread(delay = 100)
    public void playSoundtrack(String soundtrackId) {

        Log.d(TAG, "playSoundtrack");

        if (webView == null) {
            Log.d(TAG, "playSoundtrack__webView = null");
            return;
        }

        webView.loadUrl("javascript:play('" + SoundtrackApi.getSourceLink(microAppApi, soundtrackId) + "')");
        webView.loadUrl("javascript:unmute()");

        mute_btn.setImageResource(R.mipmap.ic_unmute);
        muteFlag = false;

    }

    /**
     * JS - SocketIO 初始完成
     */
    @JavascriptInterface
    public void init_socketIO_success() {
        Log.d(TAG, "init socketIO success");
        initSocketIOSuccess_uiThread();
    }

    @UiThread
    public void initSocketIOSuccess_uiThread() {
        // 自動撥放
        String usingLanguage = PrefRW.read(this, LANGUAGE_RW_ID);
        mute_btn.setVisibility(View.VISIBLE);
        if (usingLanguage != null && !"".equals(usingLanguage)) {
            if (!languageListView.performSelectedLanguage(usingLanguage))     // 沒有找到符合的語言
                languageListView.performFirstView();
        } else
            languageListView.performFirstView();
    }

    /**
     * JS - 刷新影片時間
     */
    @JavascriptInterface
    public void updateTime(int sec, int duration) {
        updateTime_uiThread(sec, duration);
    }

    @UiThread
    public void updateTime_uiThread(int _sec, int duration) {
//        System.out.println("playing time : " + _sec);
        int mins = _sec / 60;
        int sec = _sec % 60;
        int d_mins = duration / 60;
        int d_sec = duration % 60;
        playingTimeTx.setText(timeFormat(mins) + ":" + timeFormat(sec) + " / " + timeFormat(d_mins) + ":" + timeFormat(d_sec));
    }

    private String timeFormat(int arg) {
        if (arg < 10)
            return "0" + arg;
        else return arg + "";
    }

    /**
     * 聽筒模式
     */
    private void setModeCommunication() {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.setSpeakerphoneOn(false);
        audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
    }

    /**
     * 外撥模式
     */
    private void setModeSpeaker() {
        AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        audioManager.setRingerMode(audioManager.RINGER_MODE_SILENT);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            if (webView != null) {
                if (event.values[0] == 0) {
                    //near
                    webView.loadUrl("javascript:unmute()");
                } else {
                    //far
                    webView.loadUrl("javascript:mute()");
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /**
     * 5.0 檢查藍芽權限
     */
    @TargetApi(Build.VERSION_CODES.M)
    public boolean permissionCheck() {

        int permissionCheck = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        permissionCheck += ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSION_REQUEST_INTENT); //Any number
            return false;
        }
        return true;
    }

    /**
     * bluetooth le 初始
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public boolean bleInit() {
        // Use this check to determine whether BLE is supported on the device. Then
        // you can selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "device not support ble", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Ensures Bluetooth is available on the device and it is enabled. If not,
        // displays a dialog requesting user permission to enable Bluetooth.
        BluetoothManager bm = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        BluetoothAdapter mBluetoothAdapter = bm.getAdapter();
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            return false;
        }

        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (service != null && !service.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                    builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Light_Dialog_Alert);
                else
                    builder = new AlertDialog.Builder(this);

                builder.setTitle(getString(R.string.permission_location_dialog)).setMessage(getString(R.string.permission_location_content));
                builder.setNegativeButton(getString(R.string.permission_location_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                builder.setPositiveButton(getString(R.string.permission_location_enter), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(intent, REQUEST_LOCATION);
                    }
                });
                builder.show();

                return false;
            }
        }

        if (beaconCallback != null)
            beaconCallback.stopScan();

        beaconCallback = new BeaconScanCallback(this, this);

        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_ENABLE_BT:
                    afterOnResume();
                    break;
                case REQUEST_LOCATION:
                    afterOnResume();
                    break;
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @Nullable String[] permissions, @Nullable int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_INTENT)
            afterOnResume();
    }


    /**
     * 由 playing video object 的 language pack 設置 language 選項
     */
    public void setLanguageView(VideoObject playingVideoObject) {
        ctLayout.removeAllViews();
        videoNameTx.setSelected(true);
        videoNameTx.setText(getString(R.string.play_playingName, playingVideoObject.name + ""));
        languageListView = new LanguageViews(this, playingVideoObject.languagePacks, new LanguageViews.iLanguageViews() {
            @Override
            public void languageSelected(LanguagePack languagePack) {

                if (gettingVideoId) {
                    return;
                }
                // 把選定的 name 存入 preference
                PrefRW.write(getApplicationContext(), LANGUAGE_RW_ID, languagePack.name);
                playSoundtrack(languagePack.id);
            }
        });
        ctLayout.addView(languageListView);
    }

    /**
     * 檢查是否連線是否符合 micro app 讀取到的設定 (使用這個method之前必須確定讀取到 micro app)
     */
    private boolean checkConnectionState(boolean isPrivateServer, String ssid, String pwd) {
        // 檢查連線狀態
        if (isPrivateServer) {    // 若是私有雲，則要檢查連線正確
            if (wlanSettingSuccess(ssid)) { // SSID 連線成功
                return true;
            } else { // SSID 連線失敗
                GenUtils.simpleErrorDialog(getSupportFragmentManager(), getString(R.string.ssid_failed_dgTitle)
                        , getString(R.string.ssid_failed_content, ssid, pwd)).setiEvent(new ConnectionErrorDialog.iConfirmDialog() {
                    @Override
                    public void iConfirmDgEvent() {
                        finish();
                    }
                });
                return false;
            }
        } else {
            Log.d(TAG, "public server. need not to check ssid");
            return true;
        }
    }

    /**
     * 若讀取到的micro app是區網，則判斷手機是否連到指定的區網
     */
    private boolean wlanSettingSuccess(String ssid) {
        return GenUtils.wifiConnected(this) && ("" + GenUtils.getUsingSSID(this)).equals(("\"" + ssid + "\""));
    }


    /**
     * one play 取得播放選項演算法
     *
     */
//    @Override
//    public void onePlayGetPlayList(ArrayList<DisplayObject> displayObjects) {
//        onePlayGetPlayListUiThread(displayObjects);
//    }

    @UiThread
    void onePlayGetPlayListUiThread(ArrayList<DisplayObject> _displayObjects) {

        // 5 秒差才能進入 getVideo_start
        long currentUpdateTime = System.currentTimeMillis();
        if (currentUpdateTime - lastUpdateTime < 5000)
            return;
        lastUpdateTime = currentUpdateTime;

        if (gettingVideoId || _displayObjects == null) return;

        getVideo_start(_displayObjects, null, true);
    }

    /**
     * 傳入附近的display，取得要撥放的VideoObject (start)
     *
     * @param cinemaId 如果是 pull model，則不會有 displayObjects, 必須傳cinemaId。如果是push model，則cinemaId可為null。
     */
    @Background
    public void getVideo_start(ArrayList<DisplayObject> displayObjects, String cinemaId, boolean autoUpdate) {

        if (gettingVideoId) return;
        if (!autoUpdate) autoDetect = false;

        if (autoUpdate && !autoDetect) {
            //手動選取後，停止自動更新
            return;
        }

        gettingVideoId = true;
        boolean isPrivate;
        DisplayObject tmp;

        if (isPush) {

            tmp = displayObjects.get(0);
            microAppApi = microApp.getDisplayIP(tmp.x, tmp.y);
            isPrivate = microApp.isDisplayPrivateIp(tmp.x, tmp.y);

//            tmp = microApp.getDisplayObject(displayObject.x, displayObject.y);
//            DisplayObject _dis = displayObject;
//            if (microApp.getDisplayIP(_dis.x, _dis.y) != null) {    // 有 display host
//                microAppApi = microApp.getDisplayIP(_dis.x, _dis.y);
//                isPrivate = microApp.isDisplayPrivateIp(_dis.x, _dis.y);
//                tmp = microApp.getDisplayObject(_dis.x, _dis.y);
//            } else { // 沒 display host
//                System.out.println("尚無設置 display host. 讀取 video object 拒絕.");
//                gettingVideoId = false;
//                return;
//            }

            if (usingDisplayObjectId != null && usingDisplayObjectId.equals(tmp.getMajorMinorString())) {
                gettingVideoId = false;
                return;
            }
            usingDisplayObjectId = tmp.getMajorMinorString();

        } else {    // PULL 尚未實作
            microAppApi = microApp.getPullIp();
            isPrivate = microApp.pullModeIsPrivate();
            tmp = microApp.getPullDisplayObject();
        }

        if (!checkConnectionState(isPrivate, tmp.ssid, tmp.pswd)) {
            return; // 沒有連到指定網域
        }

        socketIOApi = SoundtrackApi.getSocketIOApi(microAppApi);

        ResponseEntity entity;
        try {
            if (isPush) {
                entity = new RestConnect(
                        SoundtrackApi.getRangeHeader(displayObjects, microApp.UUID)).get(this, CinemaApi.getApi(microAppApi), null);
            } else {
                entity = new RestConnect().get(this, CinemaApi.getApi(microAppApi, cinemaId), null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            connectionExceptionUiThread();
            return;
        }
        getVideo_done(entity, autoUpdate, displayObjects);
    }

    @UiThread
    public void connectionExceptionUiThread() {
        RisToast.show(this, getString(R.string.connectionFailed_getting_videoId));
    }

    /**
     * 傳入附近的display，取得要撥放的VideoObject (done)
     *
     * @param playAfterSuccess 完成連線後是否自動撥放
     */
    @UiThread
    public void getVideo_done(ResponseEntity entity, boolean playAfterSuccess, ArrayList<DisplayObject> displayObjects) {
        if (entity != null && entity.getStatusCode() == HttpStatus.OK) {
            try {
                gettingVideoId = false;

                VideoObject playingVideoObject = null;
                if (isPush) {
                    fab.show();
                    ArrayList<VideoObject> videoObjects = SoundtrackApi.parseVideoListObject(entity.getBody().toString());
                    playingVideoObject = videoObjects.get(0);
                } else {
                    ArrayList<VideoObject> videoObjects = SoundtrackApi.parseSingleVideoObject(entity.getBody().toString());
                    playingVideoObject = videoObjects.get(0);
                }

                cinemaId = playingVideoObject.cinemaId;

                initWebView();
                setLanguageView(playingVideoObject);

            } catch (JSONException e) {
                e.printStackTrace();
                RisToast.show(this, getString(R.string.connectionFailed_getting_videoId));
            }
        } else {
            String msg = getString(R.string.connectionFailed_getting_videoId);
            if (!GenUtils.networkEnable(this))
                msg = getString(R.string.no_network);
            RisToast.show(this, msg);
        }

        gettingVideoId = false;
    }

    /**
     * JS - log
     */
    @JavascriptInterface
    public void jsLog(String str) {
        System.out.println("JS:" + str);
    }

    /**
     * JS - movie changed
     */
    @JavascriptInterface
    public void movieChanged() {
        System.out.println("movie changed");
        lastUpdateTime = 0;
        gettingVideoId = false;
        usingDisplayObjectId = null;
        afterOnResume();
    }

    /**
     * JS - 取得 api host
     */
    @JavascriptInterface
    public String getConnectHost() {
        return socketIOApi;
    }

    /**
     * JS - 取得 cinemaId
     */
    @JavascriptInterface
    public String getCinemaId() {
        return cinemaId;
    }

    @Override
    public void scannedBeacons(int beaconObject_rssi) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("NativeWebView Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}
