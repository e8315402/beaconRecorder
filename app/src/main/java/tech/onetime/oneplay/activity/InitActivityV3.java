package tech.onetime.oneplay.activity;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.TextView;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import tech.onetime.oneplay.R;
//import tech.onetime.oneplay.api.excelBuilder;
import tech.onetime.oneplay.ble.BeaconScanCallback;


@EActivity(R.layout.activity_init_activity_v3)
public class InitActivityV3 extends AppCompatActivity implements BeaconScanCallback.iBeaconScanCallback {

    public final String TAG = "InitActivityV3";

    private BeaconScanCallback _beaconCallback;

    private short _scanTime = 90;
//    private short _distance = 1;

    private boolean _chooseDistance = false;
    private int _currentTxPowerIndex = 0;

    @ViewById(R.id.startScan)
    Button btn_Scan;
    @ViewById(R.id.reScan)
    Button btn_reScan;
    @ViewById(R.id.chooseDistance)
    Button btn_chooseDistance;
    @ViewById(R.id.chooseTxPower_ok)
    Button btn_chooseDistance_ok;
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

        Log.d(TAG,"startScan");

        btn_chooseDistance.setVisibility(View.GONE);

        if (bleInit()) {
//            excelBuilder.createNewSheet(_txPower);
        }

    }

    @Click(R.id.reScan)
    void reScan() {

        Log.d(TAG, "reScan");

        textView_times.setText(Integer.toString(_scanTime = 0));

        textView_rssi.setText("00");

        textView_rssi.setTextColor(getResources().getColor(R.color.default_textView_color));

        btn_reScan.setVisibility(View.GONE);

    }

    @Click(R.id.chooseDistance)
    void chooseDistance() {

        Intent intent = new Intent(this, ChooseDistanceActivity_.class);
        startActivity(intent);

//        if (!_chooseDistance) {
//
//            btn_chooseDistance.setText("Next");
//
//            _chooseDistance = true;
//
//            btn_Scan.setVisibility(View.INVISIBLE);
//
//            btn_chooseDistance_ok.setVisibility(View.VISIBLE);
//
//        }
//
//        textView_rssi.setTextColor(getResources().getColor(R.color.blue_grey_500));
//
//        if (_currentTxPowerIndex == 4) _currentTxPowerIndex = 0;
//
//        switch (_currentTxPowerIndex) {
//            case 0:
//                textView_rssi.setText(getResources().getString(R.string.txPower_1m));
//                break;
//            case 1:
//                textView_rssi.setText(getResources().getString(R.string.txPower_10m));
//                break;
//            case 2:
//                textView_rssi.setText(getResources().getString(R.string.txPower_20m));
//                break;
//            case 3:
//                textView_rssi.setText(getResources().getString(R.string.txPower_50m));
//                break;
//        }
//
//        _currentTxPowerIndex++;

    }

    @Click(R.id.chooseTxPower_ok)
    void chooseTxPower_ok() {

//        _chooseDistance = false;
//
//        textView_rssi.setTextColor(getResources().getColor(R.color.default_textView_color));
//
//        textView_rssi.setText("00");
//
//        btn_chooseDistance.setText(getResources().getString(R.string.distance));
//
//        btn_Scan.setVisibility(View.VISIBLE);
//
//        btn_chooseDistance_ok.setVisibility(View.GONE);

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public boolean bleInit() {

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
            int REQUEST_ENABLE_BT = 1001;
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            return false;
        }

        if (_beaconCallback != null)
            _beaconCallback.stopScan();

        _beaconCallback = new BeaconScanCallback(this, this);
        return true;

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN && _scanTime != 100 && _beaconCallback != null) {

            if (_beaconCallback.isScanning()) {
                _beaconCallback.stopScan();
                textView_rssi.setTextColor(getResources().getColor(R.color.red_500));
            } else {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    _beaconCallback.scan_lollipop();
                } else _beaconCallback.scan_kitkat();

                textView_rssi.setTextColor(getResources().getColor(R.color.default_textView_color));

            }

        }
        return super.onTouchEvent(event);
    }

    @Override
    @UiThread
    public void scannedBeacons(int beaconObject_rssi) {

        textView_rssi.setText(Integer.toString(beaconObject_rssi));

//        if(_scanTime == 0) excelBuilder.setCellByRowInOrder(beaconObject_rssi);
//        excelBuilder.setCellByRowInOrder(beaconObject_rssi);

        checkScanTime();

    }

    public void checkScanTime() {

        textView_times.setText(Integer.toString(++_scanTime));

        if(_scanTime != 100) {
            btn_reScan.setVisibility(View.GONE);
//            excelBuilder.nextRow();
            return;
        }

        btn_reScan.setVisibility(View.VISIBLE);

        textView_rssi.setTextColor(getResources().getColor(R.color.red_500));

        _beaconCallback.stopScan();

    }

    public void saveScanResult() {
        //     TODO
}

    @Override
    public void onResume() {

        super.onResume();

        textView_rssi.setText("00");

        textView_times.setText(Integer.toString(_scanTime));

//        textView_distance.setText(Integer.toString(_distance));

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
