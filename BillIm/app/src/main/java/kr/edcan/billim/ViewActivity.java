package kr.edcan.billim;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;

import kr.edcan.billim.utils.Article;
import kr.edcan.billim.utils.BillimService;
import kr.edcan.billim.utils.DownloadImageTask;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class ViewActivity extends ActionBarActivity implements View.OnClickListener{

    BillimService service;
    FloatingActionButton Confirm;
    ImageView backButton, viewImage;
    Intent intent;
    int id;
    TextView titleText, descriptionText, commentText, locationText, rewardText;
    SharedPreferences sharedPreferences;
    String title, info, apikey,  comment, location, reward, type, picture;
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
        apikey = sharedPreferences.getString("apikey", "");
        intent = getIntent();
        id = intent.getIntExtra("ID", -1);
        viewImage = (ImageView)findViewById(R.id.view_image);
        backButton = (ImageView)findViewById(R.id.view_back_button);
        titleText = (TextView)findViewById(R.id.view_item_name);
        descriptionText = (TextView)findViewById(R.id.view_item_type);
        commentText = (TextView)findViewById(R.id.view_comment);
        locationText = (TextView)findViewById(R.id.view_place);
        rewardText = (TextView)findViewById(R.id.view_reward);
        backButton.setOnClickListener(this);
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
                switch(article.type){
                    case 0: type = "빌려주세요"; break;
                    case 1: type = "빌려드려요"; break;
                    case 2: type = "드려요"; break;
                    case 3: type = "교환해요"; break;
                }
                title = article.name;
                info =  type+ " - " + article.author.name + "이 요청";
                comment = article.description;
                location = article.location;
                reward = article.reward;
                picture = "http://billim.kkiro.kr/"+article.photo;
                if(article.photo!=null) new DownloadImageTask(viewImage).execute(picture);
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
        finish();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}