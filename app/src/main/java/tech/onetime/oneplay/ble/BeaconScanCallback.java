package tech.onetime.oneplay.ble;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.os.Build;
//import android.os.ParcelUuid;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

//import tech.onetime.oneplay.schema.BeaconObject;
//import tech.onetime.oneplay.schema.DisplayObject;
//import tech.onetime.oneplay.schema.OnePlayMicroApp;

/**
 * Created by Alexandro on 2016/7/5.
 */

public class BeaconScanCallback implements KitkatScanCallback.iKitkatScanCallback, LollipopScanCallback.iLollipopScanCallback {

    private final String TAG = "OPBeaconScanCallback";

    private iBeaconScanCallback scanCallback;

    private LollipopScanCallback lollipopScanCallback;
    private KitkatScanCallback kitkatLeScanCallback;

    private BluetoothAdapter mBluetoothAdapter;

    private boolean scanning = false;

    public BeaconScanCallback(Context ctx, iBeaconScanCallback scanCallback) {

        this.scanCallback = scanCallback;

        BluetoothManager bm = (BluetoothManager) ctx.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bm.getAdapter();

        int apiVersion = Build.VERSION.SDK_INT;
        if (apiVersion > Build.VERSION_CODES.KITKAT) {
            scan_lollipop();
        } else {
            scan_kitkat();
        }

    }

    public interface iBeaconScanCallback {

        void scannedBeacons(int beaconObject_rssi);

    }

    /**
     * android 4.4 的 scan
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void scan_kitkat() {

        Log.d(TAG, "scan_kitkat");

        kitkatLeScanCallback = new KitkatScanCallback(this);
        mBluetoothAdapter.startLeScan(kitkatLeScanCallback);

        scanning = true;

    }

//    @TargetApi(Build.VERSION_CODES.M)
//    private void scan_lollipop_setting() {
//
//        BluetoothLeScanner scanner = mBluetoothAdapter.getBluetoothLeScanner();
//        List<ScanFilter> scanFilters = new ArrayList<ScanFilter>();
//
//        ScanFilter.Builder filterBuilder = new ScanFilter.Builder();
//        filterBuilder.setDeviceName("USBeacon");
//        scanFilters.add(filterBuilder.build());
//
//        ScanSettings.Builder scanSettingsBuilder = new ScanSettings.Builder();
////        scanSettingsBuilder.setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY);
//        scanSettingsBuilder.setScanMode(ScanSettings.SCAN_MODE_LOW_POWER);
//        scanSettingsBuilder.setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES);
//        lollipopScanCallback = new LollipopScanCallback(this);
//
//        scan_lollipop(scanner, scanFilters, scanSettingsBuilder.build(), lollipopScanCallback);
//
//    }

    /**
     * android 5.0 的 scan
     */
    @TargetApi(Build.VERSION_CODES.M)
    public void scan_lollipop() {

        Log.d(TAG, "scan_lollipop");

        BluetoothLeScanner scanner = mBluetoothAdapter.getBluetoothLeScanner();
        List<ScanFilter> scanFilters = new ArrayList<ScanFilter>();

        ScanFilter.Builder filterBuilder = new ScanFilter.Builder();
        filterBuilder.setDeviceName("USBeacon");
        filterBuilder.setDeviceAddress("C4:BE:84:21:D2:25");
        scanFilters.add(filterBuilder.build());

        ScanSettings.Builder scanSettingsBuilder = new ScanSettings.Builder();
//        scanSettingsBuilder.setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY);
//        scanSettingsBuilder.setScanMode(ScanSettings.SCAN_MODE_LOW_POWER);
        scanSettingsBuilder.setScanMode(ScanSettings.SCAN_MODE_BALANCED);
        scanSettingsBuilder.setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES);
        lollipopScanCallback = new LollipopScanCallback(this);

        scanner.startScan(scanFilters, scanSettingsBuilder.build(), lollipopScanCallback);

        scanning = true;

    }

    /**
     * kitkat - 偵測到 beacon
     *
     * @param beaconObject_rssi is the rssi after bluetooth scanning
     */
    @Override
    public void kitkat_beaconScanned(int beaconObject_rssi) {

            scanCallback.scannedBeacons(beaconObject_rssi);

    }

    /**
     * lollipop - 偵測到 beacon
     *
     * @param beaconObject_rssi is the rssi after bluetooth scanning
     */
    @Override
    public void lollipop_beaconScanned(int beaconObject_rssi) {

        scanCallback.scannedBeacons(beaconObject_rssi);

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

            scanning = false;

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public boolean isScanning() {

        return scanning;

    }

}
