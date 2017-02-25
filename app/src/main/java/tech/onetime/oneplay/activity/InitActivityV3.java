package tech.onetime.oneplay.activity;

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

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import tech.onetime.oneplay.R;
//import tech.onetime.oneplay.api.excelBuilder;
import tech.onetime.oneplay.ble.BeaconScanCallback;


@EActivity(R.layout.activity_init_activity_v3)
public class InitActivityV3 extends AppCompatActivity implements BeaconScanCallback.iBeaconScanCallback {


    public final String TAG = "InitActivityV3";

    private BeaconScanCallback _beaconCallback;

    private int _scanTime = 0;
    private int _currentDistance = 1;
    private String _currentTxPower = "";

    static final int PICK_DISTANCE_REQUEST = 1;  // The request code
    static final int PICK_TXPOWER_REQUEST = 2;
    static final int REQUEST_ENABLE_BT = 1001;

    @ViewById(R.id.startScan)
    Button btn_Scan;
    @ViewById(R.id.stopScan)
    Button btn_stopScan;
    @ViewById(R.id.cleanUp)
    Button btn_cleanUp;
    @ViewById(R.id.setting)
    Button btn_setting;
    @ViewById(R.id.chooseDistance)
    Button btn_chooseDistance;
    @ViewById(R.id.chooseTxPower)
    Button btn_chooseTxPower;
    @ViewById(R.id.storeResult)
    Button btn_storeResult;

    @ViewById(R.id.rssi)
    TextView textView_rssi;
    @ViewById(R.id.times)
    TextView textView_times;
    @ViewById(R.id.distance)
    TextView textView_distance;

    @Click(R.id.startScan)
    void startScan() {

        Log.d(TAG, "Start scan");

        if (bleInit()) {
            btn_Scan.setVisibility(View.GONE);
            btn_setting.setVisibility(View.GONE);
            btn_cleanUp.setVisibility(View.GONE);

            btn_stopScan.setVisibility(View.VISIBLE);
//            excelBuilder.createNewSheet(_txPower);
        }

    }

    @Click(R.id.stopScan)
    void stopScan() {

        Log.d(TAG, "Stop scan");

        _beaconCallback.stopScan();

        btn_stopScan.setVisibility(View.GONE);

        btn_Scan.setVisibility(View.VISIBLE);
        btn_cleanUp.setVisibility(View.VISIBLE);

    }


    @Click(R.id.cleanUp)
    void cleanUp() {

        Log.d(TAG, "Clean up");

        textView_times.setText(Integer.toString(_scanTime = 0));
        textView_rssi.setText("00");
        textView_rssi.setTextColor(getResources().getColor(R.color.default_textView_color));

        btn_cleanUp.setVisibility(View.GONE);
        btn_storeResult.setVisibility(View.GONE);

        btn_Scan.setVisibility(View.VISIBLE);
        btn_setting.setVisibility(View.VISIBLE);

    }

    @Click(R.id.setting)
    void setting() {

        Log.d(TAG, "Setting");

        btn_setting.setVisibility(View.GONE);
        btn_Scan.setVisibility(View.GONE);

        btn_chooseDistance.setVisibility(View.VISIBLE);
        btn_chooseTxPower.setVisibility(View.VISIBLE);

    }

    @Click(R.id.chooseDistance)
    void chooseDistance() {

        Log.d(TAG, "Choose distance");

        btn_chooseDistance.setVisibility(View.GONE);
        btn_chooseTxPower.setVisibility(View.GONE);

        btn_setting.setVisibility(View.VISIBLE);
        btn_Scan.setVisibility(View.VISIBLE);

        Intent intent = new Intent(this, ChooseDistanceActivity_.class);
        startActivityForResult(intent, PICK_DISTANCE_REQUEST);

    }

