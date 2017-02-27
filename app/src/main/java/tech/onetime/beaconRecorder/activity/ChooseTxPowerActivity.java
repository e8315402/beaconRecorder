package tech.onetime.beaconRecorder.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import tech.onetime.beaconRecorder.R;
import tech.onetime.beaconRecorder.schema.SettingState;

/**
 * Created by JianFa on 2017/2/25
 */

@EActivity(R.layout.activity_choose_txpower)
public class ChooseTxPowerActivity extends AppCompatActivity {

    @ViewById(R.id.txPower_1m)
    TextView textView_txPower_1m;
    @ViewById(R.id.txPower_10m)
    TextView textView_txPower_10m;
    @ViewById(R.id.txPower_20m)
    TextView textView_txPower_20m;
    @ViewById(R.id.txPower_50m)
    TextView textView_txPower_50m;

    @Click(R.id.txPower_1m)
    void chose_1m(){
        returnResult("1M");
    }

    @Click(R.id.txPower_10m)
    void chose_10m() {
        returnResult("10M");
    }

    @Click(R.id.txPower_20m)
    void chose_20M() {
        returnResult("20M");
    }

    @Click(R.id.txPower_50m)
    void chose_50M() {
        returnResult("50M");
    }

    private void returnResult(String txPower) {

        Bundle bundle = new Bundle();
        bundle.putString("txPower", txPower);
        setResult(RESULT_OK, ChooseTxPowerActivity.this.getIntent().putExtras(bundle));
        ChooseTxPowerActivity.this.finish();

    }

    @Override
    public void onResume() {

        String currentTxPower = SettingState.getInstance().get_currentTxPower();

        switch (currentTxPower) {
            case "1M":
                textView_txPower_1m.setTextColor(getResources().getColor(R.color.green_700));
                break;
            case "10M":
                textView_txPower_10m.setTextColor(getResources().getColor(R.color.green_700));
                break;
            case "20M":
                textView_txPower_20m.setTextColor(getResources().getColor(R.color.green_700));
                break;
            case "50M":
                textView_txPower_50m.setTextColor(getResources().getColor(R.color.green_700));
                break;
        }

        super.onResume();

    }

}
