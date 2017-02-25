package tech.onetime.oneplay.utils;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import org.springframework.http.ResponseEntity;

//import tech.onetime.onechefmodule.utils.P;
//import tech.onetime.onechefmodule.utils.SoftKeyboard;
//import tech.onetime.onechefmodule.view.ConnectionErrorDialog;
//import tech.onetime.onechefmodule.view.LoadingDg;

//import tech.onetime.oneplay.utils.SoftKeyboard;

import tech.onetime.oneplay.view.ConnectionErrorDialog;
import tech.onetime.oneplay.R;

/**
 * Created by Alexandro on 2016/1/11.
 */
public class GenUtils {

    public static final String TAG = "GenUtils";

    /**
     * 傳入toolbar可預設返回事件、設定toolbar名稱。
     * 若要改變toolbar返回事件，必須在caller 呼叫 setNavigationOnClickListener
     *
     * @param aca                     AppCompateActivity
     * @param toolbar                 toolbar，這個toolbar必須是RIS自定義的toolbar
     * @param title                   toolbar的名稱
     * @param backgroundColorResource toolbar 背景色
     **/
    public static void setActionBarName(final AppCompatActivity aca, Toolbar toolbar, String title, Integer backgroundColorResource) {
        toolbar.setTitle("");
        TextView toolTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        toolTitle.setText(title);
        aca.setSupportActionBar(toolbar);
        aca.getSupportActionBar().setDisplayHomeAsUpEnabled(true); // default true
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aca.finish();
            }
        });

        try {
            if (backgroundColorResource != null)
                aca.getSupportActionBar().setBackgroundDrawable(
                        new ColorDrawable(getColorResourceId(aca
                                , backgroundColorResource)));
            else
                aca.getSupportActionBar().setBackgroundDrawable(
                        new ColorDrawable(getColorResourceId(aca
                                , R.color.colorPrimary)));
        } catch (Exception e) {

        }

    }

    /**
     * 傳入toolbar可預設返回事件、設定toolbar名稱。
     * 若要改變toolbar返回事件，必須在caller 呼叫 setNavigationOnClickListener
     *
     * @param aca     AppCompateActivity
     * @param toolbar toolbar，這個toolbar必須是RIS自定義的toolbar
     * @param title   toolbar的名稱
     **/
    public static void setActionBarName(final AppCompatActivity aca, Toolbar toolbar, String title) {
        toolbar.setTitle("");
        TextView toolTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        toolTitle.setText(title);
        aca.setSupportActionBar(toolbar);
        aca.getSupportActionBar().setDisplayHomeAsUpEnabled(true); // default true
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aca.finish();
            }
        });
    }

    /*
    * 顯示loading dialog
    * ***/
