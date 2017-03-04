package tech.onetime.beaconRecorder.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import tech.onetime.beaconRecorder.R;

/**
 * Created by JianFa on 2017/2/25
 */

@EActivity(R.layout.activity_choose_distance_v2)
public class ChooseDistanceActivity extends AppCompatActivity {

    private static final Integer[] _distances = {1, 2, 3, 5, 8, 13, 20, 30, 40, 50};

    @ViewById(R.id.chooseDistanceCancel)
    Button btn_chooseDistanceCancel;

    @Click(R.id.chooseDistanceCancel)
    void chooseDistanceCancel() {

        setResult(RESULT_CANCELED);
        ChooseDistanceActivity.this.finish();

    }

    @ViewById(R.id.distanceList)
    ListView list_distance;

    @AfterViews
    void setDistancesIntoList() {

        ArrayAdapter<Integer> itemsAdapter = new ArrayAdapter<>(this, R.layout.simple_list_item_1_custom, _distances);
        list_distance.setAdapter(itemsAdapter);
        list_distance.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Bundle bundle = new Bundle();
                bundle.putInt("distance", _distances[position]);
                setResult(RESULT_OK, ChooseDistanceActivity.this.getIntent().putExtras(bundle));
                ChooseDistanceActivity.this.finish();

            }
        });
    }

}
