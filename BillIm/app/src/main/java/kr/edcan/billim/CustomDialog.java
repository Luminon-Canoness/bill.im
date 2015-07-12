package kr.edcan.billim;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.rey.material.widget.Button;


/**
 * Created by kotohana5706 on 15. 7. 12.
 */
public class CustomDialog extends Dialog {

    String dialogTitle, dialogDescription, ConfirmText, CancelText;
    Button cancel, confirm;
    View.OnClickListener dialogConfirm;
    TextView dialogTitle_, dialogDescription_;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setWindow();
        setContentView(R.layout.custom_dialog);
        setDefault();
    }

    public void setDefault() {
        dialogTitle_ = (TextView) findViewById(R.id.dialog_title);
        dialogDescription_ = (TextView) findViewById(R.id.dialog_description);
        dialogTitle_.setText(dialogTitle);
        dialogDescription_.setText(dialogDescription);
        cancel = (Button) findViewById(R.id.cancel_button);
        confirm = (Button) findViewById(R.id.confirm_button);
        cancel.setText(CancelText);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        confirm.setText(ConfirmText);
        confirm.setOnClickListener(dialogConfirm);
    }

    public CustomDialog(Context context, String title, String description, String confirmtext, String canceltext, View.OnClickListener confirmOnClick) {
        super(context, R.style.Theme_AppCompat_Light_Dialog);
        this.dialogTitle = title;
        this.dialogDescription = description;
        this.dialogConfirm = confirmOnClick;
        this.ConfirmText = confirmtext;
        this.CancelText = canceltext;
    }

    public void setWindow() {
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);
    }
}
