package com.chavez.eduardo.dasinvestigacion.network;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Eduardo on 4/9/2017.
 */

public class StatisticsObject implements Serializable {
    @SerializedName("game_developer")
    private String developer_name;

    @SerializedName("game_number")
    private int game_number;

    public String getDeveloper_name() {
        return developer_name;
    }

    public int getGame_number() {
        return game_number;
    }

    @Override
    public String toString() {
        return "\nStatisticsObject{" +
                "developer_name='" + developer_name + '\'' +
                ", game_number=" + game_number +
                '}';
    }
}
