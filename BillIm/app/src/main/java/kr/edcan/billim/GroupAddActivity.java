package kr.edcan.billim;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.afollestad.materialdialogs.MaterialDialog;
import com.getbase.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import kr.edcan.billim.utils.Article;
import kr.edcan.billim.utils.BillimService;
import kr.edcan.billim.utils.CustomDialog;
import kr.edcan.billim.utils.Group;
import kr.edcan.billim.utils.GroupResponse;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class GroupAddActivity extends ActionBarActivity implements View.OnClickListener {
    String apikey;
    BillimService service;
    SharedPreferences sharedPreferences;
    ListView groupAddListView;
    ImageView search;
    ArrayList<GroupAddData> arrayList;
    String groupname_;
    String creategroupname;
    EditText search_edittext;
    TextView groupname, addid;
    FloatingActionButton floatingActionButton;

    public static Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_add);
        setDefault();
        activity = this;
    }

    public void setDefault() {
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#D4C6C3")));
        actionBar.setTitle(Html.fromHtml("<font color=\"#8d6e63\"><b>단체 찾기</b> </p>"));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
            actionBar.setElevation(0);
        }
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("http://billim.kkiro.kr")
                .build();
        service = restAdapter.create(BillimService.class);
        sharedPreferences = getSharedPreferences("Billim", 0);
        groupAddListView = (ListView) findViewById(R.id.group_add_listview);
        apikey = sharedPreferences.getString("apikey", "");
        search = (ImageView) findViewById(R.id.group_add_search);
        search_edittext = (EditText) findViewById(R.id.group_add_edit);
        search.setOnClickListener(this);
        floatingActionButton = (FloatingActionButton)findViewById(R.id.group_add_float_button);
        floatingActionButton.setOnClickListener(this);
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

    public void setGroupSearch() {
        String query = search_edittext.getText().toString();
        if (query.trim().equals("")) search_edittext.setError("공백 없이 입력해주세요!");
        else {
            Log.e("adsf", query);
            service.searchGroup(query, new Callback<List<Group>>() {
                @Override
                public void success(List<Group> groups, Response response) {
                    arrayList = new ArrayList<>();
                    if (groups.size() != 0) {
                        for (Group group : groups) {
                            arrayList.add(new GroupAddData(getApplicationContext(), group.name, group.id));
                        }
                        GroupAddAdapter groupAddAdapter = new GroupAddAdapter(GroupAddActivity.this, arrayList);
                        groupAddListView.setAdapter(groupAddAdapter);
                        groupAddListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                                addid = (TextView) view.findViewById(R.id.group_edit_id);
                                groupname = (TextView) view.findViewById(R.id.group_edit_text);
                                groupname_ = groupname.getText().toString();
                                MaterialDialog materialDialog = new MaterialDialog.Builder(GroupAddActivity.this)
                                        .title(groupname_)
                                        .content("확인을 누르시면 그룹에 가입합니다.")
                                        .positiveText("확인")
                                        .negativeText("취소")
                                        .callback(new MaterialDialog.ButtonCallback() {
                                            @Override
                                            public void onPositive(MaterialDialog dialog) {
                                                super.onPositive(dialog);
                                                int groupid = Integer.parseInt(addid.getText().toString());
                                                service.joinGroup(apikey, groupid, new Callback<GroupResponse>() {
                                                    @Override
                                                    public void success(GroupResponse group, Response response) {
                                                        Toast.makeText(getApplicationContext(), groupname_ + " 그룹에 추가되었습니다!", Toast.LENGTH_SHORT).show();
                                                        finish();
                                                    }

                                                    @Override
                                                    public void failure(RetrofitError error) {
                                                        if (error.getResponse().getStatus() == 422) {
                                                            Toast.makeText(getApplicationContext(), "이미 가입된 그룹입니다.", Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            Log.e("Add error", error.getResponse().getStatus() + "");
                                                            Toast.makeText(getApplicationContext(), "내부 오류가 발생하였습니다..", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            }

                                        })
                                        .show();
                            }
                        });
                    } else
                        Toast.makeText(getApplicationContext(), "검색된 그룹이 없습니다!", Toast.LENGTH_SHORT).show();
                }


                @Override
                public void failure(RetrofitError error) {
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.group_add_search : setGroupSearch();
                break;
            case R.id.group_add_float_button :
                   startActivity(new Intent(getApplicationContext(), CustomDialog.class));
        }
    }

    class GroupAddAdapter extends ArrayAdapter<GroupAddData> {
        // 레이아웃 XML을 읽어들이기 위한 객체
        private LayoutInflater mInflater;

        public GroupAddAdapter(Context context, ArrayList<GroupAddData> object) {
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
                view = mInflater.inflate(R.layout.listview_group_editor, null);
            } else {
                view = v;
            }
            // 자료를 받는다.
            final GroupAddData data = this.getItem(position);
            if (data != null) {
                //화면 출력
                TextView id = (TextView) view.findViewById(R.id.group_edit_id);
                TextView text = (TextView) view.findViewById(R.id.group_edit_text);
                text.setText(data.getGroupname());
                id.setText(data.getId() + "");
            }
            return view;
        }
    }

    class GroupAddData {
        private String groupname;
        private int id;

        public GroupAddData(Context context, String groupname_, int id_) {
            groupname = groupname_;
            id = id_;
        }

        public String getGroupname() {
            return groupname;
        }

        public int getId() {
            return id;
        }
    }
}