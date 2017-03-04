package tech.onetime.beaconRecorder.activity;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.TextView;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.LinkedList;
import java.util.Queue;

import tech.onetime.beaconRecorder.R;
import tech.onetime.beaconRecorder.api.ExcelBuilder;
import tech.onetime.beaconRecorder.ble.BeaconScanCallback;
import tech.onetime.beaconRecorder.schema.BeaconObject;
import tech.onetime.beaconRecorder.schema.SettingState;

@EActivity(R.layout.activity_init_activity_v3)
public class InitActivityV3 extends AppCompatActivity implements BeaconScanCallback.iBeaconScanCallback {

    public final String TAG = "InitActivityV3";

    private BeaconScanCallback _beaconCallback;

    private int _scanTime = 0;

    private Queue<Integer> _scanResultQueue = new LinkedList<>();

    static final int SETTING_REQUEST = 1;
    static final int REQUEST_ENABLE_BT = 1001; // The request code

    @ViewById Button startScan;
    @ViewById Button stopScan;
    @ViewById Button cleanUp;
    @ViewById Button setting;
    @ViewById Button storeResult;

    @ViewById TextView rssi;
    @ViewById TextView beacon;
    @ViewById TextView times;
    @ViewById TextView distance;
    @ViewById TextView txPower;

    @Click void startScan() {

        Log.d(TAG, "Start scan");

        if (bleInit()) {
            startScan.setVisibility(View.GONE);
            setting.setVisibility(View.GONE);
            cleanUp.setVisibility(View.GONE);

            stopScan.setVisibility(View.VISIBLE);
        }

    }

    @Click void stopScan() {

        Log.d(TAG, "Stop scan");

        _beaconCallback.stopScan();

        stopScan.setVisibility(View.GONE);

        startScan.setVisibility(View.VISIBLE);
        cleanUp.setVisibility(View.VISIBLE);

    }

    @Click void cleanUp() {

        Log.d(TAG, "Clean up");

        times.setText(Integer.toString(_scanTime = 0));
        rssi.setText("00");
        rssi.setTextColor(getResources().getColor(R.color.default_textView_color));

        cleanUp.setVisibility(View.GONE);
        storeResult.setVisibility(View.GONE);

        startScan.setVisibility(View.VISIBLE);
        setting.setVisibility(View.VISIBLE);

        while(!_scanResultQueue.isEmpty()) _scanResultQueue.poll();

    }

    @Click void setting() {

        Log.d(TAG, "Setting");

        Intent intent = new Intent(this, SettingActivity_.class);
        startActivityForResult(intent, SETTING_REQUEST);

    }

    @Click(R.id.storeResult)
    void storeResult() {

        Log.d(TAG, "Store result");

        doSaveResult();

        storeResult.setVisibility(View.GONE);

        cleanUp.performClick();

    }

    @Background
    void doSaveResult() {

        Log.d(TAG, "Saving result");

        ExcelBuilder.setCurrentSheet(SettingState.getInstance().get_currentTxPower());

        ExcelBuilder.setCurrentRowByDistance(SettingState.getInstance().get_currentDistance());

        while(!_scanResultQueue.isEmpty()) {
            ExcelBuilder.setCellByRowInOrder(_scanResultQueue.poll());
        }

        ExcelBuilder.saveExcelFile(this, "temp");

        ExcelBuilder.setCurrentCell(1);

        nextState();

        updateView();

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

        rssi.setTextColor(getResources().getColor(R.color.default_textView_color));

        _beaconCallback = new BeaconScanCallback(this, this);
//        Log.d(TAG, "scanBeacon __ set beacon mac : " + currentBeaconObject.mac);
        _beaconCallback.setScanFilter_address(currentBeaconObject.mac);
        _beaconCallback.startScan();

        return true;

    }

    @Override
    @UiThread
    public void scannedBeacons(BeaconObject beaconObject) {

        int beaconObject_rssi = beaconObject.rssi;

        _scanResultQueue.offer(beaconObject_rssi);

        rssi.setText(Integer.toString(beaconObject_rssi));

        times.setText(Integer.toString(++_scanTime));

        if (_scanTime == 100) {

            stopScan.setVisibility(View.GONE);

            storeResult.setVisibility(View.VISIBLE);
            cleanUp.setVisibility(View.VISIBLE);

            rssi.setTextColor(getResources().getColor(R.color.red_500));

            _beaconCallback.stopScan();

        }

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

                        startScan.setVisibility(View.GONE);
                        setting.setVisibility(View.GONE);
                        cleanUp.setVisibility(View.GONE);

                        stopScan.setVisibility(View.VISIBLE);

                    }

                }

                if(resultCode == RESULT_CANCELED) Log.d(TAG, "Unable bluetooth");

                break;
        }

    }

    @UiThread
    public void updateView() {

        Log.d(TAG, "updateView");

        rssi.setText("00");

        times.setText(Integer.toString(_scanTime));

        distance.setText(Integer.toString(SettingState.getInstance().get_currentDistance()));

        txPower.setText(SettingState.getInstance().get_currentTxPower());

        if(SettingState.getInstance().get_currentBeaconObject() != null)
            beacon.setText(SettingState.getInstance().get_currentBeaconObject().getMajorMinorString());

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

        if(times.getText().length() == 0) updateView();

    }

    protected void onDestroy(){

        super.onDestroy();

        if (_beaconCallback != null) _beaconCallback.stopScan();

    }

}
