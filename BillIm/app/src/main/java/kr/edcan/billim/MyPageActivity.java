package kr.edcan.billim;

import android.app.ActionBar;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;


public class MyPageActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);
        setDefault();
    }
    public void setDefault() {
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#d7ccc8")));
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        actionBar.setElevation(0);
        actionBar.setTitle(Html.fromHtml("<font color=\"#8d6e63\"><b>마이페이지</b> </p>"));
    }
}
