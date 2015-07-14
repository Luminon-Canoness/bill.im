package kr.edcan.billim;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
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
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.rey.material.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import kr.edcan.billim.adapter.CData;
import kr.edcan.billim.adapter.DataAdapter;
import kr.edcan.billim.adapter.NavDrawData;
import kr.edcan.billim.adapter.NavDrawerAdapter;
import kr.edcan.billim.utils.Article;
import kr.edcan.billim.utils.BillimService;
import kr.edcan.billim.utils.DownloadImageTask;
import kr.edcan.billim.utils.Group;
import kr.edcan.billim.utils.RoundImageView;
import kr.edcan.billim.utils.User;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends ActionBarActivity implements View.OnClickListener {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    DrawerLayout drawerLayout;
    LinearLayout linearLayout, floatMenuBackground;
    ListView list, navList;
    ArrayList<NavDrawData> navigtionBar;
    ArrayList<CData> arrayList;
    ArrayList<String> groupList = null;
    String photo, phone, description;
    long mBackPressed;
    int icon[], give, take, exchange, isPopup = 0;
    TextView username, state;
    Spinner group_selector, categorySelect;
    FloatingActionButton BillimGive, BillimTake, BillimFree, BillimTrade;
    FloatingActionsMenu BillimMenu;
    ImageView drawermenu;
    RoundImageView profilePhoto;
    BillimService service;
    String apikey;
    public static Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = this;
        setDefault();
        setProfilePhoto();
        setCategorySelect();
        setNavigationBar();
    }

    public void setDefault() {
        sharedPreferences = getSharedPreferences("Billim", 0);
        editor = sharedPreferences.edit();

        profilePhoto = (RoundImageView) findViewById(R.id.profile_photo);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawermenu = (ImageView) findViewById(R.id.drawer_menu);
        username = (TextView) findViewById(R.id.profile_name);
        state = (TextView) findViewById(R.id.profile_state);
        BillimMenu = (FloatingActionsMenu) findViewById(R.id.float_menu);
        BillimGive = (FloatingActionButton) findViewById(R.id.billim_give_post);
        BillimTake = (FloatingActionButton) findViewById(R.id.billim_take_post);
        BillimTrade = (FloatingActionButton) findViewById(R.id.billim_trade_post);
        BillimFree = (FloatingActionButton) findViewById(R.id.billim_free_post);
        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
        floatMenuBackground = (LinearLayout) findViewById(R.id.float_menu_background);
        linearLayout.setOnClickListener(this);
        BillimGive.setOnClickListener(this);
        BillimTake.setOnClickListener(this);
        BillimTrade.setOnClickListener(this);
        BillimFree.setOnClickListener(this);
        floatMenuBackground.setOnClickListener(this);
        BillimGive.setTitle("빌려드려요!");
        BillimTake.setTitle("빌려주세요!");
        BillimTrade.setTitle("교환해요!");
        BillimFree.setTitle("드려요!");
        username.setText(sharedPreferences.getString("Username", "Error"));
        BillimMenu.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
            @Override
            public void onMenuExpanded() {
                floatMenuBackground.setVisibility(View.VISIBLE);
                floatMenuBackground.setEnabled(true);
            }

            @Override
            public void onMenuCollapsed() {
                floatMenuBackground.setVisibility(View.GONE);
                floatMenuBackground.setEnabled(false);
            }
        });
        apikey = sharedPreferences.getString("apikey", "");
        // icon settings
        icon = new int[]{R.drawable.ic_pillgigu, R.drawable.ic_machine, R.drawable.ic_cloth, R.drawable.ic_books, R.drawable.ic_etc};

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
        // API fetch
        // 1은 현재 그룹 번호로 바꿔줘야됨 수고
        service.userSelfInfo(apikey, new Callback<User>() {
            @Override
            public void success(User user, Response response) {
                photo = user.photo;
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
        service.articleList(1, 100000000, new Callback<List<Article>>() {
            @Override
            public void success(List<Article> articles, Response response) {
                for (Article article : articles) {
                    addDataToArrayList(article.id, (icon.length > article.category) ? icon[article.category] : icon[0], article.name, article.description, (article.type == 0) ? "빌림" : "교환");
                }
                list.setAdapter(new DataAdapter(MainActivity.this, arrayList));
                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (position != 0) {
                            TextView textView = (TextView)findViewById(R.id.id_value);
                            Intent viewIntent = new Intent(getApplicationContext(), ViewActivity.class);
                            viewIntent.putExtra("ID", Integer.parseInt(textView.getText().toString()));
                            startActivity(viewIntent);
                        }
                    }
                });
            }

            @Override
            public void failure(RetrofitError error) {
                Toast("세션이 만료됨, 다시 로그인하세요", 0);
                Log.e("에러코드", "" + error.getResponse().getStatus());
            }
        });
    }

    public void setProfilePhoto() {
        // API fetch
        // 1은 현재 그룹 번호로 바꿔줘야됨 수고
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("http://billim.kkiro.kr")
                .build();
        service = restAdapter.create(BillimService.class);
        service.userSelfInfo(apikey, new Callback<User>() {
            @Override
            public void success(User user, Response response) {
                phone = user.phone;
                description = user.description;
                give = user.give;
                take = user.take;
                exchange = user.exchange;
                state.setText(take + "번 빌림 " + give + "번 빌려줌 " + exchange + "번 교환");
            }

            @Override
            public void failure(RetrofitError error) {
                Toast("세션이 만료됨, 다시 로그인하세요", 0);
                Log.e("에러코드", "" + error.getResponse().getStatus());
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
                Toast("세션이 만료됨, 다시 로그인하세요", 0);
                Log.e("에러코드", "" + error.getResponse().getStatus());
            }
        });
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

    public void addDataToArrayList(int id, int icon, String title, String description, String confirm) {
        arrayList.add(new CData(getApplicationContext(), id, icon, title, description, confirm));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.billim_give_post: {
                // 빌림버튼
                Intent post = new Intent(getApplicationContext(), SelectActivity.class);
                post.putExtra("ShareType", 0);
                startActivityForResult(post, 1000);
                break;
            }
            case R.id.billim_take_post: {
                // 교환버튼
                Intent post = new Intent(getApplicationContext(), SelectActivity.class);
                post.putExtra("ShareType", 1);
                startActivityForResult(post, 1000);
                break;
            }
            case R.id.billim_free_post: {
                // 교환버튼
                Intent post = new Intent(getApplicationContext(), SelectActivity.class);
                post.putExtra("ShareType", 2);
                startActivityForResult(post, 1000);
                break;
            }
            case R.id.billim_trade_post: {
                // 교환버튼
                Intent post = new Intent(getApplicationContext(), SelectActivity.class);
                post.putExtra("ShareType", 3);
                startActivityForResult(post, 1000);
                break;
            }
            case R.id.float_menu_background:
                BillimMenu.collapse();
                break;
            case R.id.drawer_menu:
                // Drawer메뉴 오픈버튼
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.linearLayout:
                // Drawer 메뉴 오픈 시 터치 이벤트를 대신 가져갈 레이어, 절대 삭제 금
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (mBackPressed + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
            android.os.Process.killProcess(android.os.Process.myPid());
            return;
        } else
            Toast.makeText(getApplicationContext(), "다시 한번 누르면 종료됩니다", Toast.LENGTH_SHORT).show();
        mBackPressed = System.currentTimeMillis();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        super.onBackPressed();
        return true;
    }

    public void onPause(){
        super.onPause();
        BillimMenu.collapse();
        floatMenuBackground.setVisibility(View.GONE);

    }
    public void onResume() {
        super.onResume();
        setData();
        setArrayList();
        }
}
