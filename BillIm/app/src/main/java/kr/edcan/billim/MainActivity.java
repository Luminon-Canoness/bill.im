package kr.edcan.billim;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.rey.material.widget.SnackBar;
import com.rey.material.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends ActionBarActivity implements View.OnClickListener {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    DrawerLayout drawerLayout;
    ListView list, navList;
    ArrayList<NavDrawData> navigtionBar;
    ArrayList<CData> arrayList;
    ArrayList<String> groupList = null;

    int icon[];
    TextView username, state;
    Spinner group_selector, categorySelect;
    int type_icon, type, borrowType, count;
    FloatingActionButton BillimButton, TradeButton;
    ImageView drawermenu;

    BillimService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setDefault();
        setCategorySelect();
        setData();
        setNavigationBar();
        setArrayList();
    }

    public void addDataToArrayList(int icon, String title, String description, String confirm, int value_id) {
        arrayList.add(new CData(getApplicationContext(), icon, title, description, confirm, value_id));
    }

    public void ActivityResult() {

    }

    public void setDefault() {
        sharedPreferences = getSharedPreferences("Billim", 0);
        editor = sharedPreferences.edit();
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawermenu = (ImageView) findViewById(R.id.drawer_menu);
        username = (TextView) findViewById(R.id.profile_name);
        username.setText(sharedPreferences.getString("Username", "Error"));
        // icon settings
        icon = new int[]{R.drawable.ic_pillgigu, R.drawable.ic_machine, R.drawable.ic_cloth, R.drawable.ic_books, R.drawable.ic_etc};
    }

    public void setArrayList() {
        group_selector = (Spinner) findViewById(R.id.groupSelector);
        groupList = new ArrayList<>();
        String[] group = {"선린인터넷고등학교", "EDCAN", "1학년5반", "ㅁㄴㅇㄹ"};
        for (int i = 0; i < group.length; i++) {
            groupList.add(group[i]);
        }
        group_selector.setAdapter(new ArrayAdapter<String>(this, R.layout.select_dialog_item_material, groupList));


    }

    public void Toast(String s, int length) {
        Toast.makeText(getApplicationContext(), s, (length == 0) ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG).show();
    }

    public void setNavigationBar() {
        navigtionBar = new ArrayList<>();
        navList = (ListView) findViewById(R.id.drawer_listview);
        NavDrawerAdapter navadapter = new NavDrawerAdapter(MainActivity.this, navigtionBar);
        navList.setAdapter(navadapter);
        navigtionBar.add(new NavDrawData(getApplicationContext(), "마이페이지", R.drawable.ic_mypage));
        navigtionBar.add(new NavDrawData(getApplicationContext(), "Bill.IM 사용내역", R.drawable.ic_naeyuk));
        navigtionBar.add(new NavDrawData(getApplicationContext(), "환경설정", R.drawable.ic_option));
        navList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        Intent mypage = new Intent(getApplicationContext(), MyPageActivity.class);
                        startActivity(mypage);
                        break;
                    case 1:
                        Intent usedHistory = new Intent(getApplicationContext(), UsedHistoryActivity.class);
                        startActivity(usedHistory);
                        break;
                    case 2:
                        Intent settings = new Intent(getApplicationContext(), SettingActivity.class);
                        startActivity(settings);
                        break;
                }
            }
        });
        drawermenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    public void setCategorySelect() {
        list = (ListView) findViewById(R.id.ListView);
        View header = getLayoutInflater().inflate(R.layout.listview_main_header, null, false);
        list.addHeaderView(header);
        categorySelect = (Spinner) findViewById(R.id.main_spinner);
        SpinnerAdapter spinnerAdapter = ArrayAdapter.createFromResource(MainActivity.this, R.array.category_array, R.layout.spinner_textstyle);
        categorySelect.setAdapter(spinnerAdapter);
        categorySelect.setOnItemClickListener(new Spinner.OnItemClickListener() {
            @Override
            public boolean onItemClick(Spinner spinner, View view, int i, long l) {
                // spinner.getSelectedItem()로 터치된 인자값을 받아옵니다
                return true;
            }
        });
    }

    public void setData() {
        arrayList = new ArrayList<>();
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("http://billim.kkiro.kr")
                .build();
        service = restAdapter.create(BillimService.class);
        BillimButton = (FloatingActionButton) findViewById(R.id.billim_post);
        BillimButton.setOnClickListener(this);
        TradeButton = (FloatingActionButton) findViewById(R.id.trade_post);
        TradeButton.setOnClickListener(this);
        // API fetch
        // 1은 현재 그룹 번호로 바꿔줘야됨 수고
        service.articleList(1, 65536, new Callback<List<Article>>() {
            @Override
            public void success(List<Article> articles, Response response) {
                for (Article article : articles) {
                    Log.e("name", article.name);
                    addDataToArrayList((icon.length > article.category) ? icon[article.category] : icon[0], article.name, article.description, (article.type == 0) ? "빌림" : "교환", article.id);
                }
                list.setAdapter(new DataAdapter(MainActivity.this, arrayList));
                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (position != 0) {
                            Intent viewIntent = new Intent(getApplicationContext(), ViewActivity.class);
                            viewIntent.putExtra("position", position);
                            startActivity(viewIntent);
                        }
                    }
                });
            }

            @Override
            public void failure(RetrofitError error) {
                Toast("어플리케이션 오류(" + error.getResponse().getStatus() + ")가 발생하였습니다", 0);

            }
        });


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.billim_post: {
                Intent post = new Intent(getApplicationContext(), SelectActivity.class);
                post.putExtra("ShareType", 0);
                startActivityForResult(post, 1000);
                break;
            }
            case R.id.trade_post: {
                Intent post = new Intent(getApplicationContext(), SelectActivity.class);
                post.putExtra("ShareType", 1);
                startActivityForResult(post, 1000);
                break;
            }
        }
    }

    class CData {
        private int icon, value_id;
        private String confirm;
        private String content_label;
        private String description;

        public CData(Context context, int icon_, String content_label_, String description_, String confirm_, int value_id_) {
            icon = icon_;
            confirm = confirm_;
            content_label = content_label_;
            description = description_;
            value_id = value_id_;
        }

        public int getValue_id() {
            return value_id;
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

    class NavDrawerAdapter extends ArrayAdapter<NavDrawData> {
        // 레이아웃 XML을 읽어들이기 위한 객체
        private LayoutInflater mInflater;

        public NavDrawerAdapter(Context context, ArrayList<NavDrawData> object) {
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
                view = mInflater.inflate(R.layout.listview_drawer_content, null);
            } else {
                view = v;
            }
            // 자료를 받는다.
            final NavDrawData data = this.getItem(position);
            if (data != null) {
                //화면 출력
                TextView name = (TextView) view.findViewById(R.id.navlist_text);
                ImageView icon = (ImageView) view.findViewById(R.id.navlist_icon);
                name.setText(data.getList());
                icon.setImageResource(data.getIcon());

            }
            return view;
        }
    }

    class DataAdapter extends ArrayAdapter<CData> {
        // 레이아웃 XML을 읽어들이기 위한 객체
        private LayoutInflater mInflater;

        public DataAdapter(Context context, ArrayList<CData> object) {
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
            final CData data = this.getItem(position);
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
                value_id.setText(data.getValue_id() + "");
            }
            return view;
        }
    }

    class NavDrawData {
        private String List;
        private int icon;

        public NavDrawData(Context context, String List_, int icon_) {
            List = List_;
            icon = icon_;
        }

        public String getList() {
            return List;
        }

        public int getIcon() {
            return icon;
        }
    }

}
