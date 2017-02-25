package tech.onetime.oneplay.activity;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import tech.onetime.oneplay.R;

/**
 * Created by JianFa on 2017/2/25
 */

@EActivity(R.layout.activity_choose_distance_v2)
public class ChooseDistanceActivity extends AppCompatActivity {

    private static final Integer[] _distances = {1, 2, 3, 5, 8, 10, 20, 30, 40, 50};
    private static int _currentDistance = _distances[0];

    @ViewById(R.id.distanceList)
    ListView list_distance;

    @AfterViews
    void setDistancesIntoList() {

        ArrayAdapter<Integer> itemsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, _distances);
        list_distance.setAdapter(itemsAdapter);
        list_distance.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), _distances[position].intValue(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static int getCurrentDistance() {

        return _currentDistance;

    }

}
