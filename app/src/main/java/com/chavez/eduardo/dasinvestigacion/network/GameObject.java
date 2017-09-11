package com.chavez.eduardo.dasinvestigacion.network;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Eduardo on 27/8/2017.
 */

public class GameObject implements Serializable {
    /**
     * {
     * "id": 1,
     * "game_name":  "Red Dead Redemption 2",
     * "game_description": "Desarrollado por Rockstar, creadores de GTA V y Red Dead Redemption, Red Dead Redemption 2 es un videojuego de mundo abierto ambientado en el corazón de América. Una aventura western con una extraordinaria atmósfera y ambientación muy cuidada, que además de modo individual de juego también presenta multijugador online. RDR 2 es una epopeya de vaqueros o western sólo en su envoltorio, pues como en todo juego de Rockstar, en su interior nos cuenta una hermosa y triste historia sobre gente que intenta cambiar y no siempre puede.",
     * "game_developer": "RockStar Games",
     * "release_date": "Octubre de 2017",
     * "website": "http://www.rockstargames.com/reddeadredemption2/",
     * "platform": "Playstation 4, XBOX ONE",
     * "cover_image": "https://i11b.3djuegos.com/juegos/13424/red_dead_3__nombre_temporal_/fotos/ficha/red_dead_3__nombre_temporal_-3542549.jpg",
     * "thumb_image": "http://www.journaldugamer.com/files/2016/10/RDR2.jpg",
     * "game_image":"http://cdn-static.denofgeek.com/sites/denofgeek/files/2017/01/red-dead-redemption-2-ps4_0.jpg"
     * }
     */

    @SerializedName("id")
    private int id;

    @SerializedName("game_name")
    private String game_name;

    @SerializedName("game_description")
    private String game_description;

    @SerializedName("game_developer")
    private String game_developer;

    @SerializedName("release_date")
    private String release_date;

    @SerializedName("website")
    private String website;

    @SerializedName("platform")
    private String platform;

    @SerializedName("category")
    private String category;

    @SerializedName("cover_image")
    private String cover_image;

    @SerializedName("thumb_image")
    private String thumb_image;

    @SerializedName("game_image")
    private String game_image;

    public int getId() {
        return id;
    }

    public String getGame_name() {
        return game_name;
    }

    public String getGame_description() {
        return game_description;
    }

    public String getGame_developer() {
        return game_developer;
    }

    public String getRelease_date() {
        return release_date;
    }

    public String getWebsite() {
        return website;
    }

    public String getPlatform() {
        return platform;
    }

    public String getCover_image() {
        return cover_image;
    }

    public String getThumb_image() {
        return thumb_image;
    }

    public String getGame_image() {
        return game_image;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "GameObject{" +
                "id=" + id +
                ", game_name='" + game_name + '\'' +
                ", game_description='" + game_description + '\'' +
                ", game_developer='" + game_developer + '\'' +
                ", release_date='" + release_date + '\'' +
                ", website='" + website + '\'' +
                ", platform='" + platform + '\'' +
                ", category='" + category + '\'' +
                ", cover_image='" + cover_image + '\'' +
                ", thumb_image='" + thumb_image + '\'' +
                ", game_image='" + game_image + '\'' +
                '}';
    }
}
