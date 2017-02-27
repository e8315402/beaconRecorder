package tech.onetime.beaconRecorder.view;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import tech.onetime.beaconRecorder.R;

/**
 * Created by Alexandro on 2016/1/17.
 */
public class ConnectionErrorDialog extends DialogFragment {
    protected Dialog dialog;

    private String content;
    private String title;
    private iConfirmDialog iEvent;

    public static ConnectionErrorDialog newInstance(String _title, String _content) {
        ConnectionErrorDialog fragment = new ConnectionErrorDialog();
        Bundle args = new Bundle();
        args.putString("title", _title);
        args.putString("content", _content);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        if (getArguments() != null) {
            content = getArguments().getString("content");
            title = getArguments().getString("title");
        }

        Dialog dialog = super.onCreateDialog(savedInstanceState);

        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);

        dialog.setCanceledOnTouchOutside(true);
        setCancelable(false);

        dialog.setContentView(R.layout.connection_error_dialog);

        this.dialog = dialog;

        initView();

        return dialog;
    }

    private void initView() {
        TextView titleTx = ((TextView) dialog.findViewById(R.id.connectionErrorDg_title));
        View divideLine = dialog.findViewById(R.id.divide_line);
        TextView contentTx = ((TextView) dialog.findViewById(R.id.connectionErrorDg_content));
        contentTx.setText(content);
        if (title != null && !"".equals(title)) {
            titleTx.setVisibility(View.VISIBLE);
            divideLine.setVisibility(View.VISIBLE);
            titleTx.setText(title);
        }

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

    public void setiEvent(iConfirmDialog iEvent) {
        this.iEvent = iEvent;
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

    /**
     * 確定的事件
     **/
    public interface iConfirmDialog {
        public void iConfirmDgEvent();
    }
}
