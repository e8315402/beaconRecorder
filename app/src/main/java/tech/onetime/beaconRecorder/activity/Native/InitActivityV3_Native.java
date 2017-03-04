package tech.onetime.beaconRecorder.activity.Native;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.androidannotations.api.BackgroundExecutor;

import java.util.LinkedList;
import java.util.Queue;

import tech.onetime.beaconRecorder.R;
import tech.onetime.beaconRecorder.activity.SettingActivity_;
import tech.onetime.beaconRecorder.api.ExcelBuilder;
import tech.onetime.beaconRecorder.ble.BeaconScanCallback;
import tech.onetime.beaconRecorder.schema.BeaconObject;
import tech.onetime.beaconRecorder.schema.SettingState;

/**
 * Created by JianFa on 2017/3/4
 */

public class InitActivityV3_Native extends AppCompatActivity implements BeaconScanCallback.iBeaconScanCallback {

    public final String TAG = "InitActivityV3_Native";

    private BeaconScanCallback _beaconCallback;

    private int _scanTime = 0;

    private Queue<Integer> _scanResultQueue = new LinkedList<>();

    static final int SETTING_REQUEST = 1;
    static final int REQUEST_ENABLE_BT = 1001; // The request code

    Button btn_Scan;
    Button btn_stopScan;
    Button btn_cleanUp;
    Button btn_setting;
    Button btn_storeResult;

