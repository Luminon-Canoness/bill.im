package kr.edcan.billim;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.List;

import kr.edcan.billim.utils.Article;
import kr.edcan.billim.utils.BillimService;
import kr.edcan.billim.utils.Group;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class GroupEditActivity extends Activity {


    ArrayList<GroupEditData> arrayList;
    int group_length;
    SharedPreferences sharedPreferences;
    String apikey;
    MaterialDialog materialDialog;
    View footer;
    int group_id;
    ListView groupEditListView;
    BillimService service;
    public static Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_edit);
        activity = this;
        setDefault();
        setArray();

    }

    public void setDefault() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("http://billim.kkiro.kr")
                .build();
        service = restAdapter.create(BillimService.class);
        sharedPreferences = getSharedPreferences("Billim", 0);
        groupEditListView = (ListView) findViewById(R.id.select_group_listview);
        apikey = sharedPreferences.getString("apikey", "");
    }

    public void setArray() {
        footer = getLayoutInflater().inflate(R.layout.listview_group_editor_footer, null, false);
        arrayList = new ArrayList<>();
        service.groupSelfList(apikey, new Callback<List<Group>>() {
            @Override
            public void success(List<Group> groups, Response response) {
                group_length = groups.size();
                for (Group group : groups) {
                    arrayList.add(new GroupEditData(getApplicationContext(), group.name, group.id));
                    Log.e("asdf", group.id + "");
                }
                GroupEditAdapter groupEditAdapter = new GroupEditAdapter(getApplicationContext(), arrayList);
                groupEditListView.setAdapter(groupEditAdapter);
                groupEditListView.addFooterView(footer);
                groupEditListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (position == group_length) {
                            startActivity(new Intent(GroupEditActivity.this, GroupAddActivity.class));
                            finish();
                        } else {
                            TextView s = (TextView) view.findViewById(R.id.group_edit_id);
                            Log.e("asadfasdfsad", s.getText().toString());
                            group_id = Integer.parseInt(s.getText().toString());
                            materialDialog = new MaterialDialog.Builder(GroupEditActivity.this)
                                    .title("그룹에서 나가시겠습니까?")
                                    .content("확인을 누르시면 그룹에서 탈퇴합니다.")
                                    .positiveText("확인")
                                    .negativeText("취소")
                                    .callback(new MaterialDialog.ButtonCallback() {
                                        @Override
                                        public void onPositive(MaterialDialog dialog) {
                                            super.onPositive(dialog);
                                            service.leaveGroup(apikey, group_id, new Callback<Article>() {
                                                @Override
                                                public void success(Article article, Response response) {
                                                    Toast.makeText(getApplicationContext(), "정상적으로 탈퇴되었습니다.",Toast.LENGTH_SHORT).show();
                                                    finish();
                                                }

                                                @Override
                                                public void failure(RetrofitError error) {
                                                    Log.e("에러", error.getResponse().getStatus() + "");
                                                    if(error.getResponse().getStatus() ==200) {
                                                        Toast.makeText(getApplicationContext(), "정상적으로 탈퇴되었습니다.",Toast.LENGTH_SHORT).show();
                                                        finish();
                                                    }
                                                }
                                            });
                                        }
                                    })
                                    .show();
                        }
                    }
                });
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }


    class GroupEditAdapter extends ArrayAdapter<GroupEditData> {
        // 레이아웃 XML을 읽어들이기 위한 객체
        private LayoutInflater mInflater;

        public GroupEditAdapter(Context context, ArrayList<GroupEditData> object) {
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
            final GroupEditData data = this.getItem(position);
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

    class GroupEditData {
        private String groupname;
        private int id;

        public GroupEditData(Context context, String groupname_, int id_) {
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
