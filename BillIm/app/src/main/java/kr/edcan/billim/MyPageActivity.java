package kr.edcan.billim;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.rey.material.app.Dialog;

import java.util.ArrayList;
import java.util.List;

import kr.edcan.billim.utils.BillimService;
import kr.edcan.billim.utils.DownloadImageTask;
import kr.edcan.billim.utils.Group;
import kr.edcan.billim.utils.RoundImageView;
import kr.edcan.billim.utils.User;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class MyPageActivity extends ActionBarActivity {

    ListView mypageListview;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String apikey, group, name, photo;
    BillimService service;
    ArrayList<MyPageData> arrayList;
    RoundImageView profilePhoto;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);
        setDefault();
        setMypageListview();
    }

    public void setDefault() {
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#d7ccc8")));
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        actionBar.setElevation(0);
        actionBar.setTitle(Html.fromHtml("<font color=\"#8d6e63\"><b>마이페이지</b> </p>"));
        sharedPreferences = getSharedPreferences("Billim", 0);
        editor = sharedPreferences.edit();
        apikey = sharedPreferences.getString("apikey", "");
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("http://billim.kkiro.kr")
                .build();
        service = restAdapter.create(BillimService.class);
    }

    public void setMypageListview() {
        View header = getLayoutInflater().inflate(R.layout.mypage_header, null, false);
        mypageListview = (ListView) findViewById(R.id.mypage_listview);
        name = sharedPreferences.getString("Username", ""); // 개인정보 - 이름
        TextView username = (TextView)header.findViewById(R.id.username);
        username.setText(name);
        mypageListview.addHeaderView(header);
        arrayList = new ArrayList<>();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("잠시만 기다려주세요");
        progressDialog.show();
        service.groupSelfList(apikey, new Callback<List<Group>>() {
            @Override
            public void success(List<Group> groups, Response response) {
                progressDialog.dismiss();
                arrayList.add(new MyPageData(getApplicationContext(), R.drawable.ic_group, "그룹 삭제 혹은 추가"));
                arrayList.add(new MyPageData(getApplicationContext(), R.drawable.ic_logout, "로그아웃"));
                arrayList.add(new MyPageData(getApplicationContext(), R.drawable.ic_memberx, "회원탈퇴"));

                MyPageDataAdapter dataAdapter = new MyPageDataAdapter(getApplicationContext(), arrayList);
                mypageListview.setAdapter(dataAdapter);
                mypageListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        switch (position) {
                            case 1:
                                startActivity( new Intent(MyPageActivity.this, GroupEditActivity.class));
                                break;
                            case 2:
                                final Dialog dialog = new Dialog(MyPageActivity.this);
                                dialog.setTitle("로그아웃 하시겠습니까?");
                                dialog.setCancelable(true);
                                dialog.positiveAction("확인");
                                dialog.negativeAction("취소");
                                dialog.negativeActionClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
                                    }
                                });
                                dialog.positiveActionClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        editor.remove("apikey");
                                        editor.apply();
                                        startActivity(new Intent(MyPageActivity.this, LoginActivity.class));
                                        MainActivity.activity.finish();
                                        finish();
                                    }
                                });
                                dialog.show();
                                break;
                            case 3:
                                Toast.makeText(getApplicationContext(), "잠시만요 이건 아직임", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                });
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(getApplicationContext(), "SHIT", Toast.LENGTH_SHORT).show();
            }
        });
        service.userSelfInfo(apikey, new Callback<User>() {
            @Override
            public void success(User user, Response response) {
                photo = user.photo;
                profilePhoto = (RoundImageView)findViewById(R.id.mypage_profile_photo);
                if (photo != null) {
                    new DownloadImageTask(profilePhoto).execute(photo);
                    Bitmap bitmap = ((BitmapDrawable) profilePhoto.getDrawable()).getBitmap();
                    profilePhoto.setImageBitmap(getRoundedShape(bitmap));
                } else Log.e("에러", "Error loading profile picture");
            }

            @Override
            public void failure(RetrofitError error) {
                Toast("세션이 만료됨, 다시 로그인하세요", 0);
                Log.e("에러코드", "" + error.getResponse().getStatus());
            }
        });
    }


    class MyPageDataAdapter extends ArrayAdapter<MyPageData> {
        // 레이아웃 XML을 읽어들이기 위한 객체
        private LayoutInflater mInflater;

        public MyPageDataAdapter(Context context, ArrayList<MyPageData> object) {
            // 상위 클래스의 초기화 과정
            // context, 0, 자료구조
            super(context, 0, object);
            mInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        // 보여지는 스타일을 자신이 만든 xml로 보이기 위한 구문
        @Override
        public View getView(int position, View v, ViewGroup parent) {
            View view = null;
            // 현재 리스트의 하나의 항목에 보일 컨트롤 얻기
            if (v == null) {
                // XML 레이아웃을 직접 읽어서 리스트뷰에 넣음
                view = mInflater.inflate(R.layout.listview_mypage_content, null);
            } else {
                view = v;
            }
            // 자료를 받는다.
            final MyPageData data = this.getItem(position);
            if (data != null) {
                //화면 출력
                ImageView icon = (ImageView) view.findViewById(R.id.mypage_item_pic);
                TextView text = (TextView) view.findViewById(R.id.mypage_item_text);
                icon.setImageResource(data.getIcon());
                text.setText(data.getText());
            }
            return view;
        }
    }

    public void Toast(String s, int length) {
        Toast.makeText(getApplicationContext(), s, (length == 0) ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG).show();
    }

    public Bitmap getRoundedShape(Bitmap scaleBitmapImage) {
        int targetWidth = 50;
        int targetHeight = 50;
        Bitmap targetBitmap = Bitmap.createBitmap(targetWidth,
                targetHeight, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(targetBitmap);
        Path path = new Path();
        path.addCircle(((float) targetWidth - 1) / 2,
                ((float) targetHeight - 1) / 2,
                (Math.min(((float) targetWidth),
                        ((float) targetHeight)) / 2),
                Path.Direction.CCW);

        canvas.clipPath(path);
        Bitmap sourceBitmap = scaleBitmapImage;
        canvas.drawBitmap(sourceBitmap,
                new Rect(0, 0, sourceBitmap.getWidth(),
                        sourceBitmap.getHeight()),
                new Rect(0, 0, targetWidth, targetHeight), null);
        return targetBitmap;
    }
    class MyPageData {
        private int icon;
        private String text;

        public MyPageData(Context context, int icon_, String text_) {
            icon = icon_;
            text = text_;
        }

        public int getIcon() {
            return icon;
        }

        public String getText() {
            return text;
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
