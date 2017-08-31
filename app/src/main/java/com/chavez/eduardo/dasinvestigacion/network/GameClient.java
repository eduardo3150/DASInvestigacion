package com.chavez.eduardo.dasinvestigacion.network;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Eduardo on 27/8/2017.
 */

public interface GameClient {
    public static final String BASE_URL = "https://gitlab.com/snippets/";


    @GET("1673013/raw")
    Call<List<GameObject>> getGames();
}
