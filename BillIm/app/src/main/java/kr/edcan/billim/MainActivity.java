package kr.edcan.billim;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.rey.material.widget.Spinner;

import java.util.ArrayList;

public class MainActivity extends ActionBarActivity implements View.OnClickListener {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    DrawerLayout drawerLayout;
    ListView list;
    ListView navList;
    ExpandableListView drawer_expand_listview;
    ArrayList<NavDrawData> navigtionBar;
    ArrayList<CData> arrayList;
    ArrayList<String> groupList = null;
    ArrayList<ArrayList<String>> childList = null;
    ArrayList<String> childListContent = null;

    Spinner group_selector;
    int type_icon, type, borrowType, count;
    TextView no_billim;
    FloatingActionButton BillimButton, TradeButton;
    ImageView drawermenu, categorymenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setDefault();
        setNavigationBar();
        setArrayList();
        setCategorySelect();
        no_billim = (TextView) findViewById(R.id.no_billim);
        sharedPreferences = getSharedPreferences("Billim", 0);
        editor = sharedPreferences.edit();

        BillimButton = (FloatingActionButton) findViewById(R.id.billim_post);
        BillimButton.setOnClickListener(this);
        TradeButton = (FloatingActionButton) findViewById(R.id.trade_post);
        TradeButton.setOnClickListener(this);
        Intent i = getIntent();
        if (i.getIntExtra("State", 0) == 1) {
            count = sharedPreferences.getInt("Count", 0);
            editor.putString("name" + count, i.getStringExtra("Name"));
            editor.putString("comment" + count, i.getStringExtra("Comment"));
            editor.putInt("Count", count + 1);
            editor.putInt("Type" + count, i.getIntExtra("Type", -1));
            editor.putInt("BorrowType" + count, i.getIntExtra("BorrowType", -1));
            editor.putInt("IDValue" + count, i.getIntExtra("IDValue", 100001));
            editor.commit();
            i.removeExtra("State");
        }
        int j;
        for (j = 0; j < sharedPreferences.getInt("Count", 0); j++) {
            if (type == -1 || borrowType == -1) {
                Toast("어플리케이션 오류가 발생하였습니다", 0);
                break;
            }
            switch (sharedPreferences.getInt("Type" + j, -1)) {
                case 0: {
                    type_icon = R.drawable.ic_pillgigu;
                    break;
                }
                case 1: {
                    type_icon = R.drawable.ic_machine;
                    break;
                }
                case 2: {
                    type_icon = R.drawable.ic_food;
                    break;
                }
                case 3: {
                    type_icon = R.drawable.ic_cloth;
                    break;
                }
                case 4: {
                    type_icon = R.drawable.ic_books;
                    break;
                }
                case 5: {
                    type_icon = R.drawable.ic_etc;
                    break;
                }
            }
            addDataToArrayList(type_icon, sharedPreferences.getString("name" + j, "" + 0), sharedPreferences.getString("comment" + j, "" + 0), (sharedPreferences.getInt("BorrowType" + j, -1) == 0) ? "빌림" : "교환", sharedPreferences.getInt("IDValue" + j, 1000001));
        }
    }

    public void addDataToArrayList(int icon, String title, String description, String confirm, int value_id) {
        arrayList.add(new CData(getApplicationContext(), icon, title, description, confirm, value_id));
    }

    public void setDefault() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawermenu = (ImageView) findViewById(R.id.drawer_menu);
    }

    public void setArrayList() {
        group_selector = (Spinner) findViewById(R.id.groupSelector);
        groupList = new ArrayList<>();
        String[] group = {"선린인터넷고등학교","EDCAN","1학년5반","ㅁㄴㅇㄹ"};
        for(int i=0;i<group.length;i++){
            groupList.add(group[i]);
        }

        group_selector.setAdapter(new ArrayAdapter<String>(this, R.layout.select_dialog_item_material,groupList));
        arrayList = new ArrayList<>();
        list = (ListView) findViewById(R.id.ListView);
        View header = getLayoutInflater().inflate(R.layout.listview_main_header, null, false);
        list.addHeaderView(header);
        list.setAdapter(new DataAdapter(this, arrayList));
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    Intent viewIntent = new Intent(getApplicationContext(), ViewActivity.class);
                    viewIntent.putExtra("position", position);
                    startActivity(viewIntent);
                };
            }
        });
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

    public void setCategorySelect(){
        final Spinner categorySelect = (Spinner)findViewById(R.id.main_spinner);
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
                startActivityForResult(post,1000);
                break;
            }
        }
    }
    class CData {
        private int icon, value_id;
        private String confirm;
        private String content_label;
        private String description;

        public CData(Context context, int icon_,  String content_label_, String description_, String confirm_, int value_id_) {
            icon = icon_;
            confirm = confirm_;
            content_label = content_label_;
            description = description_;
            value_id = value_id_;
        }

        public int getValue_id() {return value_id;}
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

        public int getIcon(){
            return icon;
        }
    }

}
