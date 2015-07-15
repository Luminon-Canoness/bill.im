package kr.edcan.billim.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.rey.material.widget.Button;

import kr.edcan.billim.GroupAddActivity;
import kr.edcan.billim.R;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * Created by kotohana5706 on 15. 7. 12.
 */
public class CustomDialog extends Activity {

    String creategroupname;
    Button cancel, confirm;
    BillimService service;
    View.OnClickListener dialogConfirm;
    String apikey;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setWindow();
        setContentView(R.layout.custom_dialog);
        setDefault();
        setService();
    }
    public void setService() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("http://billim.kkiro.kr")
                .build();
        service = restAdapter.create(BillimService.class);
        sharedPreferences = getSharedPreferences("Billim", 0);
        apikey = sharedPreferences.getString("apikey", "");
    }

    public void setDefault() {
//        dialogTitle_ = (TextView) findViewById(R.id.dialog_title);
//        dialogDescription_ = (TextView) findViewById(R.id.dialogDescription);
//        dialogTitle_.setText(dialogTitle);
//        dialogDescription_.setText(dialogDescription);
        cancel = (Button) findViewById(R.id.cancel_button);
        confirm = (Button) findViewById(R.id.confirm_button);
//        cancel.setText(CancelText);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
//        confirm.setText(ConfirmText);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = (EditText)findViewById(R.id.dialogDescription);
                creategroupname = editText.getText().toString();
                service.createGroup(apikey,creategroupname, new Callback<GroupResponse>() {
                    @Override
                    public void success(GroupResponse groupResponse, Response response) {
                        Toast.makeText(getApplicationContext(), creategroupname + " 그룹이 만들어졌습니다", Toast.LENGTH_SHORT).show();
                        GroupAddActivity.activity.finish();
                        finish();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        if(error.getResponse().getStatus() == 200){
                            Toast.makeText(getApplicationContext(),creategroupname +" 그룹이 만들어졌습니다", Toast.LENGTH_SHORT).show();
                            GroupAddActivity.activity.finish();
                            finish();
                        }
                        Log.e("Error", error.getResponse().getStatus() + "");
                    }
                });
            }
        });
    }

    public void setWindow() {
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);
    }
}
