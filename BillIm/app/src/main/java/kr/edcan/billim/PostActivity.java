package kr.edcan.billim;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;

import java.io.File;

import kr.edcan.billim.utils.Article;
import kr.edcan.billim.utils.BillimService;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;


public class PostActivity extends ActionBarActivity {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    FloatingActionButton postbutton;
    EditText name, comment, place, reward;
    ImageView post_type_image;
    TextView post_type_text;
    Intent intent;
    String shareType, picturePath, apikey, item_name, item_comment, item_place, item_reward;
    ImageView toGallery;//이미지뷰 선언
    private static int RESULT_LOAD_IMAGE = 1;
    BillimService service;
    int shareType_int, category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        name = (EditText) findViewById(R.id.item_name);
        comment = (EditText) findViewById(R.id.item_comment);
        place = (EditText) findViewById(R.id.item_place);
        reward = (EditText) findViewById(R.id.item_reward);
        postbutton = (FloatingActionButton) findViewById(R.id.postbutton);
        postbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkDialog();
            }
        });
        post_type_image = (ImageView) findViewById(R.id.post_type_image);
        post_type_text = (TextView) findViewById(R.id.post_type_text);
        sharedPreferences = getSharedPreferences("Billim", 0);
        apikey = sharedPreferences.getString("apikey", "");
        setDefault();
        setImage();
    }

    public void setDefault() {
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        intent = getIntent();
        picturePath="";
        shareType_int = intent.getIntExtra("ShareType", -1);
        category = intent.getIntExtra("Type", -1);
        switch (shareType_int) {
            case 0: {
                shareType = "빌림 - 요청";
                actionBar.setTitle(Html.fromHtml("<font color='#A4847E'><b>빌려주세요!</b> </font>"));
                name.setHint("빌릴 물건의 이름 입력");
                break;
            }
            case 1: {
                shareType = "빌림 - 빌려줌";
                actionBar.setTitle(Html.fromHtml("<font color='#A4847E'><b>빌려드려요!</b> </font>"));
                name.setHint("빌려줄 물건의 이름 입력");
                break;
            }
            case 2: {
                shareType = "양도";
                actionBar.setTitle(Html.fromHtml("<font color='#A4847E'><b>드려요!</b> </font>"));
                name.setHint("양도할 물건의 이름 입력");
                break;
            }
            case 3: {
                shareType = "교환";
                actionBar.setTitle(Html.fromHtml("<font color='#A4847E'><b>드려요!</b> </font>"));
                name.setHint("교환할 물건의 이름 입력");
                break;
            }
        }
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#d7ccc8")));
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        actionBar.setElevation(0);

        switch (category) {
            case -1: {
                ShortToast("어플리케이션 오류가 발생하였습니다.");
                finish();
                break;
            }
            case 0: {
                post_type_image.setImageResource(R.drawable.ic_pillgigu);
                post_type_text.setText("카테고리 : 필기구");
                break;
            }
            case 1: {
                post_type_image.setImageResource(R.drawable.ic_machine);
                post_type_text.setText("카테고리 : 전자제품");
                break;
            }
            case 2: {
                post_type_image.setImageResource(R.drawable.ic_food);
                post_type_text.setText("카테고리 : 음식 / 음료");
                break;
            }
            case 3: {
                post_type_image.setImageResource(R.drawable.ic_cloth);
                post_type_text.setText("카테고리 : 옷");
                break;
            }
            case 4: {
                post_type_image.setImageResource(R.drawable.ic_books);
                post_type_text.setText("카테고리 : 책 / 문서");
                break;
            }
            case 5: {
                post_type_image.setImageResource(R.drawable.ic_etc);
                post_type_text.setText("카테고리 : 기타");
                break;
            }
        }
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("http://billim.kkiro.kr")
                .build();
        service = restAdapter.create(BillimService.class);

    }

    public void setImage() {
        toGallery = (ImageView) findViewById(R.id.post_image); //이미지뷰 초기화
        toGallery.setOnClickListener(new View.OnClickListener() {  //이미지뷰가 클릭되었을 때의 리스너
            @Override
            public void onClick(View arg0) {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                //안드로이드 시스템에 있는 이미지들에서 선택(PICK)을 위한 인텐트 생성
                startActivityForResult(i, RESULT_LOAD_IMAGE);//위에서 선언한 1이라는 결과 코드로 액티비티를 선언
            }
        });
    }

    public void checkDialog() {
        item_name = name.getText().toString();
        item_comment = comment.getText().toString();
        item_place = place.getText().toString();
        item_reward = reward.getText().toString();
        if (!item_name.trim().equals("") && !item_comment.trim().equals("") && !item_place.trim().equals("") && !item_reward.trim().equals("")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("다시 한번 확인해주세요!");
            builder.setMessage(
                    "이름 : " + item_name + "\n" +
                            "형식 : " + shareType + "\n" +
                            post_type_text.getText().toString() + "\n" +
                            "설명 : " + item_comment + "\n" +
                            "만날 장소 : " + item_place + "\n" +
                            "보상 : " + item_reward + "\n\n" +
                            "이 내용이 확실합니까?"
            ).setCancelable(true)
                    .setPositiveButton("확인",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    setUpload();
                                }
                            })
                    .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });

            AlertDialog alert = builder.create();
            alert.show();
        } else Toast.makeText(getApplicationContext(), "공백 없이 입력해주세요!", Toast.LENGTH_SHORT).show();
    }

    public void ShortToast(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//startActivityForResult 에서 결과가 나왔을때 작동하는 메서드 입니다
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
// 리퀘스트코드가 RESULT_LOAD_IMAGE(1) 이고, 결과가 OK라는 성공값이며, 데이터가 존재할 때 작동합니다.
            Uri selectedImage = data.getData();
// android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI   에서 받아온 데이터를 변수에 넣어준뒤
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picturePath = cursor.getString(columnIndex);
            Log.e("파일 경로", picturePath);
//Uri에서 이미지의 외장 메모리상의 주소를 받아온 뒤
            cursor.close();
            toGallery.setImageBitmap(BitmapFactory.decodeFile(picturePath));//이미지뷰에 뿌려줍니다.
        }
    }

    public void setUpload() {
        service.postArticle(apikey, 1, shareType_int, category, item_name, item_comment, item_reward, item_place,
                (!picturePath.equals("")) ? new TypedFile("image/jpeg", new File(picturePath)) : null, new Callback<Article>() {
                    @Override
                    public void success(Article article, Response response) {
                        ShortToast("성공 " + response);
                        finish();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        int errorcode = error.getResponse().getStatus();
                        ShortToast("에러 " + errorcode);
                        Log.e("error", error.getCause().toString());
                        if (errorcode == 200) finish();
                    }
                });
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