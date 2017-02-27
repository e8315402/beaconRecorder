package tech.onetime.beaconRecorder.view;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

//import tech.onetime.onechefmodule.R;
import tech.onetime.beaconRecorder.R;

/**
 * Created by Alexandro on 2016/1/17.
 */
public class ConfirmDialog extends DialogFragment {
    protected Dialog dialog;

    private String title;
    private String content;
    private iConfirmDialog iEvent;
    private boolean backIconDisable = false;

    public static ConfirmDialog newInstance(String _title, String _content) {
        ConfirmDialog fragment = new ConfirmDialog();
        Bundle args = new Bundle();
        args.putString("title", _title);
        args.putString("content", _content);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        if (getArguments() != null) {
            title = getArguments().getString("title");
            content = getArguments().getString("content");
        }

        Dialog dialog = super.onCreateDialog(savedInstanceState);

        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);

        dialog.setCanceledOnTouchOutside(true);

        dialog.setContentView(R.layout.confirm_dialog);

        this.dialog = dialog;

        initView();

        return dialog;
    }

    private void initView() {
        LinearLayout ensBtn = (LinearLayout) dialog.findViewById(R.id.ensLayout);
        RelativeLayout ctLayout = (RelativeLayout) dialog.findViewById(R.id.ctLayout);

        TextView titleTx = ((TextView) dialog.findViewById(R.id.confirmDialog_title));
        TextView contentTx = ((TextView) dialog.findViewById(R.id.confirmDialog_content));
        titleTx.setText(title);
        contentTx.setText(content);

        if(backIconDisable){
            dialog.findViewById(R.id.confirmDialog_back).setVisibility(View.GONE);
            dialog.setCanceledOnTouchOutside(false);
        }

        // dialog back
        dialog.findViewById(R.id.confirmDialog_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        // cancel
        dialog.findViewById(R.id.cancelBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        // ens
        dialog.findViewById(R.id.ensBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (iEvent != null)
                    iEvent.iConfirmDgEvent();
            }
        });
    }

    public void setiEvent(iConfirmDialog iEvent){
        this.iEvent = iEvent;
    }

    /**
     * 不顯示返回鍵，且不可touch out side cancel
     * */
    public void setBackIconGone(){
        backIconDisable = true;
    }

    /**
     * 確定的事件
     * **/
    public interface iConfirmDialog {
        public void iConfirmDgEvent();
    }

    /**
     * 在onResum 才能設置 v4 dialog 長寬
     **/
    @Override
    public void onResume() {
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setAttributes((WindowManager.LayoutParams) params);

        super.onResume();
    }
}
