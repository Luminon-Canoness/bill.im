package kr.edcan.billim;

import java.util.List;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * Created by kotohana5706 on 15. 7. 12.
 */
public interface BillimService {
    @FormUrlEncoded
    @POST("/api/auth/facebook/token")
    public void loginByFacebook(@Field("access_token") String token, Callback<User> callback);

    @FormUrlEncoded
    @POST("/api/auth/logout")
    public void logout(@Field("apikey") String apikey, Callback callback);

    @FormUrlEncoded
    @POST("/api/user/self/info")
    public void userSelfInfo(@Field("apikey") String apikey, Callback<User> callback);

    @FormUrlEncoded
    @POST("/api/article/list")
    public void articleList(@Field("group") int group, @Field("start") int start, Callback<List<Article>> callback);

    @FormUrlEncoded
    @POST("/api/group/self/list")
    public void groupSelfList(@Field("apikey") String apikey, Callback<List<Group>> callback);

}