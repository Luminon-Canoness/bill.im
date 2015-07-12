package kr.edcan.billim;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;


public class LoginActivity extends ActionBarActivity implements View.OnClickListener {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    ImageView  facebook;
    private String name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setDefault();
        if (sharedPreferences.getBoolean("LoginState", false) == true) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            SignIn();
            startActivity(intent);
            finish();
        }

    }

    public void SignIn() {
        //
        Toast.makeText(getApplicationContext(),name+" 님으로 로그인 되었습니다!", Toast.LENGTH_LONG).show();
    }
    public void setDefault() {
        sharedPreferences = getSharedPreferences("Billim", 0);
        editor = sharedPreferences.edit();
        facebook = (ImageView) findViewById(R.id.login_facebook);
        facebook.setOnClickListener(this);
        name = "구창림";
    }

    @Override
    public void onClick(View v) {
        editor.putBoolean("LoginState", true);
        editor.commit();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }
}