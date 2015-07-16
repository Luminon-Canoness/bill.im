package kr.edcan.billim;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.getbase.floatingactionbutton.FloatingActionButton;

import java.util.List;

import kr.edcan.billim.utils.Article;
import kr.edcan.billim.utils.BillimService;
import kr.edcan.billim.utils.DownloadImageTask;
import kr.edcan.billim.utils.GroupResponse;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class ViewActivity extends ActionBarActivity implements View.OnClickListener{

    BillimService service;
    FloatingActionButton Confirm, Delete;
    ImageView backButton, viewImage, report;
    Intent intent;
    int id;
    int myid;
    TextView titleText, descriptionText, commentText, locationText, rewardText, actionbarText;
    SharedPreferences sharedPreferences;
    String title, info, apikey,  comment, location, reward, type, picture;
    ProgressDialog progressDialog;
    Article currentArticle;

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
        myid = sharedPreferences.getInt("userid", 0);
        intent = getIntent();
        id = intent.getIntExtra("ID", -1);
        viewImage = (ImageView)findViewById(R.id.view_image);
        backButton = (ImageView)findViewById(R.id.view_back_button);
        titleText = (TextView)findViewById(R.id.view_item_name);
        descriptionText = (TextView)findViewById(R.id.view_item_type);
        commentText = (TextView)findViewById(R.id.view_comment);
        locationText = (TextView)findViewById(R.id.view_place);
        rewardText = (TextView)findViewById(R.id.view_reward);
        actionbarText = (TextView)findViewById(R.id.actionbar_text);
        report = (ImageView)findViewById(R.id.btn_report);
        report.setOnClickListener(this);
        backButton.setOnClickListener(this);
        Confirm = (FloatingActionButton)findViewById(R.id.view_confirm);
        Delete = (FloatingActionButton)findViewById(R.id.view_delete);
        Delete.setOnClickListener(this);
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
                actionbarText.setText(type);
                info =  type+ " - " + article.author.name + "이(가) 요청";
                title = article.name;
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
                currentArticle = article;
                Confirm.setVisibility(View.GONE);
                if(article.state == 0) Confirm.setVisibility(View.VISIBLE);
                if(article.state == 2 && article.author.id == myid) Confirm.setVisibility(View.VISIBLE);
                if(article.state == 3 && article.responder != null && article.responder.id == myid) Confirm.setVisibility(View.VISIBLE);
                if(article.state != 0 || article.author.id != myid) Delete.setVisibility(View.GONE);
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
        switch(v.getId()){
            case R.id.btn_report :{
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:112"));
                startActivity(intent);
                break;
            }
            case R.id.view_back_button:{
                finish();
            }
            case R.id.view_confirm:{
                if(currentArticle.state == 0) {
                    // 이 게시글을 수락하시겠습니까v
                    MaterialDialog materialDialog = new MaterialDialog.Builder(ViewActivity.this)
                            .title("확인해주세요!")
                            .content("게시글을 수락하시겠습니까")
                            .positiveText("확인")
                            .negativeText("취소")
                            .callback(new MaterialDialog.ButtonCallback() {
                                @Override
                                public void onPositive(MaterialDialog dialog) {
                                    super.onPositive(dialog);
                                    service.changePostState(apikey, currentArticle.id, new Callback<List<Article>>() {
                                        @Override
                                        public void success(List<Article> articles, Response response) {
                                            setData();
                                            Toast.makeText(getApplicationContext(), "게시글 상태가 변경되었습니다!", Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void failure(RetrofitError error) {
                                            Log.e("asdf", error.getResponse().getStatus()+"");
                                            setData();
                                            Toast.makeText(getApplicationContext(), "게시글 상태가 변경되었습니다!", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }

                            })
                            .show();
                } else if(currentArticle.state == 2) {
                    // 빌려줌/완료로 바꾸시겠습니까
                    MaterialDialog materialDialog = new MaterialDialog.Builder(ViewActivity.this)
                            .title("확인해주세요!")
                            .content("빌려줌 / 완료로 바꾸시겠습니까?")
                            .positiveText("확인")
                            .negativeText("취소")
                            .callback(new MaterialDialog.ButtonCallback() {
                                @Override
                                public void onPositive(MaterialDialog dialog) {
                                    super.onPositive(dialog);
                                    service.changePostState(apikey, currentArticle.id, new Callback<List<Article>>() {
                                        @Override
                                        public void success(List<Article> articles, Response response) {
                                            setData();
                                            Toast.makeText(getApplicationContext(), "게시글 상태가 변경되었습니다!", Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void failure(RetrofitError error) {
                                            setData();
                                            Toast.makeText(getApplicationContext(), "게시글 상태가 변경되었습니다!", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }

                            })
                            .show();
                } else if(currentArticle.state == 3) {
                    // 거래를 끝내시겠습니까
                    MaterialDialog materialDialog = new MaterialDialog.Builder(ViewActivity.this)
                            .title("확인해주세요!")
                            .content("거래를 끝내시겠습니까?")
                            .positiveText("확인")
                            .negativeText("취소")
                            .callback(new MaterialDialog.ButtonCallback() {
                                @Override
                                public void onPositive(MaterialDialog dialog) {
                                    super.onPositive(dialog);
                                    service.changePostState(apikey, currentArticle.id, new Callback<List<Article>>() {
                                        @Override
                                        public void success(List<Article> articles, Response response) {
                                            setData();
                                            Toast.makeText(getApplicationContext(), "게시글 상태가 변경되었습니다!", Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void failure(RetrofitError error) {
                                            setData();
                                            Toast.makeText(getApplicationContext(), "게시글 상태가 변경되었습니다!", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }

                            })
                            .show();
                }
                break;
            }
            case R.id.view_delete:
                service.deletePost(apikey, id, new Callback<List<Article>>() {
                    @Override
                    public void success(List<Article> articles, Response response) {
                        Toast("게시글이 삭제되었습니다",0);
                        finish();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        if(error.getResponse().getStatus()==200){
                            Toast("게시글이 삭제되었습니다",0);
                            finish();
                        }
                        else Log.e("asdf", error.getResponse().getStatus()+"");

                    }
                });
        }
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