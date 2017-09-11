package com.chavez.eduardo.dasinvestigacion.network;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

/**
 * Created by Eduardo on 27/8/2017.
 */

public interface GameClient {

    @GET("games")
    Call<List<GameObject>> getGames();

    @FormUrlEncoded
    @POST("games")
    Call<ResponseBody> addGame(@Field("game_name") String game_name,
                               @Field("game_description") String game_description,
                               @Field("developer_id") int developer_id,
                               @Field("release_date") String release_date,
                               @Field("website") String website,
                               @Field("platform") String platform,
                               @Field("category") String category);

    @GET("games/{id}")
    Call<List<GameObject>> getGame(@Path("id") String id);

    @Multipart
    @POST("games")
    Call<ResponseBody> addGame(@Part("game_name") RequestBody game_name,
                               @Part("game_description") RequestBody game_description,
                               @Part("developer_id") RequestBody game_developer,
                               @Part("release_date") RequestBody release_date,
                               @Part("website") RequestBody website,
                               @Part("platform") RequestBody platform,
                               @Part("category") RequestBody category,
                               @Part MultipartBody.Part cover_image,
                               @Part MultipartBody.Part thumb_image,
                               @Part MultipartBody.Part game_image);


    @PUT("games/{id}")
    @FormUrlEncoded
    Call<GameObject> updateGame(@Path("id") String id,
                                @Field("game_name") String game_name,
                                @Field("game_description") String game_description,
                                @Field("developer_id") int developer_id,
                                @Field("release_date") String release_date,
                                @Field("website") String website,
                                @Field("platform") String platform,
                                @Field("category") String category,
                                @Field("cover_image") String cover_image,
                                @Field("thumb_image") String thumb_image,
                                @Field("game_image") String game_image);


    @DELETE("games/{id}")
    Call<ResponseBody> deleteGame(@Path("id") String id);

    @GET("developers")
    Call<List<DeveloperObject>> getDevelopers();


    @GET("statistics")
    Call<List<StatisticsObject>> getStatistics();

    @FormUrlEncoded
    @POST("developers")
    Call<ResponseBody> addDeveloper(@Field("game_developer") String game_developer);

    @DELETE("developers/{id}")
    Call<ResponseBody> deleteDeveloper(@Path("id") String id);

    @PUT("developers/{id}")
    @FormUrlEncoded
    Call<DeveloperObject> updateDeveloper(@Path("id") String id,
                                          @Field("game_developer") String game_developer);
}
