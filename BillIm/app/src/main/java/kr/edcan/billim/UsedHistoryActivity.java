package kr.edcan.billim;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import kr.edcan.billim.adapter.CData;
import kr.edcan.billim.utils.Article;
import kr.edcan.billim.utils.BillimService;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class UsedHistoryActivity extends ActionBarActivity {

    ArrayList<UsedHistoryData> arrayList;
    BillimService service;
    SharedPreferences sharedPreferences;
    String apikey;
    ListView usedhistoryListview;
    int[] icon;
    String[] postType, state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usedhistory);
        setDefault();
        setService();
        setArray();
        postType = new String[]{"빌려드림", "빌림", "드림", "교환"};
    }

    public void setService() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("http://billim.kkiro.kr")
                .build();
        service = restAdapter.create(BillimService.class);
        sharedPreferences = getSharedPreferences("Billim", 0);
        apikey = sharedPreferences.getString("apikey", "");
    }

    public void setDefault() {
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#d7ccc8")));
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        actionBar.setElevation(0);
        actionBar.setTitle(Html.fromHtml("<font color=\"#8d6e63\">Bill.IM <b>사용내역</b> </p>"));
        usedhistoryListview = (ListView) findViewById(R.id.used_history_listview);
        icon = new int[]{R.drawable.ic_pillgigu, R.drawable.ic_machine, R.drawable.ic_cloth, R.drawable.ic_books, R.drawable.ic_etc};
        state= new String[]{"대기 중", "삭제 됨", "승인", "빌려줌", "완료"};
    }

    public void setArray() {
        arrayList = new ArrayList<>();
        service.articleSelfList(apikey, -1, new Callback<List<Article>>() {
            @Override
            public void success(List<Article> articles, Response response) {
                for (Article article : articles) {
                    arrayList.add(new UsedHistoryData(getApplicationContext(), article.id, (icon.length > article.category) ? icon[article.category] : icon[0], article.name + " ("+state[article.state]+")", article.description, postType[article.type]));

                }
                UsedHistoryAdapter usedHistoryAdapter = new UsedHistoryAdapter(UsedHistoryActivity.this, arrayList);
                usedhistoryListview.setAdapter(usedHistoryAdapter);
                usedhistoryListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        TextView textView = (TextView) view.findViewById(R.id.id_value);
                        Intent viewIntent = new Intent(getApplicationContext(), ViewActivity.class);
                        viewIntent.putExtra("ID", Integer.parseInt(textView.getText().toString()));
                        startActivity(viewIntent);

                    }
                });
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    class UsedHistoryAdapter extends ArrayAdapter<UsedHistoryData> {
        // 레이아웃 XML을 읽어들이기 위한 객체
        private LayoutInflater mInflater;

        public UsedHistoryAdapter(Context context, ArrayList<UsedHistoryData> object) {
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
                view = mInflater.inflate(R.layout.listview_main_content, null);
            } else {
                view = v;
            }
            // 자료를 받는다.
            final UsedHistoryData data = this.getItem(position);
            if (data != null) {
                //화면 출력
                TextView name = (TextView) view.findViewById(R.id.name);
                TextView description = (TextView) view.findViewById(R.id.description);
                TextView confirm = (TextView) view.findViewById(R.id.confirm);
                TextView value_id = (TextView) view.findViewById(R.id.id_value);
                ImageView logo = (ImageView) view.findViewById(R.id.icon);
                name.setText(data.getContent_label());
                description.setText(data.getDescription());
                logo.setImageResource(data.getIcon());
                confirm.setText(data.getConfirm());
                value_id.setText(data.getId() + "");
            }
            return view;
        }
    }

    class UsedHistoryData {
        private int icon;
        private String confirm;
        private String content_label;
        private String description;
        private int id;

        public UsedHistoryData(Context context, int id_, int icon_, String content_label_, String description_, String confirm_) {
            id = id_;
            icon = icon_;
            confirm = confirm_;
            content_label = content_label_;
            description = description_;
        }

        public int getId() {
            return id;
        }

        public int getIcon() {
            return icon;
        }

        public String getConfirm() {
            return confirm;
        }

        public String getContent_label() {
            return content_label;
        }

        public String getDescription() {
            return description;
        }
    }
}