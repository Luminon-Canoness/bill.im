package kr.edcan.billim;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
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
        no_billim = (TextView) findViewById(R.id.no_billim);
        sharedPreferences = getSharedPreferences("Billim", 0);
        editor = sharedPreferences.edit();

//        categorymenu = (ImageView) findViewById(R.id.category_select);
//        categorymenu.setOnClickListener(this);
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
            addDataToArrayList(type_icon, sharedPreferences.getString("name" + j, "" + 0), sharedPreferences.getString("comment" + j, "" + 0), (sharedPreferences.getInt("BorrowType" + j, -1) == 0) ? "빌려주세요" : "교환해요", sharedPreferences.getInt("IDValue" + j, 1000001));
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
//        groupList = new ArrayList<>();
//        childList = new ArrayList<>();
//        childListContent = new ArrayList<>();
//        drawer_expand_listview = (ExpandableListView) findViewById(R.id.drawer_expand_listview);
//        drawer_expand_listview.setAdapter(new ExpandAdapter(this, groupList, childList));
//        groupList.add("선린인터넷고등학교");
//        childListContent.add("EDCAN");
//        childListContent.add("한국디지털미디어고등학교");
//        childList.add(childListContent);
//        childList.add(childListContent);
//        drawer_expand_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                editor.putString("Group", view.findViewById(R.id.childName).toString());
//                Toast(view.findViewById(R.id.childName).toString(),1);
//            }
//        });

        group_selector = (Spinner) findViewById(R.id.groupSelector);
        groupList = new ArrayList<>();
        String[] group = {"선린인터넷고등학교","선린인터넷고등학교","선린인터넷고등학교","선린인터넷고등학교"};
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
                TextView s = (TextView) view.findViewById(R.id.id_value);
                if (position != 0) Toast(s.getText().toString(), 0);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.billim_post: {
                Intent post = new Intent(getApplicationContext(), SelectActivity.class);
                post.putExtra("ShareType", 0);
                startActivityForResult(post, 1234);
                break;
            }
            case R.id.trade_post: {
//                Intent post = new Intent(getApplicationContext(), SelectActivity.class);
//                post.putExtra("ShareType", 1);
//                startActivityForResult(post, 1234);
//                break;
                Intent asdf = new Intent(getApplicationContext(), ViewActivity.class);
                startActivity(asdf);
            }
//            case R.id.category_select: {
//                Intent list_category =  new Intent(getApplicationContext(), SelectActivity.class);
//                startActivityForResult(list_category,1234);
//            }
        }
    }
}
