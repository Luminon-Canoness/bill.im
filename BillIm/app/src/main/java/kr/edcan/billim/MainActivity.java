package kr.edcan.billim;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
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
    LinearLayout linearLayout;
    ListView list, navList;
    ArrayList<NavDrawData> navigtionBar;
    ArrayList<CData> arrayList;
    ArrayList<String> groupList = null;

    int icon[];
    TextView username;
    Spinner group_selector, categorySelect;
    FloatingActionButton BillimButton, TradeButton;
    ImageView drawermenu;
    BillimService service;
    String apikey;
    public static Activity activity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = this;
        setDefault();
        setCategorySelect();
        setData();
        setNavigationBar();
        setArrayList();
    }
    public void setDefault() {
        sharedPreferences = getSharedPreferences("Billim", 0);
        editor = sharedPreferences.edit();
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawermenu = (ImageView) findViewById(R.id.drawer_menu);
        username = (TextView) findViewById(R.id.profile_name);
        username.setText(sharedPreferences.getString("Username", "Error"));
        apikey = sharedPreferences.getString("apikey", "");
        // icon settings
        icon = new int[]{R.drawable.ic_pillgigu, R.drawable.ic_machine, R.drawable.ic_cloth, R.drawable.ic_books, R.drawable.ic_etc};
        linearLayout = (LinearLayout)findViewById(R.id.linearLayout);
        linearLayout.setOnClickListener(this);
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
        TradeButton = (FloatingActionButton) findViewById(R.id.trade_post);
        BillimButton.setOnClickListener(this);
        TradeButton.setOnClickListener(this);
        // API fetch
        // 1은 현재 그룹 번호로 바꿔줘야됨 수고
        service.articleList(1, 100000000, new Callback<List<Article>>() {
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
                        startActivityForResult(mypage, 1234);
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
        drawermenu.setOnClickListener(this);
        drawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                findViewById(R.id.linearLayout).setVisibility(View.VISIBLE);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                findViewById(R.id.linearLayout).setVisibility(View.GONE);
            }

            @Override
            public void onDrawerStateChanged(int newState) {
            }
        });
    }
    public void setArrayList() {
        group_selector = (Spinner) findViewById(R.id.groupSelector);
        groupList = new ArrayList<>();
        service.groupSelfList(apikey, new Callback<List<Group>>() {
            @Override
            public void success(List<Group> groups, Response response) {
                for (Group group : groups) {
                    groupList.add(group.name);
                }
                group_selector.setAdapter(new ArrayAdapter<>(MainActivity.this, R.layout.select_dialog_item_material, groupList));
            }

            @Override
            public void failure(RetrofitError error) {
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        });
    }
    public void Toast(String s, int length) {
        Toast.makeText(getApplicationContext(), s, (length == 0) ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG).show();
    }

    public void addDataToArrayList(int icon, String title, String description, String confirm, int value_id) {
        arrayList.add(new CData(getApplicationContext(), icon, title, description, confirm, value_id));
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.billim_post: {
                // 빌림버튼
                Intent post = new Intent(getApplicationContext(), SelectActivity.class);
                post.putExtra("ShareType", 0);
                startActivityForResult(post, 1000);
                break;
            }
            case R.id.trade_post: {
                // 교환버튼
                Intent post = new Intent(getApplicationContext(), SelectActivity.class);
                post.putExtra("ShareType", 1);
                startActivityForResult(post, 1000);
                break;
            }
            case R.id.drawer_menu:
                // Drawer메뉴 오픈버튼
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.linearLayout:
                // Drawer 메뉴 오픈 시 터치 이벤트를 대신 가져갈 레이어, 절대 삭제 금
                break;
        }
    }
    public void onResume(){
        super.onResume();
        setData();
    }
}
