package tech.onetime.beaconRecorder.view;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import tech.onetime.beaconRecorder.R;
import tech.onetime.beaconRecorder.utils.LP;

public class RisToast {

    private static mToast toast;

    public static void show(Context context, String text) {
        if (toast != null) toast.cancel();
        toast = new mToast(context, text);
        toast.show();
    }

    private static class mToast extends Toast {

        public mToast(Context context, String msg) {
            super(context);
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();

            View v = inflater.inflate(R.layout.alert_toast, null);
            TextView tx = (TextView) v.findViewById(R.id.alert_toast_text);
            tx.setText(msg);

            tx.setSingleLine(false);

            v.setLayoutParams(LP.Match_Wrap());

            super.setView(v);

            super.setGravity(Gravity.FILL_HORIZONTAL | Gravity.BOTTOM, 0, 0);
            super.setDuration(Toast.LENGTH_SHORT);

        }
    }

}
