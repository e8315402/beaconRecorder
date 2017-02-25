package tech.onetime.oneplay.ble;

import android.util.Log;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import tech.onetime.oneplay.schema.BeaconObject;

/**
 * Created by Alexandro on 2016/7/15.
 */
public class PositionAlgorithm {

    private final String TAG = "PositionAlgorithm";

    private ArrayList<BeaconObject> beacons = new ArrayList<>();
    private int logarithm, power;

    public PositionAlgorithm(ArrayList<BeaconObject> beacons, int power, int logarithm) {
        this.beacons = beacons;
        this.logarithm = logarithm;
        this.power = power;
//        for (int i = 0; i < beacons.size(); i++)
//            Log.d(TAG,Integer.toString(i) + Integer.toString(beacons.get(i).rssi));
        sortBeacons();
    }

    /**
     * 回傳估算的座標 和 平均RSSI
     */
    public double[] getCurrentPositionAndAVGRssi() throws IndexOutOfBoundsException {

        //double x0, y0;

//        if(beacons.size() <= 1) {
//            Log.d(TAG, "getCurrentPositionAndAVGRssi__beacons.size() <= 1 : return null");
//            return null;
//        }

        BeaconObject base = beacons.get(0);
        //double xSum = 0, ySum = 0;
        int avgRSSI = beacons.get(0).rssi;

        /*for (int i = 1; i < beacons.size(); i++) {
            BeaconObject com = beacons.get(i);

            double shiftRSSI = getTransRSSI(base.rssi) / (getTransRSSI(base.rssi) + getTransRSSI(com.rssi));
            double x = base.major + (com.major - base.major) * (shiftRSSI);
            double y = base.minor + (com.minor - base.minor) * (shiftRSSI);

            Log.d("point1",Integer.toString(base.major) + Integer.toString(base.minor));
            Log.d("point2",Integer.toString(com.major) + Integer.toString(com.minor));
            xSum += x;
            ySum += y;
            avgRSSI += com.rssi;
        }

        xSum = xSum / (beacons.size() - 1);
        ySum = ySum / (beacons.size() - 1);
        avgRSSI = avgRSSI / (beacons.size() - 1);*/

        DecimalFormat df = new DecimalFormat("##.00");
        double ret[] = new double[3];
        ret[0] = Double.parseDouble(df.format(base.major));
        ret[1] = Double.parseDouble(df.format(base.minor));
        ret[2] = Double.parseDouble(df.format(avgRSSI));

        return ret;
    }

    /**
     * 轉換 RSSI
     */
    private double getTransRSSI(int rssi) {
        return Math.abs(rssi) * Math.pow(power, Math.log(Math.abs(rssi) / Math.log(logarithm)));
    }

    /**
     * 由大到小排序 RSSI
     */
    private void sortBeacons() {
        Collections.sort(beacons, new Comparator() {

            @Override
            public int compare(Object lhs, Object rhs) {
                return ((BeaconObject) rhs).rssi - ((BeaconObject) lhs).rssi;
            }
        });
    }


}
