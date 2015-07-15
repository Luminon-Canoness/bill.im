package kr.edcan.billim;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import java.util.Arrays;

import kr.edcan.billim.utils.BillimService;
import kr.edcan.billim.utils.User;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class LoginActivity extends ActionBarActivity implements View.OnClickListener {

    protected CallbackManager callbackManager;
    protected AccessTokenTracker accessTokenTracker;
    protected AccessToken accessToken;
    protected String apikey,out;
    protected boolean enabled;
    protected boolean signingIn = false;
    protected User user;
    protected BillimService service;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    ImageView facebook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setDefault();
        apikey = sharedPreferences.getString("apikey","");
        if(!apikey.equals("")) {
            service.userSelfInfo(apikey, new Callback<User>() {
                @Override
                public void success(final User user, Response response) {
                    new Handler().postDelayed(new Runnable() {// 1 초 후에 실행
                        @Override
                        public void run() {
                            if (user.groups.size()==0){
                                Intent tutorial = new Intent(getApplicationContext(), Tutorial.class);
                                startActivity(tutorial);
                                finish();
                                //TODO 계정 정보 없을 시 튜토리얼
                            }else {
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    }, 500);
                }

                @Override
                public void failure(RetrofitError error) {
                    findViewById(R.id.progressBar).setVisibility(View.GONE);
                    findViewById(R.id.login_facebook).setVisibility(View.VISIBLE);
                }
            });
        } else {
            findViewById(R.id.progressBar).setVisibility(View.GONE);
            findViewById(R.id.login_facebook).setVisibility(View.VISIBLE);
        }
    }

    public void SignIn() {
        if(signingIn) return;
        signingIn = true;
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                loginResult.getAccessToken();
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {
                accessToken = currentAccessToken;
                Log.d("kr.kkiro.httptest", accessToken.getToken());
                service.loginByFacebook(accessToken.getToken(), new Callback<User>() {
                    @Override
                    public void failure(RetrofitError error) {
                        Log.e("kr.kkiro.httptest", error.getMessage());
                        Toast("로그인에 실패하였습니다", 0);
                        signingIn = false;
                    }

                    @Override
                    public void success(User user, Response response) {
                        signingIn = true;
                        Log.i("kr.kkiro.httptest", "Code" + response.getStatus());
                        LoginActivity.this.user = user;
                        if (user.enabled == false) {
                            Toast("정지된 사용자입니다. 관리자에게 문의해주세요", 0);
                        } else {
                            apikey = user.token;
                            out = user.name;
                            editor.putInt("userid", user.id);
                            editor.putString("Username", out);
                            editor.putString("apikey", apikey);
                            editor.commit();
                            if (user.groups == null || user.groups.size()==0) {
                                Intent tutorial = new Intent(getApplicationContext(), Tutorial.class);
                                startActivity(tutorial);
                                finish();
                                //TODO 계정 정보 없을 시 튜토리얼
                            }else {
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    }
                });
            }
        };
        // If the access token is available already assign it.
        accessToken = AccessToken.getCurrentAccessToken();
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));
    }

    public void setDefault() {
        sharedPreferences = getSharedPreferences("Billim", 0);
        editor = sharedPreferences.edit();
        facebook = (ImageView) findViewById(R.id.login_facebook);
        facebook.setOnClickListener(this);
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("http://billim.kkiro.kr")
                .build();
        service = restAdapter.create(BillimService.class);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(accessTokenTracker != null) accessTokenTracker.stopTracking();
    }

    public void Toast(String s, int length){
        Toast.makeText(getApplicationContext(),s,(length==0)?Toast.LENGTH_SHORT:Toast.LENGTH_LONG).show();
    }
    @Override
    public void onClick(View v) {
            SignIn();
    }
}