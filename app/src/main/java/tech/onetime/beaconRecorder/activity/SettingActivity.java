package tech.onetime.beaconRecorder.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;

import tech.onetime.beaconRecorder.R;
import tech.onetime.beaconRecorder.schema.BeaconObject;
import tech.onetime.beaconRecorder.schema.SettingState;

/**
 * Created by JianFa on 2017/2/26
 */

@EActivity(R.layout.activity_setting)
public class SettingActivity extends AppCompatActivity{

    private static final String TAG = "SettingActivity";

    private int _currentDistanceTemp;
    private String _currentTxPowerTemp = null;
    private BeaconObject _currentBeaconTemp = null;
    private String _fileNameTemp = null;

    static final int PICK_DISTANCE_REQUEST = 1;
    static final int PICK_TXPOWER_REQUEST = 2;
    static final int PICK_BEACON_REQUEST = 3;

    @ViewById(R.id.chooseDistance)
    Button btn_chooseDistance;

    @Click(R.id.chooseDistance)
    void chooseDistance() {

        Log.d(TAG, "Choose distance");

        Intent intent = new Intent(this, ChooseDistanceActivity_.class);
        startActivityForResult(intent, PICK_DISTANCE_REQUEST);

    }

    @OnActivityResult(PICK_DISTANCE_REQUEST)
    void onResult_distance(int resultCode, Intent data) {

        if (resultCode == RESULT_OK && data != null) {

            _currentDistanceTemp = data.getExtras().getInt("distance");

            Log.d(TAG, "Chose distance : " + Integer.toString(_currentDistanceTemp));

        }

    }

    @ViewById(R.id.chooseTxPower)
    Button btn_chooseTxPower;

    @Click(R.id.chooseTxPower)
    void chooseTxPower() {

        Log.d(TAG, "Choose txPower");

        Intent intent = new Intent(this, ChooseTxPowerActivity_.class);
        startActivityForResult(intent, PICK_TXPOWER_REQUEST);

    }


    @OnActivityResult(PICK_TXPOWER_REQUEST)
    void onResult_txPower(int resultCode, Intent data) {

        if (resultCode == RESULT_OK && data != null) {

            _currentTxPowerTemp = data.getExtras().getString("txPower");

            Log.d(TAG, "Chose txPower : " + _currentTxPowerTemp);

        }

    }

    @ViewById(R.id.chooseBeacon)
    Button btn_chooseBeacon;

    @Click(R.id.chooseBeacon)
    void chooseBeacon() {

        Log.d(TAG, "Choose beacon");

        Intent intent = new Intent(this, ChooseBeaconActivity_.class);
        startActivityForResult(intent, PICK_BEACON_REQUEST);

    }

    @OnActivityResult(PICK_BEACON_REQUEST)
    void onResult_beacon(int resultCode, Intent data) {

        if(resultCode == RESULT_OK && data != null) {

            _currentBeaconTemp = (BeaconObject)data.getSerializableExtra("beacon");

            Log.d(TAG, "Chose beacon : " + _currentBeaconTemp.deviceName + " , " + _currentBeaconTemp.getMajorMinorString());

        }

        if(resultCode == RESULT_CANCELED) Log.d(TAG, "Choose beacon cancel");

    }

    @ViewById(R.id.saveSetting)
    Button btn_saveSetting;

    @Click(R.id.saveSetting)
    void saveSetting() {

        Log.d(TAG, "Save setting");

        SettingState.getInstance().set_currentDistance(_currentDistanceTemp);

        SettingState.getInstance().set_currentTxPower(_currentTxPowerTemp);

        SettingState.getInstance().set_currentBeaconObject(_currentBeaconTemp);

        SettingState.getInstance().set_fileName(_fileNameTemp);

        setResult(RESULT_OK, SettingActivity.this.getIntent());
        SettingActivity.this.finish();

    }

    @ViewById(R.id.setFile)
    Button btn_setFile;

    @Click(R.id.setFile)
    void setFileName() {
        // TODO
    }

    @AfterViews
    void afterViews() {

        Log.d(TAG, "afterViews");

        _currentDistanceTemp = SettingState.getInstance().get_currentDistance();

        _currentTxPowerTemp = SettingState.getInstance().get_currentTxPower();

        _currentBeaconTemp = SettingState.getInstance().get_currentBeaconObject();

        _fileNameTemp = SettingState.getInstance().get_fileName();

    }

    @Override
    public void onBackPressed() {

//        super.onBackPressed();

        setResult(RESULT_CANCELED, SettingActivity.this.getIntent());
        SettingActivity.this.finish();

    }

}
