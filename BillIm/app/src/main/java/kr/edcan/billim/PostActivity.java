package kr.edcan.billim;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;


public class PostActivity extends ActionBarActivity {

    FloatingActionButton postbutton;
    EditText name, comment, place, reward;
    ImageView post_type_image;
    TextView post_type_text;
    Intent intent;
    String shareType;

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
        setDefault();
    }
    public void setDefault() {
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        intent = getIntent();
        switch (intent.getIntExtra("ShareType", -1)) {
            case 0: {
                shareType = "빌리기";
                actionBar.setTitle(Html.fromHtml("<font color='#A4847E'><b>빌려주세요!</b> </font>"));
                name.setHint("빌릴 물건의 이름 입력");
                break;
            }
            case 1 : {
                shareType = "교환하기";
                actionBar.setTitle(Html.fromHtml("<font color='#A4847E'><b>교환해요!</b> </font>"));
                name.setHint("교환할 물건의 이름 입력");
                break;
            }
        }
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#d7ccc8")));
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        actionBar.setElevation(0);

        switch (intent.getIntExtra("Type", -1)) {
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

    }

    public void checkDialog() {
        final String item_name = name.getText().toString();
        final String item_comment = comment.getText().toString();
        final String item_place = place.getText().toString();
        final String item_reward = reward.getText().toString();
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
                                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                    i.putExtra("Name", item_name);
                                    i.putExtra("Comment", item_comment);
                                    i.putExtra("Place", item_place);
                                    i.putExtra("Reward", item_reward);
                                    i.putExtra("BorrowType", intent.getIntExtra("ShareType", -1));
                                    i.putExtra("State", 1);
                                    i.putExtra("Type", intent.getIntExtra("Type", -1));
                                    String s = ((int)(Math.random() *100000)+1) + "";
                                    ShortToast(s);
                                    i.putExtra("IDValue",Integer.parseInt(s));
                                    startActivity(i);
                                    finish();
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
}