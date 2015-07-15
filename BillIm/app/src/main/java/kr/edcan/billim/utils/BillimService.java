package kr.edcan.billim.utils;

import java.util.List;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.mime.TypedFile;

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

    @Multipart
    @POST("/api/article/self/create")
    public void postArticle(@Part("apikey") String apikey, @Part("group") int group, @Part("type") int type, @Part("category") int category,
                            @Part("name") String name, @Part("description") String description, @Part("reward") String reward,
                            @Part("location") String location, @Part("photo") TypedFile photo, Callback<Article> callback);
    @FormUrlEncoded
    @POST("/api/article/self/info")
    public void getArticle(@Field("apikey") String apikey, @Field("id") int id, Callback<Article> callback);

    @FormUrlEncoded
    @POST("/api/group/self/part")
    public void leaveGroup(@Field("apikey") String apikey, @Field("id") int id, Callback<Article> callback);

    @FormUrlEncoded
    @POST("/api/group/search")
    public void searchGroup(@Field("name") String name, Callback<List<Group>> callback);

    @FormUrlEncoded
    @POST("/api/group/self/join")
    public void joinGroup(@Field("apikey") String apikey, @Field("id") int id, Callback<GroupResponse> callback);

    @FormUrlEncoded
    @POST("/api/group/self/create")
    public void createGroup(@Field("apikey") String apikey, @Field("name") String name, Callback<GroupResponse> callback);

    @FormUrlEncoded
    @POST("/api/user/self/delete")
    public void deleteAccount(@Field("apikey") String apikey, Callback callback);

    @FormUrlEncoded
    @POST("/api/article/self/list")
    public void articleSelfList(@Field("apikey") String apikey, @Field("group") int id, Callback<List<Article>> callback);

    @FormUrlEncoded
    @POST("/api/article/self/confirm")
    public void changePostState(@Field("apikey") String apikey, @Field("id") int id, Callback<List<Article>> callback);


}