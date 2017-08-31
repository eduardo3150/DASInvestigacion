package com.chavez.eduardo.dasinvestigacion;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.chavez.eduardo.dasinvestigacion.utils.PrefKey;
import com.github.paolorotolo.appintro.AppIntro2;


public class AlternativeIntro extends AppIntro2 {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addSlide(SampleSlide.newInstance(R.layout.slide_intro_1));
        addSlide(SampleSlide.newInstance(R.layout.slide_intro_2));
        addSlide(SampleSlide.newInstance(R.layout.slide_intro_3));
        addSlide(SampleSlide.newInstance(R.layout.slide_intro_4));
        setFlowAnimation();
    }


    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        doNotShowItAgain();
        startActivity(new Intent(AlternativeIntro.this, LoginActivity.class));
        this.finish();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        doNotShowItAgain();
        startActivity(new Intent(AlternativeIntro.this, LoginActivity.class));
        this.finish();
    }

    private void doNotShowItAgain() {
        SharedPreferences sharedPreferences = getSharedPreferences(PrefKey.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(PrefKey.FIRST_BOOT, false);
        editor.apply();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.

    }
}
