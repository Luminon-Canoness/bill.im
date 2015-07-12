package kr.edcan.billim;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;


public class MyPageActivity extends ActionBarActivity {

    ListView mypageListview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);
        setDefault();
        setMypageListview();
    }

    public void setMypageListview() {
        mypageListview = (ListView) findViewById(R.id.mypage_listview);
        String name = "구창림"; // 개인정보 ㅡ 이름
        String group = "선린인터넷고등학교"; // 개인정보 ㅡ 그룹
        ArrayList<MyPageData> arrayList = new ArrayList<>();
        arrayList.add(new MyPageData(getApplicationContext(), R.drawable.ic_personal, name));
        arrayList.add(new MyPageData(getApplicationContext(), R.drawable.ic_group, group));
        arrayList.add(new MyPageData(getApplicationContext(), R.drawable.ic_logout, "로그아웃"));
        arrayList.add(new MyPageData(getApplicationContext(), R.drawable.ic_memberx, "회원탈퇴"));
        MyPageDataAdapter dataAdapter = new MyPageDataAdapter(getApplicationContext(), arrayList);
        mypageListview.setAdapter(dataAdapter);
    }

    public void setDefault() {
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#d7ccc8")));
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        actionBar.setElevation(0);
        actionBar.setTitle(Html.fromHtml("<font color=\"#8d6e63\"><b>마이페이지</b> </p>"));
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
}
