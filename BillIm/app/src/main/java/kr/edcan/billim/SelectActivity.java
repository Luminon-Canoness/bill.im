package kr.edcan.billim;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;


public class SelectActivity extends Activity implements View.OnClickListener {

    int shareType;
    LinearLayout pilgigu, machine, food, cloth, book, etc;
    int i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#A4847F"));
        }
        pilgigu = (LinearLayout) findViewById(R.id.pilgigu);
        machine = (LinearLayout) findViewById(R.id.machine);
        food = (LinearLayout) findViewById(R.id.food);
        cloth = (LinearLayout) findViewById(R.id.cloth);
        book = (LinearLayout) findViewById(R.id.book);
        etc = (LinearLayout) findViewById(R.id.etc);
        pilgigu.setOnClickListener(this);
        etc.setOnClickListener(this);
        machine.setOnClickListener(this);
        food.setOnClickListener(this);
        cloth.setOnClickListener(this);
        book.setOnClickListener(this);
        shareType = getIntent().getIntExtra("ShareType", -1);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pilgigu: {
                i = 0;
                break;
            }
            case R.id.machine: {
                i = 1;
                break;
            }
            case R.id.food: {
                i = 2;
                break;
            }
            case R.id.cloth: {
                i = 3;
                break;
            }
            case R.id.book: {
                i = 4;
                break;
            }
            case R.id.etc: {
                i = 5;
                break;
            }
        }
        Intent intent = new Intent(getApplicationContext(), PostActivity.class);
        intent.putExtra("Type", i);
        intent.putExtra("ShareType", shareType);
        startActivity(intent);
        finishActivity(1234);
        finish();
    }
}