    TextView textView_rssi;
    TextView textView_beacon;
    TextView textView_times;
    TextView textView_distance;
    TextView textView_txPower;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init_activity_v3);

        btn_Scan = (Button) findViewById(R.id.startScan);
        btn_stopScan = (Button) findViewById(R.id.stopScan);
        btn_cleanUp = (Button) findViewById(R.id.cleanUp);
        btn_setting = (Button) findViewById(R.id.setting);
        btn_storeResult = (Button) findViewById(R.id.storeResult);

        textView_rssi = (TextView)findViewById(R.id.rssi);
        textView_beacon = (TextView)findViewById(R.id.beacon);
        textView_times = (TextView)findViewById(R.id.times);
        textView_distance = (TextView)findViewById(R.id.distance);
        textView_txPower = (TextView)findViewById(R.id.txPower);

    }

    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState) {

        super.onPostCreate(savedInstanceState);

        btn_Scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d(TAG, "Start scan");

                if (bleInit()) {
                    btn_Scan.setVisibility(View.GONE);
                    btn_setting.setVisibility(View.GONE);
                    btn_cleanUp.setVisibility(View.GONE);

                    btn_stopScan.setVisibility(View.VISIBLE);
                }

            }
        });

        btn_stopScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d(TAG, "Stop scan");

                _beaconCallback.stopScan();

                btn_stopScan.setVisibility(View.GONE);

                btn_Scan.setVisibility(View.VISIBLE);
                btn_cleanUp.setVisibility(View.VISIBLE);

            }
        });

        btn_cleanUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d(TAG, "Clean up");

                textView_times.setText(Integer.toString(_scanTime = 0));
                textView_rssi.setText("00");
                textView_rssi.setTextColor(getResources().getColor(R.color.default_textView_color));

                btn_cleanUp.setVisibility(View.GONE);
                btn_storeResult.setVisibility(View.GONE);

                btn_Scan.setVisibility(View.VISIBLE);
                btn_setting.setVisibility(View.VISIBLE);

                while(!_scanResultQueue.isEmpty()) _scanResultQueue.poll();

            }
        });

        btn_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d(TAG, "Setting");

                Intent intent = new Intent(InitActivityV3_Native.this, SettingActivity_.class);
                startActivityForResult(intent, SETTING_REQUEST);

            }
        });

        btn_storeResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d(TAG, "Store result");

                doSaveResult();

                btn_storeResult.setVisibility(View.GONE);

                btn_cleanUp.performClick();

            }
        });

    }

    void doSaveResult() {

        BackgroundExecutor.execute(new BackgroundExecutor.Task("", 0, "") {
               @java.lang.Override
               public void execute() {
                   try {

                       Log.d(TAG, "Saving result");

                       ExcelBuilder.setCurrentSheet(SettingState.getInstance().get_currentTxPower());

                       ExcelBuilder.setCurrentRowByDistance(SettingState.getInstance().get_currentDistance());

                       while(!_scanResultQueue.isEmpty()) {
                           ExcelBuilder.setCellByRowInOrder(_scanResultQueue.poll());
                       }

                       ExcelBuilder.saveExcelFile(InitActivityV3_Native.this, "temp");

                       ExcelBuilder.setCurrentCell(1);

                       nextState();

                       updateView();

                   } catch (Throwable e) {
                       Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
                   }
               }

           }
        );

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private boolean bleInit() {

        // Use this check to determine whether BLE is supported on the device. Then
        // you can selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "This device does not support bluetooth", Toast.LENGTH_SHORT).show();
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

        return scanBeacon();

    }

    private boolean scanBeacon() {

        BeaconObject currentBeaconObject = SettingState.getInstance().get_currentBeaconObject();

        if(currentBeaconObject == null) {
            Toast.makeText(this, "You did not choose a USBeacon to scan.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (_beaconCallback != null)
            _beaconCallback.stopScan();

        textView_rssi.setTextColor(getResources().getColor(R.color.default_textView_color));

        _beaconCallback = new BeaconScanCallback(this, this);
//        Log.d(TAG, "scanBeacon __ set beacon mac : " + currentBeaconObject.mac);
        _beaconCallback.setScanFilter_address(currentBeaconObject.mac);
        _beaconCallback.startScan();

        return true;

    }

    @Override
    public void scannedBeacons(BeaconObject beaconObject) {

        final int beaconObject_rssi = beaconObject.rssi;

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                _scanResultQueue.offer(beaconObject_rssi);

                textView_rssi.setText(Integer.toString(beaconObject_rssi));

                textView_times.setText(Integer.toString(++_scanTime));

                if (_scanTime == 100) {

                    btn_stopScan.setVisibility(View.GONE);

                    btn_storeResult.setVisibility(View.VISIBLE);
                    btn_cleanUp.setVisibility(View.VISIBLE);

                    textView_rssi.setTextColor(getResources().getColor(R.color.red_500));

                    _beaconCallback.stopScan();

                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case SETTING_REQUEST:

                if(resultCode == RESULT_OK) {

                    updateView();

                }
                if(resultCode == RESULT_CANCELED);

                break;

            case REQUEST_ENABLE_BT:

                if(resultCode == RESULT_OK) {

                    Log.d(TAG, "Enable bluetooth");

                    if(scanBeacon()) {

                        btn_Scan.setVisibility(View.GONE);
                        btn_setting.setVisibility(View.GONE);
                        btn_cleanUp.setVisibility(View.GONE);

                        btn_stopScan.setVisibility(View.VISIBLE);

                    }

                }

                if(resultCode == RESULT_CANCELED) Log.d(TAG, "Unable bluetooth");

                break;
        }

    }


    public void updateView() {

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "updateView");

                textView_rssi.setText("00");

                textView_times.setText(Integer.toString(_scanTime));

                textView_distance.setText(Integer.toString(SettingState.getInstance().get_currentDistance()));

                textView_txPower.setText(SettingState.getInstance().get_currentTxPower());

                if(SettingState.getInstance().get_currentBeaconObject() != null)
                    textView_beacon.setText(SettingState.getInstance().get_currentBeaconObject().getMajorMinorString());
            }
        });

    }

    public void nextState() {

        if(SettingState.getInstance().get_currentDistance() != 50) {

            SettingState.getInstance().set_theNextDistance();
            Log.d(TAG, "nextState __ the next distance : " + Integer.toString(SettingState.getInstance().get_currentDistance()));

        } else {

            SettingState.getInstance().set_theNextTxPower();
            SettingState.getInstance().set_currentDistance(1);

        }

    }

    @Override
    public void onResume() {

        super.onResume();

        Log.d(TAG, "onResume");

        if(textView_times.getText().length() == 0) updateView();

    }

    protected void onDestroy(){

        super.onDestroy();

        if (_beaconCallback != null) _beaconCallback.stopScan();

    }

}
