package kr.edcan.billim;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.view.DragEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;


public class SettingActivity extends ActionBarActivity implements View.OnClickListener{

    RelativeLayout develInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        setDefault();

    }
    public void setDefault() {
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#D4C6C3")));
        actionBar.setTitle(Html.fromHtml("<font color=\"#8d6e63\"><b>환경설정</b> </p>"));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
            actionBar.setElevation(0);
        }
        develInfo = (RelativeLayout) findViewById(R.id.developer_info);
        develInfo.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent developerInfo = new Intent(getApplicationContext(), DeveloperInfo.class);
        startActivity(developerInfo);
    }
}