    @Click(R.id.chooseTxPower)
    void chooseTxPower() {

        Log.d(TAG, "Choose txPower");

        btn_chooseDistance.setVisibility(View.GONE);
        btn_chooseTxPower.setVisibility(View.GONE);

        btn_setting.setVisibility(View.VISIBLE);
        btn_Scan.setVisibility(View.VISIBLE);

        Intent intent = new Intent(this, ChooseTxPowerActivity_.class);
        startActivityForResult(intent, PICK_TXPOWER_REQUEST);

    }

    @Click(R.id.storeResult)
    void storeResult() {

        Log.d(TAG, "Store result");

        btn_storeResult.setVisibility(View.GONE);

//        btn_cleanUp.callOnClick();
        btn_cleanUp.performClick();

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

        scanBeacon();

        return true;

    }

    private void scanBeacon() {

        if (_beaconCallback != null)
            _beaconCallback.stopScan();

        textView_rssi.setTextColor(getResources().getColor(R.color.default_textView_color));

        _beaconCallback = new BeaconScanCallback(this, this);

    }



    @Override
    @UiThread
    public void scannedBeacons(int beaconObject_rssi) {

        textView_rssi.setText(Integer.toString(beaconObject_rssi));

//        if(_scanTime == 0) excelBuilder.setCellByRowInOrder(beaconObject_rssi);
//        excelBuilder.setCellByRowInOrder(beaconObject_rssi);

        textView_times.setText(Integer.toString(++_scanTime));

        if (_scanTime != 100) {
//            btn_reScan.setVisibility(View.GONE);
//            excelBuilder.nextRow();
            return;
        }

        btn_stopScan.setVisibility(View.GONE);

        btn_storeResult.setVisibility(View.VISIBLE);
        btn_cleanUp.setVisibility(View.VISIBLE);

        textView_rssi.setTextColor(getResources().getColor(R.color.red_500));

        _beaconCallback.stopScan();

    }

    public void saveScanResult() {
        //     TODO
}

    @OnActivityResult(PICK_DISTANCE_REQUEST)
    void onResult_distance(int resultCode, Intent data) {

        if (resultCode == RESULT_OK && data != null) {

            _currentDistance = data.getExtras().getInt("distance");

            Log.d(TAG, "Chose distance : " + Integer.toString(_currentDistance));

            textView_distance.setText(Integer.toString(_currentDistance));

            textView_rssi.setTextColor(getResources().getColor(R.color.amber_700));

            textView_rssi.setText(Integer.toString(_currentDistance));

        }

    }

    @OnActivityResult(PICK_TXPOWER_REQUEST)
    void onResult_txPower(int resultCode, Intent data) {

        if (resultCode == RESULT_OK && data != null) {

            _currentTxPower = data.getExtras().getString("txPower");

            Log.d(TAG, "Chose txPower : " + _currentTxPower);

            textView_rssi.setTextColor(getResources().getColor(R.color.light_green_600));

            textView_rssi.setText(_currentTxPower);

        }

    }

    @OnActivityResult(REQUEST_ENABLE_BT)
    void onResult_enableBT(int resultCode) {

        if(resultCode == RESULT_OK) {

            Log.d(TAG, "Enable bluetooth");

            scanBeacon();

            btn_Scan.setVisibility(View.GONE);
            btn_setting.setVisibility(View.GONE);
            btn_cleanUp.setVisibility(View.GONE);

            btn_stopScan.setVisibility(View.VISIBLE);

        }

        if(resultCode == RESULT_CANCELED) Log.d(TAG, "Unable bluetooth");

    }

    @Override
    public void onResume() {

        super.onResume();

        if (textView_rssi.getText().length() == 0)
            textView_rssi.setText("00");

        textView_times.setText(Integer.toString(_scanTime));

        if (textView_distance.getText().length() == 0)
            textView_distance.setText(Integer.toString(_currentDistance));

    }

    protected void onPause(){
        super.onPause();
    }

    protected void onDestroy(){
        super.onDestroy();
        if (_beaconCallback != null) {
//            if(!excelBuilder.isFileSaved()) excelBuilder.saveExcelFile(this, "temp.xls");
            _beaconCallback.stopScan();
        }
    }

}
