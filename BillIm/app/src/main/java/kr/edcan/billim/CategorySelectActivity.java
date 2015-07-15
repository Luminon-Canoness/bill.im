package kr.edcan.billim;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


public class CategorySelectActivity extends Activity{

    int shareType;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);
        shareType = getIntent().getIntExtra("ShareType", -1);
        setSelectList();
    }


    public void setSelectList() {
        listView = (ListView) findViewById(R.id.select_dialog_listview);
        ArrayList<SelecterData> arrayList = new ArrayList<>();
        arrayList.add(new SelecterData(getApplicationContext(), R.drawable.ic_pillgigu, "필기구"));
        arrayList.add(new SelecterData(getApplicationContext(), R.drawable.ic_machine, "전자제품"));
        arrayList.add(new SelecterData(getApplicationContext(), R.drawable.ic_food, "음식 및 음료"));
        arrayList.add(new SelecterData(getApplicationContext(), R.drawable.ic_cloth, "의류"));
        arrayList.add(new SelecterData(getApplicationContext(), R.drawable.ic_books, "책 및 문서"));
        arrayList.add(new SelecterData(getApplicationContext(), R.drawable.ic_etc, "기타"));
        SelecterAdapter selecterAdapter = new SelecterAdapter(getApplicationContext(), arrayList);
        listView.setAdapter(selecterAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), PostActivity.class);
                intent.putExtra("Type", position);
                intent.putExtra("ShareType", shareType);
                finishActivity(1000);
                startActivity(intent);
                finish();
            }
        });
    }


    class SelecterData {
        private int icon;
        private String content;
        public SelecterData(Context context, int icon_, String content_) {
            icon = icon_;
            content = content_;
        }

        public int getIcon() {
            return icon;
        }

        public String getContent() {
            return content;
        }
    }

    class SelecterAdapter extends ArrayAdapter<SelecterData> {
        // 레이아웃 XML을 읽어들이기 위한 객체
        private LayoutInflater mInflater;

        public SelecterAdapter(Context context, ArrayList<SelecterData> object) {
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
                view = mInflater.inflate(R.layout.listview_select_content, null);
            } else {
                view = v;
            }
            // 자료를 받는다.
            final SelecterData data = this.getItem(position);
            if (data != null) {
                //화면 출력
                TextView name = (TextView) view.findViewById(R.id.select_text);
                ImageView icon = (ImageView) view.findViewById(R.id.select_icon);
                name.setText(data.getContent());
                icon.setImageResource(data.getIcon());
            }
            return view;
        }
    }
}
