package kr.edcan.billim;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;

import org.w3c.dom.Text;

import kr.edcan.billim.utils.Article;
import kr.edcan.billim.utils.BillimService;
import kr.edcan.billim.utils.CustomDialog;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class ViewActivity extends ActionBarActivity implements View.OnClickListener{

    BillimService service;
    FloatingActionButton Confirm;
    ImageView backButton;
    Intent intent;
    int id;
    TextView titleText, descriptionText, commentText, locationText, rewardText;
    SharedPreferences sharedPreferences;
    String title, info, apikey,  comment, location, reward;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading, please wait.");
        progressDialog.show();
        setDefault();
        setData();
    }
    public void setDefault() {
        sharedPreferences = getSharedPreferences("Billim", 0);
        apikey = sharedPreferences.getString("apikey","");
        intent = getIntent();
        id = intent.getIntExtra("ID", -1);
        backButton = (ImageView)findViewById(R.id.view_back_button);
        titleText = (TextView)findViewById(R.id.view_item_name);
        descriptionText = (TextView)findViewById(R.id.view_item_type);
        commentText = (TextView)findViewById(R.id.view_comment);
        locationText = (TextView)findViewById(R.id.view_place);
        rewardText = (TextView)findViewById(R.id.view_reward);
//        backButton.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        backButton.setBackgroundColor(Color.parseColor("#22FFFFFF"));
//                        break;
//                    case MotionEvent.ACTION_UP:
//                        backButton.setBackgroundColor(Color.alpha(android.R.color.transparent));
//                }
//                return true;
//            }
//        });
        Confirm = (FloatingActionButton)findViewById(R.id.view_confirm);
        Confirm.setOnClickListener(this);
    }
    public void setData(){
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("http://billim.kkiro.kr")
                .build();
        service = restAdapter.create(BillimService.class);
        service.getArticle(apikey, id, new Callback<Article>() {
            @Override
            public void success(Article article, Response response) {
                title = article.name;
                info = article.type + " - " + article.author + "이 빌려주기를 요청";
                comment = article.description;
                location = article.location;
                reward = article.reward;
                titleText.setText(title);
                descriptionText.setText(info);
                commentText.setText(comment);
                locationText.setText(location);
                rewardText.setText(reward);
                progressDialog.dismiss();
            }

            @Override
            public void failure(RetrofitError error) {
                progressDialog.dismiss();
                Log.e("asdf", error.getResponse().getStatus()+"");
                Toast(error.getResponse().getStatus() + "", 1);
            }
        });
    }
    public void Toast(String s, int length){
        Toast.makeText(getApplicationContext(),s,(length==0)?Toast.LENGTH_SHORT:Toast.LENGTH_LONG).show();
    }
    @Override
    public void onClick(View v) {
//        CustomDialog customDialog = new CustomDialog(ViewActivity.this, "빌려주기", "해당 물건을 빌려주시겠습니까?\n" +
//                "빌려주기를 누르면 Bill.IM 이용 내역에서 상태를 관리, 확인할 수 있습니다.", "빌려주기", "취소", new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
//        customDialog.show();
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