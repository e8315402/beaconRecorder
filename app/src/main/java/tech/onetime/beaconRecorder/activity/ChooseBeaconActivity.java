package tech.onetime.beaconRecorder.activity;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import tech.onetime.beaconRecorder.R;
import tech.onetime.beaconRecorder.ble.BeaconScanCallback;
import tech.onetime.beaconRecorder.schema.BeaconObject;

/**
 * Created by JianFa on 2017/2/26
 */
@EActivity(R.layout.activity_choose_beacon)
public class ChooseBeaconActivity extends AppCompatActivity implements BeaconScanCallback.iBeaconScanCallback{

    private BeaconScanCallback _beaconCallback;
    private ArrayAdapter<BeaconObject> itemsAdapter;
    private List<BeaconObject> _scannedBeacons = new ArrayList<>();

    private static final int REQUEST_ENABLE_BT = 1001;

    @ViewById(R.id.beaconList)
    ListView list_beacon;

    @AfterViews
    void initList() {

        /** Dynamically add item to listView*/
        itemsAdapter = new ArrayAdapter<BeaconObject>(this, android.R.layout.simple_list_item_2, android.R.id.text1, _scannedBeacons) {
            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                TextView text2 = (TextView) view.findViewById(android.R.id.text2);

                text1.setText(_scannedBeacons.get(position).deviceName);
                text2.setText(_scannedBeacons.get(position).getMajorMinorString() + " , " + _scannedBeacons.get(position).mac);
                return view;
            }
        };

        list_beacon.setAdapter(itemsAdapter);
        list_beacon.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                _beaconCallback.stopScan();

                Bundle bundle = new Bundle();
                bundle.putSerializable("beacon", _scannedBeacons.get(position));
                setResult(RESULT_OK, ChooseBeaconActivity.this.getIntent().putExtras(bundle));
                ChooseBeaconActivity.this.finish();

            }
        });
    }

    @ViewById(R.id.chooseBeaconCancel)
    Button btn_chooseBeaconCancel;

    @Click(R.id.chooseBeaconCancel)
    void chooseBeaconCancel() {

        _beaconCallback.stopScan();

        setResult(RESULT_CANCELED, ChooseBeaconActivity.this.getIntent().putExtras(new Bundle()));
        ChooseBeaconActivity.this.finish();

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private boolean bleInit() {

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

    @OnActivityResult(REQUEST_ENABLE_BT)
    void onResult_enableBT(int resultCode) {

        if(resultCode == RESULT_OK) scanBeacon();

        if(resultCode == RESULT_CANCELED) {

            setResult(RESULT_CANCELED, ChooseBeaconActivity.this.getIntent().putExtras(new Bundle()));
            ChooseBeaconActivity.this.finish();

        }

    }

    private void scanBeacon() {

        if (_beaconCallback != null)
            _beaconCallback.stopScan();

        _beaconCallback = new BeaconScanCallback(this, this);

        _beaconCallback.startScan();

    }

    @Override
    public void scannedBeacons(BeaconObject beaconObject) {

        if(isDuplicateBeacon(beaconObject)) return;

        _scannedBeacons.add(beaconObject);

        itemsAdapter.notifyDataSetChanged();

    }

    @Override
    public void onResume() {

        super.onResume();

        // Use this check to determine whether BLE is supported on the device. Then
        // you can selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "This device does not support bluetooth", Toast.LENGTH_SHORT).show();
        } else bleInit();

    }

    @Override
    public void onDestroy() {

        super.onDestroy();

        if(_beaconCallback != null) _beaconCallback.stopScan();

    }

    private boolean isDuplicateBeacon(BeaconObject checkedBeacon) {

        for(BeaconObject ob : _scannedBeacons)
            if(ob.getMajorMinorString().compareTo(checkedBeacon.getMajorMinorString()) == 0)
                return true;

        return false;

    }

}