//    public static LoadingDg showLoadingDialog(FragmentManager fragmentManager) {
//        LoadingDg dialogFragment = new LoadingDg();
//        dialogFragment.setCancelable(false);
//        dialogFragment.show(fragmentManager, "dialog");
//
//        return dialogFragment;
//    }

    /*
    * 顯示連線錯誤 dialog
    * ***/
    public static ConnectionErrorDialog showConnectErrorDialog(Context ctx, FragmentManager fragmentManager, ResponseEntity<String> entity) {
        String title = "";
        if (entity != null)
            title = ctx.getString(R.string.connection_error_code, entity.getStatusCode());
        String content = ctx.getString(R.string.connection_failed);

        if (!networkEnable(ctx)) {
            content = ctx.getString(R.string.no_network);
            title = "";
        }

        ConnectionErrorDialog dialogFragment = ConnectionErrorDialog.newInstance(title, content);
        dialogFragment.setCancelable(false);
        dialogFragment.show(fragmentManager, "dialog");

        return dialogFragment;
    }

    /*
    * 顯示錯誤 dialog
    * ***/
    public static ConnectionErrorDialog simpleErrorDialog(FragmentManager fragmentManager, String title, String content) {

        ConnectionErrorDialog dialogFragment = ConnectionErrorDialog.newInstance(title, content);
        dialogFragment.setCancelable(false);
        dialogFragment.show(fragmentManager, "dialog");

        return dialogFragment;
    }

    public static void hideKeyboard(Activity aca) {
        View view = aca.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) aca.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * 鍵盤隱藏或顯示的evnet
     *
     * @param rootLayout activity中最底層的layout
     **/
    public static SoftKeyboard setKeyboardShowHideListener(Context ctx, ViewGroup rootLayout, final SoftKeyboard.iKeyboardChange iChange) {
        InputMethodManager im = (InputMethodManager) ctx.getSystemService(Service.INPUT_METHOD_SERVICE);
        SoftKeyboard softKeyboard;
        softKeyboard = new SoftKeyboard(rootLayout, im);
        softKeyboard.setSoftKeyboardCallback(new SoftKeyboard.SoftKeyboardChanged() {

            @Override
            public void onSoftKeyboardHide() {
                iChange.keyBoardHide();
            }

            @Override
            public void onSoftKeyboardShow() {
                iChange.keyBoardShowed();
            }
        });
        return softKeyboard;
    }

    /**
     * 檢查是否開啟wifi。android M 以上的做法較複雜
     */
    public static boolean wifiConnected(Context ctx) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ConnectivityManager connectivityManager = ((ConnectivityManager) ctx.getSystemService
                    (Context.CONNECTIVITY_SERVICE));
            boolean isWifiConnected = false;
            Network[] networks = connectivityManager.getAllNetworks();
            if (networks == null) {
                isWifiConnected = false;
            } else {
                for (Network network : networks) {
                    android.net.NetworkInfo info = connectivityManager.getNetworkInfo(network);
                    if (info != null && info.getType() == ConnectivityManager.TYPE_WIFI) {
                        if (info.isAvailable() && info.isConnected()) {
                            isWifiConnected = true;
                            break;
                        }
                    }
                }
            }
            return isWifiConnected;
        } else {
            ConnectivityManager connManager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
            android.net.NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            return mWifi != null && mWifi.isConnected();
        }
    }

    /**
     * 取得已連線的ssid。請先檢查是否有網路連線。
     */
    public static String getUsingSSID(Context ctx) {
        WifiManager wifiManager = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo;

        wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo.getSupplicantState() == SupplicantState.COMPLETED) {
            String usingSSID = wifiInfo.getSSID();
            Log.d(TAG, "using ssid:" + usingSSID); // android獲取的SSID會有前後quote
            return usingSSID;
        }
        return null;
    }

    public static boolean isTextInteger(String text) {
        try {
            Integer.parseInt(text);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 由snackbar顯示提示訊息。
     */
    public static void showSnackBar(final Activity ctx, String text, int roodId, final boolean finish) {
        final Snackbar snackbar = Snackbar.make(ctx.findViewById(roodId), text, Snackbar.LENGTH_INDEFINITE);

        snackbar.setAction(ctx.getString(R.string.close_tx), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
                if (finish)
                    ctx.finish();
            }
        });

        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(Color.DKGRAY);
        snackbar.show();
    }

    /**
     * 檢查網路是否開啟
     */
    public static boolean networkEnable(Context mContext) {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Network[] networks = connectivityManager.getAllNetworks();
            NetworkInfo networkInfo;
            for (Network mNetwork : networks) {
                networkInfo = connectivityManager.getNetworkInfo(mNetwork);
                if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
                    return true;
                }
            }
        } else { // version below lollipop
            if (connectivityManager != null) {
                //noinspection deprecation
                NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
                if (info != null) {
                    for (NetworkInfo anInfo : info) {
                        if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
                            //Log.d("Network",
                            //        "NETWORK NAME: " + anInfo.getTypeName());
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    /**
     * 返回到主頁面，且把activity stack清掉。
     */
    public static void intentHome(Context ctx, Intent in) {
        if(in == null)
            ((Activity)ctx).finish();
        in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        ctx.startActivity(in);
        ((Activity) ctx).finish();
    }

    /**
     * 由android版本取得 color resources
     * */
    public static int getColorResourceId(Context ctx, int resId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            return ContextCompat.getColor(ctx, resId);
        else
            return ctx.getResources().getColor(resId);
    }

    /**
     * 返回到登入頁，且把activity stack清掉。如應用程式太久沒用，expired了。
     */
    public static void expired(Context ctx) {
        /*Intent in = new Intent(ctx, DemoLoginActivity_.class);
        in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        ctx.startActivity(in);
        ((Activity) ctx).finish();*/
    }


    /**
     * 把指定的view從view group移除
     **/
    public static void removeViewFromGroup(ViewGroup viewRoot, View removeView) {
        for (int i = 0; i < viewRoot.getChildCount(); i++) {
            View view = viewRoot.getChildAt(i);
            if (view == removeView) {
                viewRoot.removeViewAt(i);
            }
        }
    }

//    /**
//     * 防止按鈕彈跳
//     **/
//    private static long lastClickTime;
//
//    public static boolean isFastDoubleClick() {
//        long time = System.currentTimeMillis();
//        long timeD = time - lastClickTime;
//        if (0 < timeD && timeD < 1500) {
//            return true;
//        }
//        lastClickTime = time;
//        return false;
//    }


}
