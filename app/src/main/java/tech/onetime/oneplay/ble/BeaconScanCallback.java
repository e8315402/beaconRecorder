package tech.onetime.oneplay.ble;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import tech.onetime.oneplay.schema.BeaconObject;


/**
 * Created by Alexandro on 2016/7/5.
 */

public class BeaconScanCallback implements KitkatScanCallback.iKitkatScanCallback, LollipopScanCallback.iLollipopScanCallback {

    private final String TAG = "OPBeaconScanCallback";

    private iBeaconScanCallback scanCallback;

    private LollipopScanCallback lollipopScanCallback;
    private KitkatScanCallback kitkatLeScanCallback;

    private BluetoothAdapter mBluetoothAdapter;
    private List<ScanFilter> _scanFilters;

//    private boolean scanning = false;

    public BeaconScanCallback(Context ctx, iBeaconScanCallback scanCallback) {

        this.scanCallback = scanCallback;

        BluetoothManager bm = (BluetoothManager) ctx.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bm.getAdapter();

        _scanFilters = new ArrayList<>();
//        ScanFilter.Builder filterBuilder = new ScanFilter.Builder();
//        filterBuilder.setDeviceName("USBeacon");
        _scanFilters.add(new ScanFilter.Builder().setDeviceName("USBeacon").build());

    }

    public interface iBeaconScanCallback {

        void scannedBeacons(BeaconObject beaconObject);

    }

    public void startScan() {

        int apiVersion = Build.VERSION.SDK_INT;

        if (apiVersion > Build.VERSION_CODES.KITKAT)
            scan_lollipop();
        else scan_kitkat();

    }

    /**
     * android 4.4
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void scan_kitkat() {

        Log.d(TAG, "scan_kitkat");

        kitkatLeScanCallback = new KitkatScanCallback(this);
        mBluetoothAdapter.startLeScan(kitkatLeScanCallback);

//        scanning = true;

    }

    /**
     * android 5.0
     */
    @TargetApi(Build.VERSION_CODES.M)
    private void scan_lollipop() {

        Log.d(TAG, "scan_lollipop");

        lollipopScanCallback = new LollipopScanCallback(this);

        ScanSettings.Builder scanSettingsBuilder = new ScanSettings.Builder();
        scanSettingsBuilder.setScanMode(ScanSettings.SCAN_MODE_BALANCED);
        scanSettingsBuilder.setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES);
//        scanSettingsBuilder.setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY);
//        scanSettingsBuilder.setScanMode(ScanSettings.SCAN_MODE_LOW_POWER);

        BluetoothLeScanner scanner = mBluetoothAdapter.getBluetoothLeScanner();
        scanner.startScan(_scanFilters, scanSettingsBuilder.build(), lollipopScanCallback);

//        scanning = true;

    }

    @TargetApi(Build.VERSION_CODES.M)
    public void setScanFilter(String deviceAddress) {

        ScanFilter.Builder filterBuilder = new ScanFilter.Builder();
        filterBuilder.setDeviceAddress(deviceAddress);
        _scanFilters.add(filterBuilder.build());

    }


    /**
     * kitkat - 偵測到 beacon
     *
     * @param beaconObject is the object include beacon attribute
     */
    @Override
    public void kitkat_beaconScanned(BeaconObject beaconObject) {

            scanCallback.scannedBeacons(beaconObject);

    }

    /**
     * lollipop - 偵測到 beacon
     *
     * @param beaconObject  is the object include beacon attribute
     */
    @Override
    public void lollipop_beaconScanned(BeaconObject beaconObject) {

        scanCallback.scannedBeacons(beaconObject);

    }

    public void stopScan() {

        Log.d(TAG, "stopScan");

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                BluetoothLeScanner scanner = mBluetoothAdapter.getBluetoothLeScanner();
                scanner.stopScan(lollipopScanCallback);
            } else {
                if (mBluetoothAdapter != null)
                    mBluetoothAdapter.stopLeScan(kitkatLeScanCallback);
                if (kitkatLeScanCallback != null)
                    kitkatLeScanCallback.stopDetect();
            }

//            scanning = false;

        } catch (Exception e) {

            e.printStackTrace();

        }

    }

//    public boolean isScanning() {
//
//        return scanning;
//
//    }

}
