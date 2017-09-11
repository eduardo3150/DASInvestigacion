package com.chavez.eduardo.dasinvestigacion.network;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Eduardo on 4/9/2017.
 */

public class DeveloperObject implements Serializable {
    @SerializedName("id")
    private int id;

    @SerializedName("game_developer")
    private String game_developer;

    public int getId() {
        return id;
    }

    public String getGame_developer() {
        return game_developer;
    }

    @Override
    public String toString() {
        return "\nDeveloperObject{" +
                "id=" + id +
                ", game_developer='" + game_developer + '\'' +
                '}';
    }
}
