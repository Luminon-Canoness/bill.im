package kr.edcan.billim;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.view.View;

import com.getbase.floatingactionbutton.FloatingActionButton;

import kr.edcan.billim.utils.CustomDialog;


public class ViewActivity extends ActionBarActivity implements View.OnClickListener{

    FloatingActionButton Confirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        setDefault();
    }
    public void setDefault() {
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#D4C6C3")));
        actionBar.setTitle(Html.fromHtml("<font color=\"#8d6e63\"><b>진행중인 항목</b> </p>"));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
            actionBar.setElevation(0);
        }
        Confirm = (FloatingActionButton)findViewById(R.id.view_confirm);
        Confirm.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        CustomDialog customDialog = new CustomDialog(ViewActivity.this, "빌려주기", "해당 물건을 빌려주시겠습니까?\n" +
                "빌려주기를 누르면 Bill.IM 이용 내역에서 상태를 관리, 확인할 수 있습니다.", "빌려주기", "취소", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        customDialog.show();
    }
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_view, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
}